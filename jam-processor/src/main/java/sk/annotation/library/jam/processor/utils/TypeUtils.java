package sk.annotation.library.jam.processor.utils;

import sk.annotation.library.jam.processor.data.TypeInfo;
import sk.annotation.library.jam.processor.data.TypeWithVariableInfo;
import sk.annotation.library.jam.processor.data.mapi.MethodApiFullSyntax;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

abstract public class TypeUtils {
    static private String resolveConstructorName(String intName) {
        if (Iterable.class.getCanonicalName().equals(intName)) return ArrayList.class.getCanonicalName();
        else if (Collection.class.getCanonicalName().equals(intName)) return ArrayList.class.getCanonicalName();
        else if (List.class.getCanonicalName().equals(intName)) return ArrayList.class.getCanonicalName();
        else if (Set.class.getCanonicalName().equals(intName)) return LinkedHashSet.class.getCanonicalName();
        else if (SortedSet.class.getCanonicalName().equals(intName)) return TreeSet.class.getCanonicalName();
        else if (NavigableSet.class.getCanonicalName().equals(intName)) return TreeSet.class.getCanonicalName();
        else if (Map.class.getCanonicalName().equals(intName)) return LinkedHashMap.class.getCanonicalName();
        else if (SortedMap.class.getCanonicalName().equals(intName)) return TreeMap.class.getCanonicalName();
        else if (NavigableMap.class.getCanonicalName().equals(intName)) return TreeMap.class.getCanonicalName();
        else if (ConcurrentMap.class.getCanonicalName().equals(intName))
            return ConcurrentHashMap.class.getCanonicalName();
        else if (ConcurrentNavigableMap.class.getCanonicalName().equals(intName))
            return ConcurrentSkipListMap.class.getCanonicalName();

        return null;
    }

    static public TypeInfo resolveConstructorType(ProcessingEnvironment processingEnv, TypeInfo type) {
        return new TypeInfo(_resolveConstructorType(processingEnv, type.getType(processingEnv)));
    }

    static public TypeMirror _resolveConstructorType(ProcessingEnvironment processingEnv, TypeMirror type) {
        // List|Collection -> LinkedList or ArrayList
        if (type == null) return type;
        if (isArrayType(processingEnv, type)) {
            ArrayType arrayType = (ArrayType) type;
            return arrayType.getComponentType();
        }

        if (type instanceof DeclaredType) {
            DeclaredType tp = (DeclaredType) type;
            String implName = resolveConstructorName(tp.asElement().toString());
            if (implName == null) return type;

            TypeElement typeElement = processingEnv.getElementUtils().getTypeElement(implName);
            List<? extends TypeMirror> typeArguments = tp.getTypeArguments();
            if (typeArguments != null && !typeArguments.isEmpty()) {
                return processingEnv.getTypeUtils().getDeclaredType(typeElement, typeArguments.toArray(new TypeMirror[0]));
            }

            return typeElement.asType();
        }

        if (type instanceof javax.lang.model.type.WildcardType) {
            return _resolveConstructorType(processingEnv, unwrapWildCardType(type));
        }

        String newType = resolveConstructorName(type.toString());
        if (newType == null) return type;
        return processingEnv.getElementUtils().getTypeElement(newType).asType();
    }


    static public String getClassSimpleName(TypeMirror type) {
        if (type instanceof DeclaredType) {
            return ((DeclaredType) type).asElement().getSimpleName().toString();
        }
        if (type instanceof Element) {
            return ((Element) type).getSimpleName().toString();
        }
//        if (type instanceof Type.ClassType) {
//            Type.ClassType tp = (Type.ClassType) type;
//            return tp.tsym.getSimpleName().toString();
//        }
//        if (type instanceof Symbol.TypeSymbol) {
//            Symbol.TypeSymbol tp = (Symbol.TypeSymbol) type;
//            return tp.getSimpleName().toString();
//        }
        return "";
    }

    static public TypeMirror getBaseTypeWithoutParametrizedFields(TypeMirror type) {
        if (type instanceof DeclaredType) {
            return ((DeclaredType) type).asElement().asType();
        }
        if (type instanceof Element) {
            return ((Element) type).asType();
        }
//
//        if (type instanceof Type.ClassType) {
//            Type.ClassType tp = (Type.ClassType) type;
//            return tp.tsym.asType();
//        }
//        if (type instanceof Symbol.TypeSymbol) {
//            Symbol.TypeSymbol tp = (Symbol.TypeSymbol) type;
//            return tp.asType();
//        }
        type = unwrapWildCardType(type);
//        if (type instanceof Type) return (Type) type;
        return type;
    }

