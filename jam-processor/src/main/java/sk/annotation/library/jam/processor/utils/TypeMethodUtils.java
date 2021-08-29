package sk.annotation.library.jam.processor.utils;

import com.sun.tools.javac.code.Type;
import sk.annotation.library.jam.processor.data.TypeInfo;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeMethodUtils {
    final ProcessingEnvironment processingEnv;
    final Map<String, TypeMirror> resolvedParametrizedTypes = new HashMap<>();
    private TypeMethodUtils(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    private static enum TypCompareValue {SAME, REQUIRED_VALUE_AS_PARENT, REQUIRED_VALUE_AS_CHILD}

    static public boolean isMethodCallableForMapper(ProcessingEnvironment processingEnv, ExecutableType requiredMethodType, ExecutableType testedMethodType) {
        if (testedMethodType == null || requiredMethodType==null) {
            return false;
        }
        if (testedMethodType.getParameterTypes().size() != requiredMethodType.getParameterTypes().size()) return false;
        if (TypeInfo.analyzeReturnType(testedMethodType.getReturnType()) == null) return false;

        TypeMethodUtils typeMethodUtils = new TypeMethodUtils(processingEnv);

//        if (typeUtils.isSubsignature(requiredMethodType, testedMethodType)) {
//            return true; // OK
//        }

        for (int iParam = 0; iParam < testedMethodType.getParameterTypes().size(); iParam++) {
            TypeMirror requiredParamType = requiredMethodType.getParameterTypes().get(iParam);
            TypeMirror testParamType = testedMethodType.getParameterTypes().get(iParam);
            if (!typeMethodUtils.testRequiredType(testParamType, requiredParamType, TypCompareValue.SAME)) {
                return false;
            }
        }

        return typeMethodUtils.testRequiredType(testedMethodType.getReturnType(), requiredMethodType.getReturnType(), TypCompareValue.SAME);
    }

    static public boolean isMethodCallableForInterceptor(ProcessingEnvironment processingEnv, TypeMirror requiredSrcType, TypeMirror requiredDstType, ExecutableType testedMethodType) {
        if (testedMethodType.getParameterTypes().size() != 2) return false;
        if (TypeInfo.analyzeReturnType(testedMethodType.getReturnType()) != null) return false;

        TypeMethodUtils typeMethodUtils = new TypeMethodUtils(processingEnv);

        if (!typeMethodUtils.testRequiredType(testedMethodType.getParameterTypes().get(0), requiredSrcType, TypCompareValue.REQUIRED_VALUE_AS_PARENT)) {
            return false;
        }
        if (!typeMethodUtils.testRequiredType(testedMethodType.getParameterTypes().get(1), requiredDstType, TypCompareValue.REQUIRED_VALUE_AS_PARENT)) {
            return false;
        }

        return true;
    }

    private static boolean hasTypeArguments(TypeMirror type) {
        if (type instanceof DeclaredType) {
            List<? extends TypeMirror> typeArguments = ((DeclaredType) type).getTypeArguments();
            if (typeArguments == null || typeArguments.isEmpty()) return false;
            return true;
        }
        return false;
    }

    private boolean testRequiredType(TypeMirror testParamType, TypeMirror requiredParamType, TypCompareValue mode) {
        if (testParamType == null || requiredParamType == null) {
            return false;
        }
        if (testParamType instanceof Type.TypeVar) {
            Type.TypeVar tstType = (Type.TypeVar) testParamType;
            if (!testRequiredType(tstType.getUpperBound(), requiredParamType, TypCompareValue.REQUIRED_VALUE_AS_PARENT)) return false;

            String typeVarName = tstType.tsym.toString();
            if (!resolvedParametrizedTypes.containsKey(typeVarName)) {
                resolvedParametrizedTypes.put(typeVarName, requiredParamType);
                return true;
            }

            // resolved parametrized types has to be same
            return TypeUtils.isSameType(processingEnv, requiredParamType, resolvedParametrizedTypes.get(typeVarName));
        }

        if (hasTypeArguments(testParamType) && hasTypeArguments(requiredParamType)) {
            List<? extends TypeMirror> reqTypeArguments = ((DeclaredType) requiredParamType).getTypeArguments();
            List<? extends TypeMirror> testTypeArguments = ((DeclaredType) testParamType).getTypeArguments();

            if (reqTypeArguments.size() != testTypeArguments.size()) return false;

            for (int i = 0; i < reqTypeArguments.size(); i++) {
                if (!testRequiredType(testTypeArguments.get(i), reqTypeArguments.get(i), TypCompareValue.SAME)) {
                    return false;
                }
            }
        }

        switch (mode) {
            case SAME:
                return TypeUtils.isSameType(processingEnv, processingEnv.getTypeUtils().erasure(testParamType), processingEnv.getTypeUtils().erasure(requiredParamType));

            case REQUIRED_VALUE_AS_PARENT:
                return TypeUtils.isAssignable(processingEnv, processingEnv.getTypeUtils().erasure(requiredParamType), processingEnv.getTypeUtils().erasure(testParamType));

            case REQUIRED_VALUE_AS_CHILD:
                return TypeUtils.isAssignable(processingEnv, processingEnv.getTypeUtils().erasure(testParamType), processingEnv.getTypeUtils().erasure(requiredParamType));
        }

        throw new IllegalStateException("Unknown TYPE");
    }
}
