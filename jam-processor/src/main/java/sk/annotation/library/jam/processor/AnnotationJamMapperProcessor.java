package sk.annotation.library.jam.processor;

import com.google.auto.service.AutoService;
import com.sun.tools.javac.code.Symbol;
import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.processor.data.MapperClassInfo;
import sk.annotation.library.jam.processor.sourcewriter.JavaClassWriter;
import sk.annotation.library.jam.processor.utils.ElementUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.*;


@AutoService(Processor.class)
public class AnnotationJamMapperProcessor extends AbstractProcessor {

    private Set<String> supportedAnnotationTypes = null;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        if (supportedAnnotationTypes == null) {
            Set<String> set = new LinkedHashSet<>();
            set.add(Mapper.class.getCanonicalName());
//            set.add(JamGenerated.class.getCanonicalName());
            supportedAnnotationTypes = Collections.unmodifiableSet(set);
        }
        return supportedAnnotationTypes;
    }

    private Map<String, Boolean> foundMappersPerGeneratedState = new LinkedHashMap<>();

    private boolean firstRun = true;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (firstRun) {
            firstRun = false;
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Annotation processor " + this.getClass().getCanonicalName() + " - started!");
        }


        // 1) Find All Mappers
        for (Element element : roundEnv.getElementsAnnotatedWith(Mapper.class)) {
            if (element instanceof TypeElement) {
                this.foundMappersPerGeneratedState.putIfAbsent(ElementUtils.getQualifiedName(element), false);
                continue;
            }

            if (element instanceof Symbol.MethodSymbol) {
                Symbol owner = ((Symbol.MethodSymbol) element).owner;
                this.foundMappersPerGeneratedState.putIfAbsent(ElementUtils.getQualifiedName(owner), false);
                continue;
            }
        }

        // 2) generate mappers:
        for (Map.Entry<String, Boolean> e : foundMappersPerGeneratedState.entrySet()) {
            // ignore generated ...
            if (e.getValue()) continue;

            Element rootElement = ElementUtils.findRootElementByQualifiedName(roundEnv, e.getKey());
            if (rootElement != null) {

                TypeElement typeElementMapper = processingEnv.getElementUtils().getTypeElement(e.getKey());
                if (typeElementMapper != null) {
                    generateMapper(typeElementMapper);
                    e.setValue(true);
                    continue;
                }
            }
        }


        // Claiming that annotations have been processed by this processor
        return true;
    }

    static public Map<String, Boolean> cache = new HashMap<>();
    protected void generateMapper(TypeElement element) {
        String fullNamePath = ElementUtils.getQualifiedName(element);
        if (cache.containsKey(fullNamePath)) return;
        cache.put(fullNamePath, false);

        MapperClassInfo mapperInfo = MapperClassInfo.getOrCreate(processingEnv, element);
        if (mapperInfo == null) return;

        JavaClassWriter javaClassWriter = new JavaClassWriter(mapperInfo);
        javaClassWriter.writeSourceCode(processingEnv);

        cache.put(fullNamePath, true);
    }
}