    static public TypeMirror unwrapWildCardType(TypeMirror type) {
        if (type instanceof javax.lang.model.type.WildcardType) {
            javax.lang.model.type.WildcardType tp = (javax.lang.model.type.WildcardType) type;
            TypeMirror type2 = tp.getExtendsBound();
            if (type2 == null) type2 = tp.getSuperBound();
            return type2;
        }
        return type;
    }
//    static public TypeMirror unwrapWildCardType(TypeMirror type) {
//        if (type instanceof Type.WildcardType) {
//            Type.WildcardType tp = (Type.WildcardType) type;
//            return unwrapWildCardType(tp.type);
//        }
//        return type;
//    }
//
    static public List<TypeMirror> getParametrizedTypes(TypeMirror type) {
        type = unwrapWildCardType(type);
        if (type instanceof DeclaredType) {
            return (List<TypeMirror>) ((DeclaredType) type).getTypeArguments();
        }
//        if (type instanceof Type.ClassType) {
//            Type.ClassType tp = (Type.ClassType) type;
//            return tp.allparams();
//        }
        return null;
    }

    static public boolean isSameMethodInputs(ProcessingEnvironment processingEnv, List<TypeWithVariableInfo> inputs1, MethodApiFullSyntax syntaxMethod) {
        if (syntaxMethod == null) return false;
        return isSameMethodInputs(processingEnv, inputs1, syntaxMethod.getParams());
    }

    static public boolean isSameMethodInputs(ProcessingEnvironment processingEnv, List<TypeWithVariableInfo> inputs1, List<TypeWithVariableInfo> inputs2) {
        if (inputs1 == null && inputs2 == null) return true;
        if (inputs1 == null || inputs2 == null) return false;

        if (inputs1.size() != inputs2.size()) return false;


        for (int i = 0; i < inputs1.size(); i++) {
            TypeWithVariableInfo param1 = inputs1.get(i);
            TypeWithVariableInfo param2 = inputs2.get(i);
            if (!isSame(processingEnv, param1.getVariableType(), param2.getVariableType())) return false;
        }
        return true;
    }

    public static boolean isSameType(ProcessingEnvironment processingEnv, TypeMirror var1, TypeMirror var2) {
        if (processingEnv.getTypeUtils().isSameType(var1, var2)) return true;

        // temporary detection same type - TypeUtils.isSameType is sometimes not working in kotlin
        if (Objects.equals(var1 + "", var2 + "")) {
            //boolean eq = processingEnv.getTypeUtils().isSameType(processingEnv.getTypeUtils().erasure(var1), processingEnv.getTypeUtils().erasure(var2));
            //processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "DEBUG: type strings equals ("+eq+")  " + var1 + "("+var1.getClass().getCanonicalName()+")" + " / " + var2+"("+var2.getClass().getCanonicalName()+")");
            return true;
        }

        return false;
    }

    public static boolean isSameTypes(ProcessingEnvironment processingEnv, Class clsType, TypeMirror... types) {
        if (types == null || types.length == 0) return false;

        TypeMirror type = processingEnv.getElementUtils().getTypeElement(clsType.getCanonicalName()).asType();
        for (TypeMirror tp : types) {
            if (!TypeUtils.isSameType(processingEnv, type, tp)) return false;
        }

        return true;
    }

    //  @return {@code true} if and only if the first type is d subtype of the second
    static public boolean isSame(ProcessingEnvironment processingEnv, TypeInfo type1, TypeInfo type2) {
        if (type1 == null && type2 == null) return true;
        if (type1 == null || type2 == null) return false;
        return isSame(processingEnv, type1.getType(processingEnv), type2.getType(processingEnv));
    }

    static public boolean isSame(ProcessingEnvironment processingEnv, TypeMirror type1, TypeMirror type2) {
        if (type1 == null && type2 == null) return true;
        if (type1 == null || type2 == null) return false;
        return TypeUtils.isSameType(processingEnv, type1, type2);
    }

    static public boolean isAssignable(ProcessingEnvironment processingEnv, TypeMirror type1, TypeMirror type2) {
        if (type1 == null && type2 == null) return true;
        if (type1 == null || type2 == null) return false;
        return processingEnv.getTypeUtils().isAssignable(processingEnv.getTypeUtils().erasure(type1), processingEnv.getTypeUtils().erasure(type2));
    }

