package sk.annotation.library.jam.processor.utils;

import sk.annotation.library.jam.annotations.DisableMapperFeature;
import sk.annotation.library.jam.annotations.enums.MapperFeature;
import sk.annotation.library.jam.processor.Constants;
import sk.annotation.library.jam.processor.data.MapperClassInfo;
import sk.annotation.library.jam.processor.data.TypeWithVariableInfo;
import sk.annotation.library.jam.processor.utils.commons.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * methodId -> different body methods => isRequiredInputWithMethodId() - che
 * if methods needs to work with @Context or PREVENT_CYCLIC_MAPPING is enabled => isRequiredInputWithContextObject()
 *
 * Options during generation of method:
 * ifEnabledPreventCyclicMapping() => we have to generate lines for dependency checking !!!
 * ifContextValueIsOnInput() => we have to generate put values to Context
 * if methodCall is required and ifContextRequired
 *
 *
 * */

public class FeatureSourceUtils {
	final private Set<MapperFeature> disabledFeatures;

	public FeatureSourceUtils(MapperClassInfo mapperClassInfo) {

		DisableMapperFeature features = mapperClassInfo.getParentElement().getAnnotation(DisableMapperFeature.class);
		Set<MapperFeature> disabledFeatures = new HashSet<>();
		if (features != null) {
			Collections.addAll(disabledFeatures, features.value());
		}

		// Special ALL (if ALL  => add all other features + remove ALL item)
		if (disabledFeatures.contains(MapperFeature.ALL)) Collections.addAll(disabledFeatures, MapperFeature.values());
		disabledFeatures.remove(MapperFeature.ALL);

		this.disabledFeatures = Collections.unmodifiableSet(disabledFeatures);
	}



	////////////////////////////////////////////////////////////////////////////
	// 1) Analyze inputs for isRequiredInputWithMethodId()
	private boolean requiredInputWithMethodId = false;
	public void setRequiredInputWithMethodId(boolean requiredInputWithMethodId) {
		if (requiredInputWithMethodId) this.requiredInputWithMethodId = true;
	}
	public boolean isRequiredInputWithMethodId() {
		return requiredInputWithMethodId;
	}

	////////////////////////////////////////////////////////////////////////////
	// 2) Analyze inputs for isRequiredInputWithContextData()
	private boolean areUsedRealContextValues = false;
	public void checkContextValuesInMethodInputs(List<TypeWithVariableInfo> params) {
		if (areUsedRealContextValues) return;
		if (params == null) return;
		for (TypeWithVariableInfo param : params) {
			if (param == null) continue;
			if (StringUtils.isEmpty(param.getHasContextKey())) continue;
			if (Constants.methodParamInfo_ctxForMethodId.getHasContextKey().equals(param.getHasContextKey())) continue;
			if (Constants.methodParamInfo_ctxForRunData.getHasContextKey().equals(param.getHasContextKey())) continue;
			areUsedRealContextValues = true;
			return;
		}
	}
	public boolean isRequiredInputWithContextData() {
		return areUsedRealContextValues || !isDisabled_CYCLIC_MAPPING() || !isDisabled_SHARED_CONTEXT_DATA_IN_SUB_MAPPER();
	}

	////////////////////////////////////////////////////////////////////////////
	// 3) Feature controlled by configuration
	public boolean isDisabled_SHARED_CONTEXT_DATA_IN_SUB_MAPPER() {
		return disabledFeatures.contains(MapperFeature.SHARED_CONTEXT_DATA_IN_SUB_MAPPER);
	}

	public boolean isDisabled_CYCLIC_MAPPING() {
		return disabledFeatures.contains(MapperFeature.PREVENT_CYCLIC_MAPPING);
	}



	public String getInfoHowCanBeDisabled(MapperFeature nameOfFeature) {
		StringBuilder sb = new StringBuilder();
		sb.append("with annotation @");
		sb.append(DisableMapperFeature.class.getSimpleName()).append("(");
		sb.append(MapperFeature.class.getSimpleName()).append(".").append(nameOfFeature.name());
		sb.append(")");
		return sb.toString();
	}
}
