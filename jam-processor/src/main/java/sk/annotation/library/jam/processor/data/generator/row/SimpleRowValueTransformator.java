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
        return cache.computeIfAbsent(cls, (a) -> TypeUtils.convertToTypeMirror(processingEnv, cls));
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

        //primitive wrappers mapping ------------------------------------------------------------------------------

        if (isTypeFromTo(processingEnv, source, destination, boolean.class, Boolean.class)) return varValue;
        if (isTypeFromTo(processingEnv, source, destination, Boolean.class, boolean.class)) return varValue + "==null ? false : " + varValue;

        if (isTypeFromTo(processingEnv, source, destination, byte.class, Byte.class)) return varValue;
        if (isTypeFromTo(processingEnv, source, destination, Byte.class, byte.class)) return varValue + "==null ? 0 : " + varValue;

        if (isTypeFromTo(processingEnv, source, destination, short.class, Short.class)) return varValue;
        if (isTypeFromTo(processingEnv, source, destination, Short.class, short.class)) return varValue + "==null ? 0 : " + varValue;

        if (isTypeFromTo(processingEnv, source, destination, int.class, Integer.class)) return varValue;
        if (isTypeFromTo(processingEnv, source, destination, Integer.class, int.class)) return varValue + "==null ? 0 : " + varValue;

        if (isTypeFromTo(processingEnv, source, destination, long.class, Long.class)) return varValue;
        if (isTypeFromTo(processingEnv, source, destination, Long.class, long.class)) return varValue + "==null ? 0L : " + varValue;

        if (isTypeFromTo(processingEnv, source, destination, float.class, Float.class)) return varValue;
        if (isTypeFromTo(processingEnv, source, destination, Float.class, float.class)) return varValue + "==null ? 0f : " + varValue;

        if (isTypeFromTo(processingEnv, source, destination, double.class, Double.class)) return varValue;
        if (isTypeFromTo(processingEnv, source, destination, Double.class, double.class)) return varValue + "==null ? 0 : " + varValue;

        if (isTypeFromTo(processingEnv, source, destination, char.class, Character.class)) return varValue;
        if (isTypeFromTo(processingEnv, source, destination, Character.class, char.class)) return varValue + "==null ? '\\u0000' : " + varValue;

        //cross types mapping ------------------------------------------------------------------------------------

        //byte
        if (isTypeFromTo(processingEnv, source, destination, byte.class, short.class)) return "(short)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, byte.class, Short.class)) return "(short)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, byte.class, int.class)) return "(int)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, byte.class, Integer.class)) return "(int)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, byte.class, long.class)) return "(long)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, byte.class, Long.class)) return "(long)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, byte.class, float.class)) return "(float)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, byte.class, Float.class)) return "(float)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, byte.class, double.class)) return "(double)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, byte.class, Double.class)) return "(double)" + varValue;

        if (isTypeFromTo(processingEnv, source, destination, Byte.class, short.class)) return varValue + "==null ? 0 : " + varValue + ".shortValue()";
        if (isTypeFromTo(processingEnv, source, destination, Byte.class, Short.class)) return varValue + "==null ? null : " + varValue + ".shortValue()";
        if (isTypeFromTo(processingEnv, source, destination, Byte.class, int.class)) return varValue + "==null ? 0 : " + varValue + ".intValue()";
        if (isTypeFromTo(processingEnv, source, destination, Byte.class, Integer.class)) return varValue + "==null ? null : " + varValue + ".intValue()";
        if (isTypeFromTo(processingEnv, source, destination, Byte.class, long.class)) return varValue + "==null ? 0 : " + varValue + ".longValue()";
        if (isTypeFromTo(processingEnv, source, destination, Byte.class, Long.class)) return varValue + "==null ? null : " + varValue + ".longValue()";
        if (isTypeFromTo(processingEnv, source, destination, Byte.class, float.class)) return varValue + "==null ? 0 : " + varValue + ".floatValue()";
        if (isTypeFromTo(processingEnv, source, destination, Byte.class, Float.class)) return varValue + "==null ? null : " + varValue + ".floatValue()";
        if (isTypeFromTo(processingEnv, source, destination, Byte.class, double.class)) return varValue + "==null ? 0 : " + varValue + ".doubleValue()";
        if (isTypeFromTo(processingEnv, source, destination, Byte.class, Double.class)) return varValue + "==null ? null : " + varValue + ".doubleValue()";


        //short
        if (isTypeFromTo(processingEnv, source, destination, short.class, byte.class)) return "(byte)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, short.class, Byte.class)) return "(byte)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, short.class, int.class)) return "(int)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, short.class, Integer.class)) return "(int)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, short.class, long.class)) return "(long)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, short.class, Long.class)) return "(long)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, short.class, float.class)) return "(float)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, short.class, Float.class)) return "(float)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, short.class, double.class)) return "(double)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, short.class, Double.class)) return "(double)" + varValue;

        if (isTypeFromTo(processingEnv, source, destination, Short.class, byte.class)) return varValue + "==null ? 0 : " + varValue + ".byteValue()";
        if (isTypeFromTo(processingEnv, source, destination, Short.class, Byte.class)) return varValue + "==null ? null : " + varValue + ".byteValue()";
        if (isTypeFromTo(processingEnv, source, destination, Short.class, int.class)) return varValue + "==null ? 0 : " + varValue + ".intValue()";
        if (isTypeFromTo(processingEnv, source, destination, Short.class, Integer.class)) return varValue + "==null ? null : " + varValue + ".intValue()";
        if (isTypeFromTo(processingEnv, source, destination, Short.class, long.class)) return varValue + "==null ? 0 : " + varValue + ".longValue()";
        if (isTypeFromTo(processingEnv, source, destination, Short.class, Long.class)) return varValue + "==null ? null : " + varValue + ".longValue()";
        if (isTypeFromTo(processingEnv, source, destination, Short.class, float.class)) return varValue + "==null ? 0 : " + varValue + ".floatValue()";
        if (isTypeFromTo(processingEnv, source, destination, Short.class, Float.class)) return varValue + "==null ? null : " + varValue + ".floatValue()";
        if (isTypeFromTo(processingEnv, source, destination, Short.class, double.class)) return varValue + "==null ? 0 : " + varValue + ".doubleValue()";
        if (isTypeFromTo(processingEnv, source, destination, Short.class, Double.class)) return varValue + "==null ? null : " + varValue + ".doubleValue()";

        //int
        if (isTypeFromTo(processingEnv, source, destination, int.class, byte.class)) return "(byte)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, int.class, Byte.class)) return "(byte)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, int.class, short.class)) return "(short)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, int.class, Short.class)) return "(short)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, int.class, long.class)) return "(long)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, int.class, Long.class)) return "(long)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, int.class, float.class)) return "(float)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, int.class, Float.class)) return "(float)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, int.class, double.class)) return "(double)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, int.class, Double.class)) return "(double)" + varValue;

        if (isTypeFromTo(processingEnv, source, destination, Integer.class, byte.class)) return varValue + "==null ? 0 : " + varValue + ".byteValue()";
        if (isTypeFromTo(processingEnv, source, destination, Integer.class, Byte.class)) return varValue + "==null ? null : " + varValue + ".byteValue()";
        if (isTypeFromTo(processingEnv, source, destination, Integer.class, short.class)) return varValue + "==null ? 0 : " + varValue + ".shortValue()";
        if (isTypeFromTo(processingEnv, source, destination, Integer.class, Short.class)) return varValue + "==null ? null : " + varValue + ".shortValue()";
        if (isTypeFromTo(processingEnv, source, destination, Integer.class, long.class)) return varValue + "==null ? 0 : " + varValue + ".longValue()";
        if (isTypeFromTo(processingEnv, source, destination, Integer.class, Long.class)) return varValue + "==null ? null : " + varValue + ".longValue()";
        if (isTypeFromTo(processingEnv, source, destination, Integer.class, float.class)) return varValue + "==null ? 0 : " + varValue + ".floatValue()";
        if (isTypeFromTo(processingEnv, source, destination, Integer.class, Float.class)) return varValue + "==null ? null : " + varValue + ".floatValue()";
        if (isTypeFromTo(processingEnv, source, destination, Integer.class, double.class)) return varValue + "==null ? 0 : " + varValue + ".doubleValue()";
        if (isTypeFromTo(processingEnv, source, destination, Integer.class, Double.class)) return varValue + "==null ? null : " + varValue + ".doubleValue()";

        //long
        if (isTypeFromTo(processingEnv, source, destination, long.class, byte.class)) return "(byte)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, long.class, Byte.class)) return "(byte)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, long.class, short.class)) return "(short)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, long.class, Short.class)) return "(short)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, long.class, int.class)) return "(int)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, long.class, Integer.class)) return "(int)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, long.class, float.class)) return "(float)"+varValue;
        if (isTypeFromTo(processingEnv, source, destination, long.class, Float.class)) return "(float)"+varValue;
        if (isTypeFromTo(processingEnv, source, destination, long.class, double.class)) return "(double)"+varValue;
        if (isTypeFromTo(processingEnv, source, destination, long.class, Double.class)) return "(double)"+varValue;

        if (isTypeFromTo(processingEnv, source, destination, Long.class, byte.class)) return varValue + "==null ? 0 :" + varValue + ".byteValue()";
        if (isTypeFromTo(processingEnv, source, destination, Long.class, Byte.class)) return varValue + "==null ? null :" + varValue + ".byteValue()";
        if (isTypeFromTo(processingEnv, source, destination, Long.class, short.class)) return varValue + "==null ? 0 :" + varValue + ".shortValue()";
        if (isTypeFromTo(processingEnv, source, destination, Long.class, Short.class)) return varValue + "==null ? null :" + varValue + ".shortValue()";
        if (isTypeFromTo(processingEnv, source, destination, Long.class, int.class)) return varValue + "==null ? 0 :" + varValue + ".intValue()";
        if (isTypeFromTo(processingEnv, source, destination, Long.class, Integer.class)) return varValue + "==null ? null :" + varValue + ".intValue()";
        if (isTypeFromTo(processingEnv, source, destination, Long.class, float.class)) return varValue + "==null ? 0 :" + varValue + ".floatValue()";
        if (isTypeFromTo(processingEnv, source, destination, Long.class, Float.class)) return varValue + "==null ? null :" + varValue + ".floatValue()";
        if (isTypeFromTo(processingEnv, source, destination, Long.class, double.class)) return varValue + "==null ? 0 :" + varValue + ".doubleValue()";
        if (isTypeFromTo(processingEnv, source, destination, Long.class, Double.class)) return varValue + "==null ? null :" + varValue + ".doubleValue()";

        //float
        if (isTypeFromTo(processingEnv, source, destination, float.class, byte.class)) return "(byte)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, float.class, Byte.class)) return "(byte)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, float.class, short.class)) return "(short)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, float.class, Short.class)) return "(short)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, float.class, int.class)) return "(int)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, float.class, Integer.class)) return "(int)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, float.class, long.class)) return "(long)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, float.class, Long.class)) return "(long)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, float.class, double.class)) return "(double)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, float.class, Double.class)) return "(double)" + varValue;

        if (isTypeFromTo(processingEnv, source, destination, Float.class, byte.class)) return varValue + "==null ? 0 :" + varValue + ".byteValue()";
        if (isTypeFromTo(processingEnv, source, destination, Float.class, Byte.class)) return varValue + "==null ? null :" + varValue + ".byteValue()";
        if (isTypeFromTo(processingEnv, source, destination, Float.class, short.class)) return varValue + "==null ? 0 :" + varValue + ".shortValue()";
        if (isTypeFromTo(processingEnv, source, destination, Float.class, Short.class)) return varValue + "==null ? null :" + varValue + ".shortValue()";
        if (isTypeFromTo(processingEnv, source, destination, Float.class, int.class)) return varValue + "==null ? 0 :" + varValue + ".intValue()";
        if (isTypeFromTo(processingEnv, source, destination, Float.class, Integer.class)) return varValue + "==null ? null :" + varValue + ".intValue()";
        if (isTypeFromTo(processingEnv, source, destination, Float.class, long.class)) return varValue + "==null ? 0 :" + varValue + ".longValue()";
        if (isTypeFromTo(processingEnv, source, destination, Float.class, Long.class)) return varValue + "==null ? null :" + varValue + ".longValue()";
        if (isTypeFromTo(processingEnv, source, destination, Float.class, double.class)) return varValue + "==null ? 0 :" + varValue + ".doubleValue()";
        if (isTypeFromTo(processingEnv, source, destination, Float.class, Double.class)) return varValue + "==null ? null :" + varValue + ".doubleValue()";

        //double
        if (isTypeFromTo(processingEnv, source, destination, double.class, byte.class)) return "(byte)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, double.class, Byte.class)) return "(byte)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, double.class, short.class)) return "(short)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, double.class, Short.class)) return "(short)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, double.class, int.class)) return "(int)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, double.class, Integer.class)) return "(int)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, double.class, long.class)) return "(long)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, double.class, Long.class)) return "(long)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, double.class, float.class)) return "(float)" + varValue;
        if (isTypeFromTo(processingEnv, source, destination, double.class, Float.class)) return "(float)" + varValue;

        if (isTypeFromTo(processingEnv, source, destination, Double.class, byte.class)) return varValue + "==null ? 0 :" + varValue + ".byteValue()";
        if (isTypeFromTo(processingEnv, source, destination, Double.class, Byte.class)) return varValue + "==null ? null :" + varValue + ".byteValue()";
        if (isTypeFromTo(processingEnv, source, destination, Double.class, short.class)) return varValue + "==null ? 0 :" + varValue + ".shortValue()";
        if (isTypeFromTo(processingEnv, source, destination, Double.class, Short.class)) return varValue + "==null ? null :" + varValue + ".shortValue()";
        if (isTypeFromTo(processingEnv, source, destination, Double.class, int.class)) return varValue + "==null ? 0 :" + varValue + ".intValue()";
        if (isTypeFromTo(processingEnv, source, destination, Double.class, Integer.class)) return varValue + "==null ? null :" + varValue + ".intValue()";
        if (isTypeFromTo(processingEnv, source, destination, Double.class, long.class)) return varValue + "==null ? 0 :" + varValue + ".longValue()";
        if (isTypeFromTo(processingEnv, source, destination, Double.class, Long.class)) return varValue + "==null ? null :" + varValue + ".longValue()";
        if (isTypeFromTo(processingEnv, source, destination, Double.class, float.class)) return varValue + "==null ? 0 :" + varValue + ".floatValue()";
        if (isTypeFromTo(processingEnv, source, destination, Double.class, Float.class)) return varValue + "==null ? null :" + varValue + ".floatValue()";

        //string number mapping ------------------------------------------------------------------------------------

        if (isTypeFromTo(processingEnv, source, destination, String.class, boolean.class)) return varValue + "==null ? false : Boolean.parseBoolean(" + varValue + ")";
        if (isTypeFromTo(processingEnv, source, destination, String.class, Boolean.class)) return varValue + "==null ? null : Boolean.parseBoolean(" + varValue + ")";
        if (isTypeFromTo(processingEnv, source, destination, String.class, byte.class)) return varValue + "==null ? 0 : Byte.parseByte(" + varValue + ")";
        if (isTypeFromTo(processingEnv, source, destination, String.class, Byte.class)) return varValue + "==null ? null : Byte.parseByte(" + varValue + ")";
        if (isTypeFromTo(processingEnv, source, destination, String.class, short.class)) return varValue + "==null ? 0 : Short.parseShort(" + varValue + ")";
        if (isTypeFromTo(processingEnv, source, destination, String.class, Short.class)) return varValue + "==null ? null : Short.parseShort(" + varValue + ")";
        if (isTypeFromTo(processingEnv, source, destination, String.class, int.class)) return varValue + "==null ? 0 : Integer.parseInt(" + varValue + ")";
        if (isTypeFromTo(processingEnv, source, destination, String.class, Integer.class)) return varValue + "==null ? null : Integer.parseInt(" + varValue + ")";
        if (isTypeFromTo(processingEnv, source, destination, String.class, long.class)) return varValue + "==null ? 0 : Long.parseLong(" + varValue + ")";
        if (isTypeFromTo(processingEnv, source, destination, String.class, Long.class)) return varValue + "==null ? null : Long.parseLong(" + varValue + ")";
        if (isTypeFromTo(processingEnv, source, destination, String.class, float.class)) return varValue + "==null ? 0 : Float.parseFloat(" + varValue + ")";
        if (isTypeFromTo(processingEnv, source, destination, String.class, Float.class)) return varValue + "==null ? null : Float.parseFloat(" + varValue + ")";
        if (isTypeFromTo(processingEnv, source, destination, String.class, double.class)) return varValue + "==null ? 0 : Double.parseDouble(" + varValue + ")";
        if (isTypeFromTo(processingEnv, source, destination, String.class, Double.class)) return varValue + "==null ? null : Double.parseDouble(" + varValue + ")";
        if (isTypeFromTo(processingEnv, source, destination, String.class, char.class)) return varValue + "==null ? '\\u0000' : " + varValue + ".charAt(0)";
        if (isTypeFromTo(processingEnv, source, destination, String.class, Character.class)) return varValue + "==null ? null : " + varValue + ".charAt(0)";

        if (isTypeFromTo(processingEnv, source, destination, boolean.class, String.class)) return varValue + "+\"\"";
        if (isTypeFromTo(processingEnv, source, destination, Boolean.class, String.class)) return varValue + "==null ? null : " + varValue + "+\"\"";
        if (isTypeFromTo(processingEnv, source, destination, byte.class, String.class)) return varValue + "+\"\"";
        if (isTypeFromTo(processingEnv, source, destination, Byte.class, String.class)) return varValue + "==null ? null : " + varValue + "+\"\"";
        if (isTypeFromTo(processingEnv, source, destination, short.class, String.class)) return varValue + "+\"\"";
        if (isTypeFromTo(processingEnv, source, destination, Short.class, String.class)) return varValue + "==null ? null : " + varValue + "+\"\"";
        if (isTypeFromTo(processingEnv, source, destination, int.class, String.class)) return varValue + "+\"\"";
        if (isTypeFromTo(processingEnv, source, destination, Integer.class, String.class)) return varValue + "==null ? null : " + varValue + "+\"\"";
        if (isTypeFromTo(processingEnv, source, destination, long.class, String.class)) return varValue + "+\"\"";
        if (isTypeFromTo(processingEnv, source, destination, Long.class, String.class)) return varValue + "==null ? null : " + varValue + "+\"\"";
        if (isTypeFromTo(processingEnv, source, destination, float.class, String.class)) return varValue + "+\"\"";
        if (isTypeFromTo(processingEnv, source, destination, Float.class, String.class)) return varValue + "==null ? null : " + varValue + "+\"\"";
        if (isTypeFromTo(processingEnv, source, destination, double.class, String.class)) return varValue + "+\"\"";
        if (isTypeFromTo(processingEnv, source, destination, Double.class, String.class)) return varValue + "==null ? null : " + varValue + "+\"\"";
        if (isTypeFromTo(processingEnv, source, destination, char.class, String.class)) return varValue + "+\"\"";
        if (isTypeFromTo(processingEnv, source, destination, Character.class, String.class)) return varValue + "==null ? null : " + varValue + "+\"\"";

        //BigDecimal, BigInteger mapping ----------------------------------------------------------------------------

        if (isTypeFromTo(processingEnv, source, destination, byte.class, BigDecimal.class)) return "new BigDecimal(" + varValue + "+\"\")";
        if (isTypeFromTo(processingEnv, source, destination, Byte.class, BigDecimal.class)) return varValue + "==null ? null : new BigDecimal("+varValue+"+\"\")";
        if (isTypeFromTo(processingEnv, source, destination, short.class, BigDecimal.class)) return "new BigDecimal(" + varValue + "+\"\")";
        if (isTypeFromTo(processingEnv, source, destination, Short.class, BigDecimal.class)) return varValue + "==null ? null : new BigDecimal("+varValue+"+\"\")";
        if (isTypeFromTo(processingEnv, source, destination, int.class, BigDecimal.class)) return "new BigDecimal(" + varValue + "+\"\")";
        if (isTypeFromTo(processingEnv, source, destination, Integer.class, BigDecimal.class)) return varValue + "==null ? null : new BigDecimal("+varValue+"+\"\")";
        if (isTypeFromTo(processingEnv, source, destination, long.class, BigDecimal.class)) return "new BigDecimal(" + varValue + "+\"\")";
        if (isTypeFromTo(processingEnv, source, destination, Long.class, BigDecimal.class)) return varValue + "==null ? null : new BigDecimal("+varValue+"+\"\")";
        if (isTypeFromTo(processingEnv, source, destination, float.class, BigDecimal.class)) return "new BigDecimal(" + varValue + "+\"\")";
        if (isTypeFromTo(processingEnv, source, destination, Float.class, BigDecimal.class)) return varValue + "==null ? null : new BigDecimal("+varValue+"+\"\")";
        if (isTypeFromTo(processingEnv, source, destination, double.class, BigDecimal.class)) return "new BigDecimal(" + varValue + "+\"\")";
        if (isTypeFromTo(processingEnv, source, destination, Double.class, BigDecimal.class)) return varValue + "==null ? null : new BigDecimal("+varValue+"+\"\")";
        if (isTypeFromTo(processingEnv, source, destination, String.class, BigDecimal.class)) return varValue + "==null ? null : new BigDecimal("+varValue+"+\"\")";

        if (isTypeFromTo(processingEnv, source, destination, BigDecimal.class, byte.class)) return varValue + "==null ? 0 : "+varValue+".byteValue()";
        if (isTypeFromTo(processingEnv, source, destination, BigDecimal.class, Byte.class)) return varValue + "==null ? null : "+varValue+".byteValue()";
        if (isTypeFromTo(processingEnv, source, destination, BigDecimal.class, short.class)) return varValue + "==null ? 0 : "+varValue+".shortValue()";
        if (isTypeFromTo(processingEnv, source, destination, BigDecimal.class, Short.class)) return varValue + "==null ? null : "+varValue+".shortValue()";
        if (isTypeFromTo(processingEnv, source, destination, BigDecimal.class, int.class)) return varValue + "==null ? 0 : "+varValue+".intValue()";
        if (isTypeFromTo(processingEnv, source, destination, BigDecimal.class, Integer.class)) return varValue + "==null ? null : "+varValue+".intValue()";
        if (isTypeFromTo(processingEnv, source, destination, BigDecimal.class, long.class)) return varValue + "==null ? 0 : "+varValue+".longValue()";
        if (isTypeFromTo(processingEnv, source, destination, BigDecimal.class, Long.class)) return varValue + "==null ? null : "+varValue+".longValue()";
        if (isTypeFromTo(processingEnv, source, destination, BigDecimal.class, float.class)) return varValue + "==null ? 0 : "+varValue+".floatValue()";
        if (isTypeFromTo(processingEnv, source, destination, BigDecimal.class, Float.class)) return varValue + "==null ? null : "+varValue+".floatValue()";
        if (isTypeFromTo(processingEnv, source, destination, BigDecimal.class, double.class)) return varValue + "==null ? 0 : "+varValue+".doubleValue()";
        if (isTypeFromTo(processingEnv, source, destination, BigDecimal.class, Double.class)) return varValue + "==null ? null : "+varValue+".doubleValue()";
        if (isTypeFromTo(processingEnv, source, destination, BigDecimal.class, String.class)) return varValue + "==null ? null : "+varValue+".toString()";


        if (isTypeFromTo(processingEnv, source, destination, byte.class, BigInteger.class)) return "new BigInteger(" + varValue + "+\"\")";
        if (isTypeFromTo(processingEnv, source, destination, Byte.class, BigInteger.class)) return varValue + "==null ? null : new BigInteger("+varValue+"+\"\")";
        if (isTypeFromTo(processingEnv, source, destination, short.class, BigInteger.class)) return "new BigInteger(" + varValue + "+\"\")";
        if (isTypeFromTo(processingEnv, source, destination, Short.class, BigInteger.class)) return varValue + "==null ? null : new BigInteger("+varValue+"+\"\")";
        if (isTypeFromTo(processingEnv, source, destination, int.class, BigInteger.class)) return "new BigInteger(" + varValue + "+\"\")";
        if (isTypeFromTo(processingEnv, source, destination, Integer.class, BigInteger.class)) return varValue + "==null ? null : new BigInteger("+varValue+"+\"\")";
        if (isTypeFromTo(processingEnv, source, destination, long.class, BigInteger.class)) return "new BigInteger(" + varValue + "+\"\")";
        if (isTypeFromTo(processingEnv, source, destination, Long.class, BigInteger.class)) return varValue + "==null ? null : new BigInteger("+varValue+"+\"\")";
        if (isTypeFromTo(processingEnv, source, destination, String.class, BigInteger.class)) return varValue + "==null ? null : new BigInteger("+varValue+"+\"\")";

        if (isTypeFromTo(processingEnv, source, destination, BigInteger.class, byte.class)) return varValue + "==null ? 0 : "+varValue+".byteValue()";
        if (isTypeFromTo(processingEnv, source, destination, BigInteger.class, Byte.class)) return varValue + "==null ? null : "+varValue+".byteValue()";
        if (isTypeFromTo(processingEnv, source, destination, BigInteger.class, short.class)) return varValue + "==null ? 0 : "+varValue+".shortValue()";
        if (isTypeFromTo(processingEnv, source, destination, BigInteger.class, Short.class)) return varValue + "==null ? null : "+varValue+".shortValue()";
        if (isTypeFromTo(processingEnv, source, destination, BigInteger.class, int.class)) return varValue + "==null ? 0 : "+varValue+".intValue()";
        if (isTypeFromTo(processingEnv, source, destination, BigInteger.class, Integer.class)) return varValue + "==null ? null : "+varValue+".intValue()";
        if (isTypeFromTo(processingEnv, source, destination, BigInteger.class, long.class)) return varValue + "==null ? 0 : "+varValue+".longValue()";
        if (isTypeFromTo(processingEnv, source, destination, BigInteger.class, Long.class)) return varValue + "==null ? null : "+varValue+".longValue()";
        if (isTypeFromTo(processingEnv, source, destination, BigInteger.class, float.class)) return varValue + "==null ? 0 : "+varValue+".floatValue()";
        if (isTypeFromTo(processingEnv, source, destination, BigInteger.class, Float.class)) return varValue + "==null ? null : "+varValue+".floatValue()";
        if (isTypeFromTo(processingEnv, source, destination, BigInteger.class, double.class)) return varValue + "==null ? 0 : "+varValue+".doubleValue()";
        if (isTypeFromTo(processingEnv, source, destination, BigInteger.class, Double.class)) return varValue + "==null ? null : "+varValue+".doubleValue()";
        if (isTypeFromTo(processingEnv, source, destination, BigInteger.class, String.class)) return varValue + "==null ? null : "+varValue+".toString()";

        return null;
    }
}