    public static boolean isAssignableTypes(ProcessingEnvironment processingEnv, Class clsType, TypeMirror... types) {
        if (types == null || types.length == 0) return false;

        TypeMirror type = processingEnv.getTypeUtils().erasure(processingEnv.getElementUtils().getTypeElement(clsType.getCanonicalName()).asType());

        for (TypeMirror tp : types) {
            TypeMirror erasureTP = processingEnv.getTypeUtils().erasure(tp);
            if (!processingEnv.getTypeUtils().isAssignable(erasureTP, type)) return false;
        }

        return true;
    }

    public static boolean isArrayType(ProcessingEnvironment processingEnv, TypeMirror type) {
        TypeMirror erasureTP = processingEnv.getTypeUtils().erasure(type);
        return erasureTP instanceof ArrayType;
    }

    public static boolean isArrayOrCollection(ProcessingEnvironment processingEnv, TypeMirror... types) {
        TypeMirror type = processingEnv.getTypeUtils().erasure(processingEnv.getElementUtils().getTypeElement(Collection.class.getCanonicalName()).asType());
        for (TypeMirror tp : types) {
            TypeMirror erasureTP = processingEnv.getTypeUtils().erasure(tp);
            if (!processingEnv.getTypeUtils().isAssignable(erasureTP, type) && !(erasureTP instanceof ArrayType)) return false;
        }
        return true;
    }

    static public ExecutableType findType(ProcessingEnvironment processingEnv, TypeMirror owner, ExecutableElement element) {
        return (ExecutableType) processingEnv.getTypeUtils().asMemberOf((DeclaredType) owner, element);
    }

    static public DeclaredType findType(ProcessingEnvironment processingEnv, TypeMirror owner, VariableElement element) {
        if (owner instanceof DeclaredType) {
            TypeMirror typeMirror = processingEnv.getTypeUtils().asMemberOf((DeclaredType) owner, element);
            if (typeMirror instanceof DeclaredType) {
                return (DeclaredType) typeMirror;
            }

            return (DeclaredType) owner;
        }

        if (element.asType() instanceof DeclaredType) {
            return (DeclaredType) element.asType();
        }


        throw new IllegalStateException("Not Implemented yet!!!");
    }

    static public String createNullValue(TypeMirror type) {
        if (type == null) return null;
        if (type instanceof NoType) return null;
        if (!type.getKind().isPrimitive()) return "null";
        switch (type.getKind()) {
            case BOOLEAN:
                return "false";
            case INT:
            case SHORT:
            case LONG:
                return "0";
            case FLOAT:
                return "0.0f";
            case DOUBLE:
                return "0.0";
            case BYTE:
                return "(byte) 0";
            case CHAR:
                return "'0'";
        }

        throw new IllegalStateException("Nemapovany typ " + type);
    }

    static public TypeMirror convertToTypeMirror(ProcessingEnvironment processingEnv, Class cls) {
        if (cls == null) return null;
        return convertToTypeMirror(processingEnv, cls.getCanonicalName());
    }

    static public TypeMirror convertToTypeMirror(ProcessingEnvironment processingEnv, String className) {
        if (className == null) return null;

        if (int.class.getCanonicalName().equals(className)) {
            return processingEnv.getTypeUtils().getPrimitiveType(TypeKind.INT);
        }

        if (boolean.class.getCanonicalName().equals(className))
            return processingEnv.getTypeUtils().getPrimitiveType(TypeKind.BOOLEAN);
        if (int.class.getCanonicalName().equals(className))
            return processingEnv.getTypeUtils().getPrimitiveType(TypeKind.INT);
        if (long.class.getCanonicalName().equals(className))
            return processingEnv.getTypeUtils().getPrimitiveType(TypeKind.LONG);
        if (float.class.getCanonicalName().equals(className))
            return processingEnv.getTypeUtils().getPrimitiveType(TypeKind.FLOAT);
        if (double.class.getCanonicalName().equals(className))
            return processingEnv.getTypeUtils().getPrimitiveType(TypeKind.DOUBLE);
        if (byte.class.getCanonicalName().equals(className))
            return processingEnv.getTypeUtils().getPrimitiveType(TypeKind.BYTE);
        if (short.class.getCanonicalName().equals(className))
            return processingEnv.getTypeUtils().getPrimitiveType(TypeKind.SHORT);
        if (char.class.getCanonicalName().equals(className))
            return processingEnv.getTypeUtils().getPrimitiveType(TypeKind.CHAR);
        if (void.class.getCanonicalName().equals(className))
            return processingEnv.getTypeUtils().getPrimitiveType(TypeKind.VOID);

        try {
            return processingEnv.getElementUtils().getTypeElement(className).asType();
        } catch (Exception e) {
            return null;
        }
    }

