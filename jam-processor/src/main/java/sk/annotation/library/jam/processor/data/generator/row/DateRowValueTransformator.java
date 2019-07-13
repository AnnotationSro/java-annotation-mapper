package sk.annotation.library.jam.processor.data.generator.row;

import sk.annotation.library.jam.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.jam.processor.utils.TypeUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class DateRowValueTransformator extends AbstractRowValueTransformator {
    public static final DateRowValueTransformator instance = new DateRowValueTransformator();

    private Map<Class, TypeMirror> cache = new LinkedHashMap<>();

    protected TypeMirror getType(ProcessingEnvironment processingEnv, Class cls) {
        return cache.computeIfAbsent(cls, (a) -> TypeUtils.convertToType(processingEnv, cls));
    }

    protected boolean isOneOfAcceptedType(ProcessingEnvironment processingEnv, TypeMirror type) {
        if (type == null) return false;
        if (TypeUtils.isAssignable(processingEnv, type, getType(processingEnv, Date.class))) return true;
        if (TypeUtils.isAssignable(processingEnv, getType(processingEnv, Date.class), type)) return true;
        return false;
    }

    @Override
    boolean accept(ProcessingEnvironment processingEnv, TypeMirror source, TypeMirror destination) {
        return isOneOfAcceptedType(processingEnv, source) && isOneOfAcceptedType(processingEnv, destination);
    }

    @Override
    public String generateRowTransform(SourceGeneratorContext ctx, TypeMirror source, TypeMirror destination, String varValue) {
        return varValue + "==null ? null : new " + ctx.javaClassWriter.imports.resolveType(destination) + "(" + varValue + ".getTime())";
    }

}
