package sk.annotation.library.jam.processor.utils;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import org.apache.commons.lang.StringUtils;
import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.processor.data.confwrappers.FieldValueAccessData;
import sk.annotation.library.jam.processor.data.mapi.MethodApiFullSyntax;
import sk.annotation.library.jam.utils.MapperUtil;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.SimpleElementVisitor6;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

	static public Element findParentElement(ProcessingEnvironment processingEnv, Element element) {
		if (element == null) return null;
		if (element.asType() == null) return null;
		if (element.getKind() == ElementKind.PACKAGE) {
			TypeMirror typeMirror = element.asType();
			if (typeMirror instanceof Type.PackageType) {
				return (((Type.PackageType) element.asType()).tsym).owner;
			}
			if (typeMirror instanceof Symbol.PackageSymbol) {
				return ((Symbol.PackageSymbol) element.asType()).owner;
			}
		}
		return element.getEnclosingElement();
	}

	static public <T extends Annotation> List<Element> findAllElementsWithAnnotationsInStructure(ProcessingEnvironment processingEnv, Element element, Class<T> annotationType) {
		return findAllElementsWithAnnotationsInStructure(processingEnv, element, e ->
			e !=null && e.getAnnotation(annotationType)!=null
		);
	}
	static public <T extends Annotation> List<Element> findAllElementsWithAnnotationsInStructure(ProcessingEnvironment processingEnv, Element element, Function<Element, Boolean> accept) {
		List<Element> ret = new LinkedList<>();

		// Scan owner class and packages ...
		for (
				Element parentElement = element;
				parentElement != null;
				parentElement = findParentElement(processingEnv, parentElement)
		) {
			if (accept.apply(parentElement)) ret.add(element);
		}

		return ret;
	}
	static public <T extends Annotation> List<T> findAllAnnotationsInStructure(ProcessingEnvironment processingEnv, Element element, Class<T> annotationType) {
		return findAllElementsWithAnnotationsInStructure(processingEnv, element, annotationType).stream().map(e -> e.getAnnotation(annotationType)).collect(Collectors.toList());
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

		if (typeElementMapper.getAnnotation(Mapper.class) != null)
			return fullClassName + MapperUtil.constPostFixClassName;

		// Its neccessary to find all methods and their annotations yet
		List<? extends Element> allElements = typeElementMapper.getEnclosedElements();
		for (ExecutableElement el : ElementFilter.constructorsIn(allElements)) {
			if (el.getAnnotation(Mapper.class) == null) continue;

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

		List<Element> allAcceptedMembers = new LinkedList<>();
		addAcceptedMembers(processingEnv, allAcceptedMembers, element);
		return allAcceptedMembers;
	}
	private static void addAcceptedMembers(ProcessingEnvironment processingEnv, List<Element> allAcceptedMembers, TypeElement element) {
		if (element == null) return;

		List<? extends Element> allMembers = processingEnv.getElementUtils().getAllMembers(element);
		for (Element member : allMembers) {
			if (exclusions.contains(member)) continue;
			allAcceptedMembers.add(member);
		}

		if (element.getSuperclass() instanceof Type.ClassType) {
			Type.ClassType superClass = (Type.ClassType) element.getSuperclass();
			if (TypeUtils.isSame(processingEnv, superClass, TypeUtils.convertToType(processingEnv, Object.class))) return;
			if (Object.class.getCanonicalName().equals(superClass.toString())) return;

			addAcceptedMembers(processingEnv, allAcceptedMembers, (TypeElement)(superClass).asElement());
		}
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
				if (member.getModifiers().contains(Modifier.STATIC)) continue;

				if (member instanceof VariableElement) {
					ret.computeIfAbsent(name, FieldValueAccessData::new).setField(processingEnv, typeFrom, (VariableElement) member);
					continue;
				}

				if (member instanceof ExecutableElement) {
					ExecutableElement method = (ExecutableElement) member;

					// Check SETTER
					if (StringUtils.startsWith(name, "set")) {
						MethodApiFullSyntax methodSyntax = MethodApiFullSyntax.analyze(processingEnv, typeFrom, method);
						if (methodSyntax == null || !methodSyntax.getErrorsMapping().isEmpty()) {
							// Ignore bad API
							processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, methodSyntax.getErrorsMapping().toString(), method);
							continue;
						}
						if (methodSyntax.getParams().size() != 1) continue;
						if (methodSyntax.getReturnType() != null) continue;

						name = StringUtils.uncapitalize(name.substring(3));
						ret.computeIfAbsent(name, FieldValueAccessData::new).setSetter(processingEnv, typeFrom, method);

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
						MethodApiFullSyntax methodSyntax = MethodApiFullSyntax.analyze(processingEnv, typeFrom, method);
						if (methodSyntax == null || !methodSyntax.getErrorsMapping().isEmpty()) {
							// Ignore bad API
							processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, methodSyntax.getErrorsMapping().toString(), method);
							continue;
						}
						if (methodSyntax.getParams().size() != 0) continue;
						if (methodSyntax.getReturnType() == null) continue;

						ret.computeIfAbsent(getterForField, FieldValueAccessData::new).setGetter(processingEnv, typeFrom, method);

						continue;
					}
				}

			}

			return ret;
		});
	}
}