    static public String findPackageName(TypeMirror typeMirror) {
        NoType pck = findPackageType(typeMirror);
        if (pck == null) return null;
        return pck.toString();
    }

    static public NoType findPackageType(TypeMirror typeMirror) {
        TypeMirror topElementType = findTopElementType(typeMirror);
        if (topElementType == null) {
            return null;
        }

        Element parent = ((DeclaredType)topElementType).asElement().getEnclosingElement();
        if (parent == null) {
            return null;
        }
        TypeMirror parentType = parent.asType();
        if (parentType instanceof NoType) {
            return (NoType) parentType;
        }
        return null;
    }

    static public DeclaredType findTopElementType(TypeMirror typeMirror) {
        if (typeMirror == null) return null;
        if (typeMirror.getKind() != TypeKind.DECLARED) return null;

        if (!(typeMirror instanceof DeclaredType)) return null;

        DeclaredType declaredType = (DeclaredType) typeMirror;
        Element element = declaredType.asElement();


        Element owner = element.getEnclosingElement();
        if (owner!=null) {
            DeclaredType parentType = findTopElementType(owner.asType());
            if (parentType != null) {
                return parentType;
            }
        }

        return declaredType;
    }

    private static Class[] cls1 = {
            java.lang.Enum.class
    };
    private static Class[] cls2 = {
            java.lang.Boolean.class,
            java.lang.Byte.class, java.lang.Short.class,
            java.lang.Integer.class, java.lang.Long.class,
            java.lang.Float.class, java.lang.Double.class,
            java.lang.String.class, java.lang.Character.class,
            BigDecimal.class, BigInteger.class,
            LocalDate.class, LocalDateTime.class, LocalTime.class,
            ZonedDateTime.class, Instant.class
    };
    private static List<TypeMirror> _cls1 = null;
    private static List<TypeMirror> _cls2 = null;

    static private boolean isBaseOrPrimitiveType(ProcessingEnvironment processingEnv, TypeMirror source) {
        if (source.getKind().isPrimitive()) return true;

        if (_cls1 == null) {
            _cls1 = new ArrayList<>(cls1.length);
            _cls2 = new ArrayList<>(cls2.length);
            for (Class aClass : cls1) {
                _cls1.add(convertToTypeMirror(processingEnv, aClass));
            }
            for (Class aClass : cls2) {
                _cls2.add(convertToTypeMirror(processingEnv, aClass));
            }
        }

        // same
        for (TypeMirror typeMirror : _cls2) {
            if (isSameType(processingEnv, source, typeMirror)) return true;
        }

        for (TypeMirror typeMirror : _cls1) {
            if (processingEnv.getTypeUtils().isAssignable(source, typeMirror)) return true;
            if (processingEnv.getTypeUtils().isSubtype(source, typeMirror)) return true;
        }

        return false;
    }

    public static boolean isKnownImmutableType(ProcessingEnvironment processingEnv, TypeMirror inType) {
        if (inType == null) {
//            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "unknown type  - NULL!");
            return true;
        }
        if (isBaseOrPrimitiveType(processingEnv, inType)) {
//            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "primitive type " + inType);
            return true;
        }
        if (isEnunType(processingEnv, new TypeInfo(inType))) {
//            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "imutable type " + inType);
            return true;
        }

//        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "unknown type " + inType);
        return false;
    }

    public static boolean isEnunType(ProcessingEnvironment processingEnv, TypeInfo inType) {
        if (inType == null) return false;
        return isEnunType(processingEnv, inType.getType(processingEnv));
    }

    public static boolean isEnunType(ProcessingEnvironment processingEnv, TypeMirror inType) {
        if (inType == null) return false;
        Element element = processingEnv.getTypeUtils().asElement(inType);
        if (element == null) return false;
        return element.getKind() == ElementKind.ENUM;
    }

    public static List<String> getEnumValues(ProcessingEnvironment processingEnv, TypeInfo inType) {
        Element typeSymbol = processingEnv.getTypeUtils().asElement(inType.getType(processingEnv));
        //Preconditions.checkArgument(typeSymbol.getKind() == ElementKind.ENUM);
        return typeSymbol.getEnclosedElements().stream()
                .filter(a -> a.getKind() == ElementKind.ENUM_CONSTANT)
                .map(Element::toString).collect(Collectors.toList());
    }
}
