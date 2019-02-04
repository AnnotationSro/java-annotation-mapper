package sk.annotation.library.mapper.fast.processor.utils;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import org.apache.commons.lang.StringUtils;
import sk.annotation.library.mapper.fast.processor.data.MethodParamInfo;
import sk.annotation.library.mapper.fast.processor.data.TypeInfo;
import sk.annotation.library.mapper.fast.processor.data.mapi.MethodApiFullSyntax;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

abstract public class TypeUtils {
	static private String resolveConstructorName(String intName) {
		if (Iterable.class.getCanonicalName().equals(intName)) return ArrayList.class.getCanonicalName();
		else if (Collection.class.getCanonicalName().equals(intName)) return ArrayList.class.getCanonicalName();
		else if (List.class.getCanonicalName().equals(intName)) return ArrayList.class.getCanonicalName();
		else if (Set.class.getCanonicalName().equals(intName)) return HashSet.class.getCanonicalName();
		else if (SortedSet.class.getCanonicalName().equals(intName)) return TreeSet.class.getCanonicalName();
		else if (NavigableSet.class.getCanonicalName().equals(intName)) return TreeSet.class.getCanonicalName();
		else if (Map.class.getCanonicalName().equals(intName)) return LinkedHashMap.class.getCanonicalName();
		else if (SortedMap.class.getCanonicalName().equals(intName)) return TreeMap.class.getCanonicalName();
		else if (NavigableMap.class.getCanonicalName().equals(intName)) return TreeMap.class.getCanonicalName();
		else if (ConcurrentMap.class.getCanonicalName().equals(intName)) return ConcurrentHashMap.class.getCanonicalName();
		else if (ConcurrentNavigableMap.class.getCanonicalName().equals(intName)) return ConcurrentSkipListMap.class.getCanonicalName();

		return null;
	}
	static public TypeInfo resolveConstructorType(ProcessingEnvironment processingEnv, TypeInfo type) {
		return new TypeInfo(_resolveConstructorType(processingEnv, type.getType(processingEnv)));
	}
	static public TypeMirror _resolveConstructorType(ProcessingEnvironment processingEnv, TypeMirror type) {
		// List|Collection -> LinkedList or ArrayList
		if (type == null) return type;

		if (type instanceof Type.ClassType) {
			Type.ClassType tp = (Type.ClassType) type;
			String implName = resolveConstructorName(tp.tsym.toString());

			if (implName == null) return type;

			TypeElement typeElement = processingEnv.getElementUtils().getTypeElement(implName);

			if (tp.allparams()!=null && !tp.allparams().isEmpty()) {
				return processingEnv.getTypeUtils().getDeclaredType(typeElement, tp.allparams().toArray(new TypeMirror[0]));
			}
			return typeElement.asType();
		}

		String newType = resolveConstructorName(type.toString());
		if (newType==null) return type;
		return processingEnv.getElementUtils().getTypeElement(newType).asType();
	}

	static public String getClassSimpleName(TypeMirror type) {
		if (type instanceof Type.ClassType) {
			Type.ClassType tp = (Type.ClassType) type;
			return tp.tsym.getSimpleName().toString();
		}
		if (type instanceof Symbol.TypeSymbol) {
			Symbol.TypeSymbol tp = (Symbol.TypeSymbol) type;
			return tp.getSimpleName().toString();
		}
		return "";
	}
	static public Type getBaseTypeWithoutParametrizedFields(TypeMirror type) {
		if (type instanceof Type.ClassType) {
			Type.ClassType tp = (Type.ClassType) type;
			return tp.tsym.asType();
		}
		if (type instanceof Symbol.TypeSymbol) {
			Symbol.TypeSymbol tp = (Symbol.TypeSymbol) type;
			return tp.asType();
		}
		if (type instanceof Type) return (Type) type;
		return null;
	}
	static public List<Type> getParametrizedTypes(TypeMirror type) {
		if (type instanceof Type.ClassType) {
			Type.ClassType tp = (Type.ClassType) type;
			return tp.allparams();
		}
		return null;
	}
	static public boolean isSameMethodInputs(ProcessingEnvironment processingEnv, List<MethodParamInfo> inputs1, MethodApiFullSyntax syntaxMethod) {
		if (syntaxMethod == null) return false;
		return isSameMethodInputs(processingEnv, inputs1, syntaxMethod.getParams());
	}
	static public boolean isSameMethodInputs(ProcessingEnvironment processingEnv, List<MethodParamInfo> inputs1, List<MethodParamInfo> inputs2) {
		if (inputs1 == null && inputs2==null) return true;
		if (inputs1 == null || inputs2==null) return false;

		if (inputs1.size()!=inputs2.size()) return false;


		for (int i = 0; i < inputs1.size(); i++) {
			MethodParamInfo param1 = inputs1.get(i);
			MethodParamInfo param2 = inputs2.get(i);
			if (!isSame(processingEnv, param1.getVariable().getType(), param2.getVariable().getType())) return false;
		}
		return true;
	}



