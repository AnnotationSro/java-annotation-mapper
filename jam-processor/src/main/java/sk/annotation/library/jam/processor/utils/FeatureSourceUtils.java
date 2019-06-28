package sk.annotation.library.jam.processor.utils;

import lombok.Getter;
import lombok.Setter;
import sk.annotation.library.jam.annotations.DisableMapperFeature;
import sk.annotation.library.jam.annotations.enums.MapperFeature;

import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FeatureSourceUtils {
    final private Set<MapperFeature> disabledFeatures = new HashSet<>();
    final private boolean disabledAllFeatures;
    @Setter
    @Getter
    private boolean enableMethodContext = false;

    public FeatureSourceUtils(TypeElement element) {
        DisableMapperFeature features = element.getAnnotation(DisableMapperFeature.class);
        if (features!=null  && features.value().length>0) {
            Collections.addAll(disabledFeatures, features.value());
        }
        boolean disabledAllFeatures = true;
        for (MapperFeature feature : MapperFeature.values()) {
            if (!disabledFeatures.contains(feature)) {
                disabledAllFeatures = false;
                break;
            }
        }
        this.disabledAllFeatures = disabledAllFeatures;
    }

    public boolean isDisabledToUseMapperRunCtxData() {
        return disabledAllFeatures;
    }

    public boolean isDisabled_SHARED_THREAD_CONTEXT() {
        return disabledFeatures.contains(MapperFeature.PERSISTED_DATA_IN_LOCAL_THREAD);
    }

    public boolean isDisabled_CONTEXT_VALUES() {
        return disabledFeatures.contains(MapperFeature.METHOD_SUPPORTS_CONTEXT_PARAMETERS);
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
