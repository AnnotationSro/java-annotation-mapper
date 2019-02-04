package sk.annotation.library.mapper.fast.processor.utils;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import org.apache.commons.lang.StringUtils;
import sk.annotation.library.mapper.fast.annotations.FastMapper;
import sk.annotation.library.mapper.fast.processor.data.confwrappers.FieldValueAccessData;
import sk.annotation.library.mapper.fast.processor.data.mapi.MethodApiFullSyntax;
import sk.annotation.library.mapper.fast.utils.context.MapperUtil;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.lang.annotation.Annotation;
import java.util.*;

abstract public class ElementUtils {

	static public Element findRootElementByQualifiedName(RoundEnvironment roundEnv, String fullName) {
		for (Element element : roundEnv.getRootElements()) {
			String name = ElementUtils.getQualifiedName(element);
			if (Objects.equals(name, fullName)) {
				return element;
			}
		}
		return null;
	}


	static public String getQualifiedName(Element element) {
		if (element == null) return null;

		if (element instanceof TypeElement) {
			TypeElement typeElement = (TypeElement) element;
			return typeElement.getQualifiedName() + "";
		}

		if (element instanceof Symbol) {
			Symbol symbol = (Symbol) element;
			return symbol.getQualifiedName() + "";
		}

		return null;
	}

	static public Symbol.PackageSymbol findPackageElementType(ProcessingEnvironment processingEnv, Element element) {
		if (element == null) return null;
		if (element.asType() == null) return null;
		if (element.getKind() == ElementKind.PACKAGE) {
			TypeMirror typeMirror = element.asType();
			if (typeMirror instanceof Type.PackageType) {
				return (Symbol.PackageSymbol) (((Type.PackageType) element.asType()).tsym);
			}
			if (typeMirror instanceof Symbol.PackageSymbol) {
				return (Symbol.PackageSymbol) element.asType();
			}
		}
		return findPackageElementType(processingEnv, element.getEnclosingElement());
	}

	static public <T extends Annotation> List<T> findAllAnnotationsInStructure(ProcessingEnvironment processingEnv, Element element, Class<T> annotationType) {
		List<T> ret = new LinkedList<>();
		T conf = element.getAnnotation(annotationType);
		if (conf != null) ret.add(conf);


		// Scan packages ...
		for (
				Symbol.PackageSymbol pckType = findPackageElementType(processingEnv, element);
				pckType != null;
				pckType = (Symbol.PackageSymbol) pckType.owner
		) {
			conf = pckType.getAnnotation(annotationType);
			if (conf != null) ret.add(conf);
		}

		return ret;
	}

// Working, but not important now
//	static public TypeElement findTopElementType(ProcessingEnvironment processingEnv, Element element) {
//		if (element == null) return null;
//		if (element.asType() == null) return null;
//		if (element.getKind() == ElementKind.PACKAGE) return null;
//		if (!(element instanceof TypeElement)) return null;
//
//		TypeElement typeElement = (TypeElement) element;
//		if (!typeElement.getNestingKind().isNested()) return typeElement;
//
//		return findTopElementType(processingEnv, element.getEnclosingElement());
//	}


	//	static public boolean hasDefaultConstructor(ProcessingEnvironment processingEnv, String fullClassName) {
//		TypeElement typeElementMapper = processingEnv.getElementUtils().getTypeElement(fullClassName);
//		if (typeElementMapper == null) return false;
//
//		List<? extends Element> allElements = typeElementMapper.getEnclosedElements();
//		for (ExecutableElement el : ElementFilter.constructorsIn(allElements)) {
//			if (el.getParameters().isEmpty()) return true;
//		}
//
//		return false;
//	}
	static public String findGeneratedMapperClass(ProcessingEnvironment processingEnv, String fullClassName) {
		TypeElement typeElementMapper = processingEnv.getElementUtils().getTypeElement(fullClassName);
		if (typeElementMapper == null) return null;

		if (typeElementMapper.getAnnotation(FastMapper.class) != null)
			return fullClassName + MapperUtil.constPostFixClassName;

		// este treba najst vsetky metody d ich annotacie
		List<? extends Element> allElements = typeElementMapper.getEnclosedElements();
		for (ExecutableElement el : ElementFilter.constructorsIn(allElements)) {
			if (el.getAnnotation(FastMapper.class) == null) continue;

			return fullClassName + MapperUtil.constPostFixClassName;
		}

		return null;
	}

	protected static Set<Element> exclusions = null;

	public static List<? extends Element> findAllAcceptedMember(ProcessingEnvironment processingEnv, TypeElement element) {
		if (exclusions == null) {
			exclusions = new HashSet<>();
			TypeElement objectElement = processingEnv.getElementUtils().getTypeElement(Object.class.getName());
			exclusions.addAll(ElementFilter.methodsIn(objectElement.getEnclosedElements()));
		}

		List<? extends Element> allMembers = processingEnv.getElementUtils().getAllMembers(element);
		List<Element> allAcceptedMembers = new ArrayList<>(allMembers.size());
		for (Element member : allMembers) {
			if (exclusions.contains(member)) continue;
			allAcceptedMembers.add(member);
		}
		return allAcceptedMembers;
	}


	static private final Map<Type, Map<String, FieldValueAccessData>> cachedValues = new HashMap<>();
	public static Map<String, FieldValueAccessData> findAllAccesableFields(ProcessingEnvironment processingEnv, Type typeFrom) {
		if (typeFrom == null || typeFrom.getKind().isPrimitive()) {
			return Collections.emptyMap();
		}

		return cachedValues.computeIfAbsent(typeFrom, (aaa) -> {
			List<? extends Element> allMembers = ElementUtils.findAllAcceptedMember(processingEnv, (TypeElement) typeFrom.asElement());
			Map<String, FieldValueAccessData> ret = new HashMap<>();

			for (Element member : allMembers) {
				String name = member.getSimpleName().toString();

				if (member instanceof VariableElement) {
					ret.computeIfAbsent(name, FieldValueAccessData::new).setField((VariableElement) member);
					continue;
				}

				if (member instanceof ExecutableElement) {
					ExecutableElement method = (ExecutableElement) member;

					// Check SETTER
					if (StringUtils.startsWith(name, "set")) {
						MethodApiFullSyntax methodSyntax = MethodApiFullSyntax.analyze(processingEnv, method);
						if (methodSyntax.getParams().size() != 1) continue;
						if (methodSyntax.getReturnType() != null) continue;

						name = StringUtils.uncapitalize(name.substring(3));
						ret.computeIfAbsent(name, FieldValueAccessData::new).setSetter(method);

						continue;
					}

					// Check GETTER
					String getterForField = null;
					if (StringUtils.startsWith(name, "get")) {
						getterForField = StringUtils.uncapitalize(name.substring(3));
					} else if (StringUtils.startsWith(name, "is")) {
						getterForField = StringUtils.uncapitalize(name.substring(2));
					}

					if (getterForField != null) {
						MethodApiFullSyntax methodSyntax = MethodApiFullSyntax.analyze(processingEnv, method);
						if (methodSyntax.getParams().size() != 0) continue;
						if (methodSyntax.getReturnType() == null) continue;

						ret.computeIfAbsent(getterForField, FieldValueAccessData::new).setGetter(method);

						continue;
					}
				}

			}

			return ret;
		});
	}
}
