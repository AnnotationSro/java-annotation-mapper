package sk.annotation.library.jam.processor.data.mapi;

import com.sun.tools.javac.code.Type;
import sk.annotation.library.jam.processor.data.TypeInfo;
import sk.annotation.library.jam.processor.data.TypeWithVariableInfo;
import sk.annotation.library.jam.processor.utils.commons.StringUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MethodApiKey {
	final private String[] visibleStrTypes;		// first is return type
	final private boolean apiWithReturnType;
	public boolean isApiWithReturnType() {
		return apiWithReturnType;
	}

	final private TypeMirror[] visibleTypes;
	private ExecutableType methodType = null;

	private MethodApiKey (boolean apiWithReturnType, String[] visibleStrTypes,TypeMirror[] visibleTypes) {
		this.apiWithReturnType = apiWithReturnType;
		this.visibleStrTypes = visibleStrTypes;
		this.visibleTypes = visibleTypes;
		this.methodType = null;
	}
	private MethodApiKey (boolean apiWithReturnType, TypeMirror... inputParams) {
		this.apiWithReturnType = apiWithReturnType;
		this.visibleTypes = inputParams;
		this.visibleStrTypes = transform(inputParams);
	}

	public ExecutableType createMethodExecutableType(ProcessingEnvironment processingEnv, TypeElement parentElement) {
		if (methodType == null) {
			Type returnType = (Type) visibleTypes[0];
			List<Type> params = new ArrayList<>(visibleTypes.length);
			for (int i = 1; i < visibleTypes.length; i++) {
				TypeMirror visibleType = visibleTypes[i];
				if (visibleType instanceof Type) {
					params.add((Type) visibleType);
				}
				else {
					//WARNING !!!
					throw new IllegalStateException("");
				}
			}
			if (apiWithReturnType && returnType!=null) {
				params.add(returnType);
			}
			Type returnTypeMirror = (Type) visibleTypes[0];
			if (returnTypeMirror == null) {
				returnTypeMirror = (Type) processingEnv.getTypeUtils().getNoType(TypeKind.VOID);
			}
			methodType = new Type.MethodType(com.sun.tools.javac.util.List.from(params.toArray(new Type[0])), returnTypeMirror, com.sun.tools.javac.util.List.<Type>nil(), ((Type) parentElement.asType()).tsym);
		}
		return methodType;
	}

    @Override
    public String toString() {
        return "MethodApiKey{" +
                "visibleStrTypes=" + Arrays.toString(visibleStrTypes) +
                ", apiWithReturnType=" + apiWithReturnType +
                '}';
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
//		MethodApiKey that = (MethodApiKey) o;
		return this.toString().equals(o.toString());
	}

	@Override
	public int hashCode() {
//		int result = Objects.hash(apiWithReturnType);
//		result = 31 * result + Arrays.hashCode(visibleTypes);
		return this.toString().hashCode();
	}

	public MethodApiKey (TypeInfo returnType, List<TypeWithVariableInfo> inputParams) {
		this(detectApiWithReturnType(returnType, inputParams), merge(returnType, inputParams));
	}

	public static MethodApiKey createWithoutReturnTypeInParam(MethodApiKey apiKey) {
		if (apiKey == null || !apiKey.apiWithReturnType) return null;
		return new MethodApiKey(false, apiKey.visibleStrTypes, apiKey.visibleTypes);
	}

	private static String[] transform(TypeMirror[] tp) {
		String[] ret = new String[tp.length];
		for (int i = 0; i < tp.length; i++) {
			ret[i] = tp[i]==null ? null : tp[i].toString();
		}
		return ret;
	}
	private static TypeMirror[] merge(TypeInfo returnType, List<TypeWithVariableInfo> inputParams) {
		List<TypeMirror> visibleTypes = new ArrayList<>(inputParams.size()+1);
		visibleTypes.add(unwrapType(returnType));
		for (TypeWithVariableInfo param : inputParams) {
			if (StringUtils.isNotEmpty(param.getHasContextKey())) continue;
			if (param.isMarkedAsReturn()) continue;
			visibleTypes.add(unwrapType(param));
		}
		return visibleTypes.toArray(new TypeMirror[visibleTypes.size()]);
	}
	private static boolean detectApiWithReturnType(TypeInfo returnType, List<TypeWithVariableInfo> inputParams) {
		if (returnType == null) return false;
		if (inputParams.isEmpty()) return false;
		return inputParams.get(inputParams.size()-1).isMarkedAsReturn();
	}


	static private TypeMirror unwrapType(TypeInfo type) {
		if (type == null) return null;
		return type.type;
	}
	static private TypeMirror unwrapType(TypeWithVariableInfo param) {
		if (param == null) return null;
		return unwrapType(param.getVariableType());
	}

	public TypeMirror[] getVisibleTypes() {
		return visibleTypes;
	}
}