	//  @return {@code true} if and only if the first type is d subtype of the second
	static public boolean isSame(ProcessingEnvironment processingEnv, TypeInfo type1, TypeInfo type2) {
		if (type1 == null && type2 == null) return true;
		if (type1 == null || type2 == null) return false;
		return processingEnv.getTypeUtils().isSameType(type1.getType(processingEnv), type2.getType(processingEnv));
	}
	//@return {@code true} if and only if the first type is assignable to the second
	static public boolean isAssignable(ProcessingEnvironment processingEnv, TypeInfo type1, TypeInfo type2) {
		if (type1 == null && type2 == null) return true;
		if (type1 == null || type2 == null) return false;
		return processingEnv.getTypeUtils().isAssignable(type1.getType(processingEnv), type2.getType(processingEnv));
	}

	static public Type findType(VariableElement element) {
		if (element == null) return null;
		if (element instanceof Symbol) return ((Symbol)element).type;

		throw new IllegalStateException("Not Implemented yet!!!");
	}
	static public Type convertToType(ProcessingEnvironment processingEnv, String className) {
		if (className == null) return null;
		try {
			return (Type) processingEnv.getElementUtils().getTypeElement(className).asType();
		}
		catch (Exception e) {
			return null;
		}
	}
	static public Type convertToType(ProcessingEnvironment processingEnv, Class cls) {
		return convertToType(processingEnv, cls.getCanonicalName());
	}
	static public TypeInfo convertToDefinitionType(ProcessingEnvironment processingEnv, Class cls) {
		TypeMirror typeMirror = convertToType(processingEnv, cls);
		if (typeMirror == null) return null;
		return new TypeInfo(typeMirror);
	}

	static public String findPackageName(TypeMirror typeMirror) {
		Type.PackageType pck = findPackageType(typeMirror);
		if (pck == null) return null;
		return pck.toString();
	}
	static public Type.PackageType findPackageType(TypeMirror typeMirror) {
		Type topElementType = findTopElementType(typeMirror);
		if (topElementType == null) return null;

		Symbol parent = topElementType.asElement().owner;
		if (parent == null) return null;
		Type parentType = parent.asType();
		if (parentType instanceof Type.PackageType) return (Type.PackageType) parentType;
		return null;
	}

	static public Type findTopElementType(TypeMirror typeMirror) {
		if (typeMirror == null) return null;
		if (typeMirror.getKind() != TypeKind.DECLARED) return null;
		if (!(typeMirror instanceof Type)) return null;

		Type typeElement = (Type) typeMirror;
		Symbol.TypeSymbol typeSymbol = typeElement.asElement();
		if (typeSymbol == null) return null;

		Type parentType = findTopElementType(typeSymbol.owner.asType());
		if (parentType!=null) return parentType;

		return typeElement;
	}


	private static Class[] cls1 = {java.lang.Enum.class, java.lang.Number.class};
	private static Class[] cls2 = {java.lang.String.class, java.lang.Boolean.class, java.lang.Character.class};
	private static List<TypeMirror> _cls1 = null;
	private static List<TypeMirror> _cls2 = null;

	static public boolean isBaseOrPrimitiveType(ProcessingEnvironment processingEnv, TypeMirror source) {
		if (source.getKind().isPrimitive()) return true;

		if (_cls1 == null) {
			_cls1 = new ArrayList<>(cls1.length);
			_cls2 = new ArrayList<>(cls2.length);
			for (Class aClass : cls1) {
				_cls1.add(convertToType(processingEnv, aClass));
			}
			for (Class aClass : cls2) {
				_cls2.add(convertToType(processingEnv, aClass));
			}
		}

		// same
		for (TypeMirror typeMirror : _cls2) {
			if (processingEnv.getTypeUtils().isSameType(source, typeMirror)) return true;
		}

		for (TypeMirror typeMirror : _cls1) {
			if (processingEnv.getTypeUtils().isAssignable(source, typeMirror)) return true;
			if (processingEnv.getTypeUtils().isSubtype(source, typeMirror)) return true;
		}

		return false;
	}
}
