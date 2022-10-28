package sk.annotation.library.jam.processor.data.mapi;

import javax.lang.model.type.TypeMirror;
import java.util.List;

public class MethodInfo {
    final private TypeMirror returnType;
    final private List<TypeMirror> parameterTypes;

    public MethodInfo(TypeMirror returnType, List<TypeMirror> parameterTypes) {
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
    }

    public TypeMirror getReturnType() {
        return returnType;
    }

    public List<TypeMirror> getParameterTypes() {
        return parameterTypes;
    }
}
