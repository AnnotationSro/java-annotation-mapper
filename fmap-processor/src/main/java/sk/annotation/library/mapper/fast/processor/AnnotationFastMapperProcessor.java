package sk.annotation.library.mapper.fast.processor;

import com.google.auto.service.AutoService;
import com.sun.tools.javac.code.Symbol;
import sk.annotation.library.mapper.fast.annotations.FastMapper;
import sk.annotation.library.mapper.fast.annotations.FastMapperGenerated;
import sk.annotation.library.mapper.fast.processor.data.MapperClassInfo;
import sk.annotation.library.mapper.fast.processor.sourcewriter.JavaClassWriter;
import sk.annotation.library.mapper.fast.processor.utils.ElementUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.*;



@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AnnotationFastMapperProcessor extends AbstractProcessor {

	private Set<String> supportedAnnotationTypes = null;

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		if (supportedAnnotationTypes == null) {
			Set<String> set = new LinkedHashSet<>();
			set.add(FastMapper.class.getCanonicalName());
			set.add(FastMapperGenerated.class.getCanonicalName());
			supportedAnnotationTypes = Collections.unmodifiableSet(set);
		}
		return supportedAnnotationTypes;
	}

	private Map<String, Boolean> foundMappersPerGeneratedState = new LinkedHashMap<>();

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		// 1) Find All Mappers
		for (Element element : roundEnv.getElementsAnnotatedWith(FastMapper.class)) {
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
					generateMapper(e.getKey(), typeElementMapper);
					e.setValue(true);
					continue;
				}
			}
		}


		// Claiming that annotations have been processed by this processor
		return true;
	}

	protected void generateMapper(String fullName, TypeElement element) {

		MapperClassInfo mapperInfo = MapperClassInfo.getOrCreate(processingEnv, element);
		if (mapperInfo == null) return;

		JavaClassWriter javaClassWriter = new JavaClassWriter(mapperInfo);
		javaClassWriter.writeSourceCode(processingEnv);
	}
}
