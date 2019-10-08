package sk.annotation.library.jam.processor;

import sk.annotation.library.jam.annotations.Context;
import sk.annotation.library.jam.annotations.JamGenerated;
import sk.annotation.library.jam.annotations.Return;
import sk.annotation.library.jam.processor.data.AnnotationsInfo;
import sk.annotation.library.jam.processor.data.TypeInfo;
import sk.annotation.library.jam.processor.data.TypeWithVariableInfo;
import sk.annotation.library.jam.utils.MapperRunCtxData;
import sk.annotation.library.jam.utils.MapperRunCtxDataHolder;
import sk.annotation.library.jam.utils.cache.InstanceCacheValue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

abstract public class Constants {
    // my annotation types
    static final public TypeInfo annotationContext = new TypeInfo(Context.class);
    static final public TypeInfo annotationReturn = new TypeInfo(Return.class);
    static final public TypeInfo annotationJamMapperGenerated = new TypeInfo(JamGenerated.class);

    // java annotations
    static final public TypeInfo annotationOverride = new TypeInfo(Override.class);
//    static final public TypeInfo annotationGenerated = new TypeInfo(javax.annotation.Generated.class);

    static final public AnnotationsInfo createAnnotationGenerated() {
        AnnotationsInfo val = new AnnotationsInfo();
        val.getOrAddAnnotation(annotationJamMapperGenerated)
                .withStringValue(AnnotationJamMapperProcessor.class.getCanonicalName())
                .withKeyAndStringValue("date", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
        ;
        return val;
    }


    // Spring & CDI
    static final public String annotationFieldSPRING = "org.springframework.beans.factory.annotation.Autowired";
    static final public String annotationFieldCDI = "javax.inject.Inject";

    static final public String springComponent = "org.springframework.stereotype.Component";
    static final public String springContext = "org.springframework.context.annotation.Scope";

    static final public String cdiComponent = "javax.inject.Named.class";
    static final public String cdiContextRequest = "javax.enterprise.context.RequestScoped.class";
    static final public String cdiContextSession = "javax.enterprise.context.SessionScoped.class";
    static final public String cdiContextApplication = "javax.enterprise.context.ApplicationScoped.class";
    static final public String cdiContextSingleton = "javax.inject.Singleton.class";



    static final public TypeInfo typeInstanceCacheValue = new TypeInfo(InstanceCacheValue.class);
    static final public TypeInfo typeMapperRunCtxData = new TypeInfo(MapperRunCtxData.class);
    static final public TypeInfo typeMapperRunCtxDataHolder = new TypeInfo(MapperRunCtxDataHolder.class);

    static final public TypeWithVariableInfo methodParamInfo_ctxForMethodId = new TypeWithVariableInfo("confId", new TypeInfo(int.class), Context.jamConfif, false);
    static final public TypeWithVariableInfo methodParamInfo_ctxForRunData = new TypeWithVariableInfo("ctx", typeMapperRunCtxData, Context.jamContext, false);
}
