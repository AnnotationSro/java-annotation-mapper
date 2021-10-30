package sk.annotation.library.jam.processor;

import com.sun.tools.javac.code.Symbol;
import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.processor.data.MapperClassInfo;
import sk.annotation.library.jam.processor.sourcewriter.JavaClassWriter;
import sk.annotation.library.jam.processor.utils.ElementUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.InputStream;
import java.util.*;


//@AutoService(Processor.class)
public class AnnotationJamMapperProcessor extends AbstractProcessor {

    private Set<String> supportedAnnotationTypes = null;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
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

    protected static final String version = getVersion();
    protected static String getVersion() {
        try {
            InputStream is = AnnotationJamMapperProcessor.class.getResourceAsStream("/META-INF/maven/sk.annotation.library.jam/jam-processor/pom.properties");
            if (is != null) {
                Properties p = new Properties();
                p.load(is);
                String version = p.getProperty("version");
                if (version != null && version.trim().length()>0) {
                    return " (version= " + version + ")";
                }
            }
        }
        catch (Exception e) {
            // ignore error
        }
        return "";
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (firstRun) {
            firstRun = false;
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Annotation processor " + this.getClass().getCanonicalName() + version + " - started!");
        }

        Set<String> newValues = new HashSet<>();

        // 1) Find All Mappers
        for (Element element : roundEnv.getElementsAnnotatedWith(Mapper.class)) {
            if (element instanceof TypeElement) {
                String key = ElementUtils.getQualifiedName(element);
                newValues.add(key);
                this.foundMappersPerGeneratedState.putIfAbsent(key, false);
                continue;
            }

            if (element instanceof Symbol.MethodSymbol) {
                Symbol owner = ((Symbol.MethodSymbol) element).owner;
                String key = ElementUtils.getQualifiedName(owner);
                newValues.add(key);
                this.foundMappersPerGeneratedState.putIfAbsent(key, false);
                continue;
            }

            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Annotation processor " + this.getClass().getCanonicalName() + version + " - found unsupported element type " + element);
        }

        // 2) generate mappers:
        for (Map.Entry<String, Boolean> e : foundMappersPerGeneratedState.entrySet()) {
            // ignore generated ...
            if (e.getValue()) {
                if (newValues.contains(e.getKey())) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "Was generated : " + e.getKey());
                }
                continue;
            }

            Element rootElement = ElementUtils.findRootElementByQualifiedName(roundEnv, e.getKey());
            if (rootElement != null) {

                TypeElement typeElementMapper = processingEnv.getElementUtils().getTypeElement(e.getKey());
                if (typeElementMapper != null) {
                    generateMapper(typeElementMapper);
                    e.setValue(true);
                    continue;
                }

            }
            processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "Cannot generate : " + e.getKey());
        }


        // Claiming that annotations have been processed by this processor
        return true;
    }

    protected void generateMapper(TypeElement element) {
        MapperClassInfo mapperInfo = MapperClassInfo.getOrCreate(processingEnv, element);
        if (mapperInfo == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "Cannot create MapperClassInfo : " + ElementUtils.getQualifiedName(element));
            return;
        }

        JavaClassWriter javaClassWriter = new JavaClassWriter(mapperInfo);
        javaClassWriter.writeSourceCode(processingEnv);
    }
}
