package sk.annotation.library.jam.processor.data.generator.row;

import sk.annotation.library.jam.processor.data.MapperClassInfo;
import sk.annotation.library.jam.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.jam.processor.utils.TypeUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleRowValueTransformator extends AbstractRowValueTransformator {
    public static final SimpleRowValueTransformator instance = new SimpleRowValueTransformator();

    private Map<Class, TypeMirror> cache = new LinkedHashMap<>();

    protected TypeMirror getType(ProcessingEnvironment processingEnv, Class cls) {
        return cache.computeIfAbsent(cls, (a) -> TypeUtils.convertToType(processingEnv, cls));
    }

    protected boolean isTypeFromTo(ProcessingEnvironment processingEnv, TypeMirror type1, TypeMirror type2, Class cls1, Class cls2) {
        return TypeUtils.isSame(processingEnv, type1, getType(processingEnv, cls1)) &&
                TypeUtils.isSame(processingEnv, type2, getType(processingEnv, cls2));
    }

    @Override
    boolean accept(ProcessingEnvironment processingEnv, MapperClassInfo ownerClassInfo, TypeMirror source, TypeMirror destination) {
        String code = _generateRowTransform(processingEnv, source, destination, "a");
        return code != null;
    }

    @Override
    public String generateRowTransform(SourceGeneratorContext ctx, TypeMirror source, TypeMirror destination, String varValue) {
        return _generateRowTransform(ctx.processingEnv, source, destination, varValue);
    }

    protected String _generateRowTransform(ProcessingEnvironment processingEnv, TypeMirror source, TypeMirror destination, String varValue) {

        if (isTypeFromTo(processingEnv, source, destination, int.class, Integer.class)) return varValue;
        if (isTypeFromTo(processingEnv, source, destination, Integer.class, int.class)) return varValue + "==null ? 0 : " + varValue;

// Examples:
//        if (isTypeFromTo(processingEnv, source, destination, int.class, BigInteger.class)) return varValue+"==null ? null : BigInteger.valueOf("+varValue+")";
//        if (isTypeFromTo(processingEnv, source, destination, int.class, BigDecimal.class)) return "BigDecimal.valueOf("+varValue+")";

        if (isTypeFromTo(processingEnv, source, destination, long.class, Long.class)) return varValue;
        if (isTypeFromTo(processingEnv, source, destination, Long.class, long.class)) return varValue + "==null ? 0 : " + varValue;

        return null;
    }
}
