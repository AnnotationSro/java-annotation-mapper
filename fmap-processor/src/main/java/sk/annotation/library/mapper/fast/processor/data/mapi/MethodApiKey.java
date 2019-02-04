package sk.annotation.library.mapper.fast.processor.data.mapi;

import lombok.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.HashCodeExclude;
import sk.annotation.library.mapper.fast.processor.data.MethodParamInfo;
import sk.annotation.library.mapper.fast.processor.data.TypeInfo;

import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode
public class MethodApiKey {
	@Getter(AccessLevel.NONE)
	final private String[] visibleStrTypes;		// first is return type
	final private boolean apiWithReturnType;

	@EqualsAndHashCode.Exclude
	final private TypeMirror[] visibleTypes;

	private MethodApiKey (boolean apiWithReturnType, TypeMirror... inputParams) {
		this.apiWithReturnType = apiWithReturnType;
		this.visibleTypes = inputParams;
		this.visibleStrTypes = transform(inputParams);
	}


	public MethodApiKey (TypeInfo returnType, List<MethodParamInfo> inputParams) {
		this(detectApiWithReturnType(returnType, inputParams), merge(returnType, inputParams));
	}

	private static String[] transform(TypeMirror[] tp) {
		String[] ret = new String[tp.length];
		for (int i = 0; i < tp.length; i++) {
			ret[i] = tp[i]==null ? null : tp[i].toString();
		}
		return ret;
	}
	private static TypeMirror[] merge(TypeInfo returnType, List<MethodParamInfo> inputParams) {
		List<TypeMirror> visibleTypes = new ArrayList<>(inputParams.size()+1);
		visibleTypes.add(unwrapType(returnType));
		for (MethodParamInfo param : inputParams) {
			if (StringUtils.isNotEmpty(param.getHasContextKey())) continue;
			visibleTypes.add(unwrapType(param));
		}
		return visibleTypes.toArray(new TypeMirror[visibleTypes.size()]);
	}
	private static boolean detectApiWithReturnType(TypeInfo returnType, List<MethodParamInfo> inputParams) {
		if (returnType == null) return false;
		if (inputParams.isEmpty()) return false;
		return inputParams.get(inputParams.size()-1).isMarkedAsReturn();
	}


	static private TypeMirror unwrapType(TypeInfo type) {
		if (type == null) return null;
		return type.type;
	}
	static private TypeMirror unwrapType(MethodParamInfo param) {
		if (param == null) return null;
		return unwrapType(param.getVariable().getType());
	}

}
