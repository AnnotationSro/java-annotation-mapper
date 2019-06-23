package sk.annotation.library.mapper.fast.processor;

import sk.annotation.library.mapper.fast.annotations.Context;
import sk.annotation.library.mapper.fast.annotations.FastMapperGenerated;
import sk.annotation.library.mapper.fast.annotations.Return;
import sk.annotation.library.mapper.fast.processor.data.AnnotationsInfo;
import sk.annotation.library.mapper.fast.processor.data.TypeWithVariableInfo;
import sk.annotation.library.mapper.fast.processor.data.TypeInfo;
import sk.annotation.library.mapper.fast.utils.MapperRunCtxData;
import sk.annotation.library.mapper.fast.utils.MapperRunCtxDataHolder;
import sk.annotation.library.mapper.fast.utils.context.MapperUtil;

import javax.annotation.Generated;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

abstract public class Constants {
	// my annotation types
	static final public TypeInfo annotationContext = new TypeInfo(Context.class);
	static final public TypeInfo annotationReturn = new TypeInfo(Return.class);
	static final public TypeInfo annotationFastMapperGenerated = new TypeInfo(FastMapperGenerated.class);

	// java annotations
	static final public TypeInfo annotationOverride = new TypeInfo(Override.class);
	static final public TypeInfo annotationGenerated = new TypeInfo(Generated.class);

	static final public AnnotationsInfo createAnnotationGenerated() {
		AnnotationsInfo val = new AnnotationsInfo();
		val.getOrAddAnnotation(annotationGenerated)
				.withStringValue(AnnotationFastMapperProcessor.class.getCanonicalName())
				.withKeyAndStringValue("date", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
		;
		return val;
	}


	// Spring & CDI
	static final public TypeInfo annotationFieldSPRING = new TypeInfo(org.springframework.beans.factory.annotation.Autowired.class);
	static final public TypeInfo annotationFieldCDI = new TypeInfo(javax.inject.Inject.class);

	static final public TypeInfo springComponent = new TypeInfo(org.springframework.stereotype.Component.class);
	static final public TypeInfo springContext = new TypeInfo(org.springframework.context.annotation.Scope.class);

	static final public TypeInfo cdiComponent = new TypeInfo(javax.inject.Named.class);
	static final public TypeInfo cdiContextRequest = new TypeInfo(javax.enterprise.context.RequestScoped.class);
	static final public TypeInfo cdiContextSession = new TypeInfo(javax.enterprise.context.SessionScoped.class);
	static final public TypeInfo cdiContextApplication = new TypeInfo(javax.enterprise.context.ApplicationScoped.class);
	static final public TypeInfo cdiContextSingleton = new TypeInfo(javax.inject.Singleton.class);




	static final private String reservedNameForMethodId = "**mapper-conf-id**";
	static final private String reservedNameForContextData = "**mapper-ctx**";

	static final public TypeInfo typeMapperUtil = new TypeInfo(MapperUtil.class);
	static final public TypeInfo typeMapperRunCtxData = new TypeInfo(MapperRunCtxData.class);
	static final public TypeInfo typeMapperRunCtxDataHolder = new TypeInfo(MapperRunCtxDataHolder.class);

	static final public TypeWithVariableInfo methodParamInfo_ctxForMethodId = new TypeWithVariableInfo("confId", new TypeInfo(int.class), Constants.reservedNameForMethodId, false);;
	static final public TypeWithVariableInfo methodParamInfo_ctxForRunData = new TypeWithVariableInfo("ctx", typeMapperRunCtxData, Constants.reservedNameForContextData, false);;

}
