package sk.annotation.library.jam.example.ex14;

import sk.annotation.library.jam.annotations.DisableMapperFeature;
import sk.annotation.library.jam.annotations.Mapper;
import sk.annotation.library.jam.annotations.enums.MapperFeature;

import java.math.BigDecimal;
import java.math.BigInteger;

@Mapper
@DisableMapperFeature(MapperFeature.ALL)
public interface SimpleTypesMapper {
	boolean t1(Boolean obj);
	byte t2(Byte obj);
	short t3(Short obj);
	int t4(Integer obj);
	long t5(Long obj);
	float t6(Float obj);
	double t7(Double obj);
	char t8(Character obj);

	Boolean t9(boolean obj);
	Byte t10(byte obj);
	Short t11(short obj);
	Integer t12(int obj);
	Long t13(long obj);
	Float t14(float obj);
	Double t15(double obj);
	Character t16(char obj);

	short t17(byte obj);
	Short t18(byte obj);
	int t19(byte obj);
	Integer t20(byte obj);
	long t21(byte obj);
	Long t22(byte obj);
	float t23(byte obj);
	Float t24(byte obj);
	double t25(byte obj);
	Double t26(byte obj);

	short t27(Byte obj);
	Short t28(Byte obj);
	int t29(Byte obj);
	Integer t30(Byte obj);
	long t31(Byte obj);
	Long t32(Byte obj);
	float t33(Byte obj);
	Float t34(Byte obj);
	double t35(Byte obj);
	Double t36(Byte obj);

	byte t37(short obj);
	Byte t38(short obj);
	int t39(short obj);
	Integer t40(short obj);
	long t41(short obj);
	Long t42(short obj);
	float t43(short obj);
	Float t44(short obj);
	double t45(short obj);
	Double t46(short obj);

	byte t47(Short obj);
	Byte t48(Short obj);
	int t49(Short obj);
	Integer t50(Short obj);
	long t51(Short obj);
	Long t52(Short obj);
	float t53(Short obj);
	Float t54(Short obj);
	double t55(Short obj);
	Double t56(Short obj);

	byte t57(int obj);
	Byte t58(int obj);
	short t59(int obj);
	Short t60(int obj);
	long t61(int obj);
	Long t62(int obj);
	float t63(int obj);
	Float t64(int obj);
	double t65(int obj);
	Double t66(int obj);

	byte t67(Integer obj);
	Byte t68(Integer obj);
	short t69(Integer obj);
	Short t70(Integer obj);
	long t71(Integer obj);
	Long t72(Integer obj);
	float t73(Integer obj);
	Float t74(Integer obj);
	double t75(Integer obj);
	Double t76(Integer obj);

	byte t77(long obj);
	Byte t78(long obj);
	short t79(long obj);
	Short t80(long obj);
	int t81(long obj);
	Integer t82(long obj);
	float t83(long obj);
	Float t84(long obj);
	double t85(long obj);
	Double t86(long obj);

	byte t87(Long obj);
	Byte t88(Long obj);
	short t89(Long obj);
	Short t90(Long obj);
	int t91(Long obj);
	Integer t92(Long obj);
	float t93(Long obj);
	Float t94(Long obj);
	double t95(Long obj);
	Double t96(Long obj);

	boolean t97(String obj);
	Boolean t98(String obj);
	byte t99(String obj);
	Byte t101(String obj);
	short t102(String obj);
	Short t103(String obj);
	int t104(String obj);
	Integer t105(String obj);
	float t106(String obj);
	Float t107(String obj);
	double t108(String obj);
	Double t109(String obj);
	char t110(String obj);
	Character t111(String obj);


	String t112(boolean obj);
	String t113(Boolean obj);
	String t114(byte obj);
	String t115(Byte obj);
	String t116(short obj);
	String t117(Short obj);
	String t118(int obj);
	String t119(Integer obj);
	String t120(float obj);
	String t121(Float obj);
	String t122(double obj);
	String t123(Double obj);
	String t124(char obj);
	String t125(Character obj);


	BigInteger t126(byte obj);
	BigInteger t127(Byte obj);
	BigInteger t128(short obj);
	BigInteger t129(Short obj);
	BigInteger t130(int obj);
	BigInteger t131(Integer obj);
	BigInteger t132(long obj);
	BigInteger t133(Long obj);
	BigInteger t134(String obj);

	byte t135(BigInteger obj);
	Byte t136(BigInteger obj);
	short t137(BigInteger obj);
	Short t138(BigInteger obj);
	int t139(BigInteger obj);
	Integer t140(BigInteger obj);
	float t141(BigInteger obj);
	Float t142(BigInteger obj);
	double t143(BigInteger obj);
	Double t144(BigInteger obj);
	String t145(BigInteger obj);


	BigDecimal t146(byte obj);
	BigDecimal t147(Byte obj);
	BigDecimal t148(short obj);
	BigDecimal t149(Short obj);
	BigDecimal t150(int obj);
	BigDecimal t151(Integer obj);
	BigDecimal t152(float obj);
	BigDecimal t153(Float obj);
	BigDecimal t154(double obj);
	BigDecimal t155(Double obj);
	BigDecimal t156(String obj);

	byte t157(BigDecimal obj);
	Byte t158(BigDecimal obj);
	short t159(BigDecimal obj);
	Short t160(BigDecimal obj);
	int t161(BigDecimal obj);
	Integer t162(BigDecimal obj);
	float t163(BigDecimal obj);
	Float t164(BigDecimal obj);
	double t165(BigDecimal obj);
	Double t166(BigDecimal obj);
	String t167(BigDecimal obj);

	byte t168(float obj);
	Byte t169(float obj);
	short t170(float obj);
	Short t171(float obj);
	int t172(float obj);
	Integer t173(float obj);
	long t174(float obj);
	Long t175(float obj);
	double t176(float obj);
	Double t177(float obj);

	byte t178(Float obj);
	Byte t179(Float obj);
	short t180(Float obj);
	Short t181(Float obj);
	int t182(Float obj);
	Integer t183(Float obj);
	long t184(Float obj);
	Long t185(Float obj);
	double t186(Float obj);
	Double t187(Float obj);

	byte t188(double obj);
	Byte t189(double obj);
	short t190(double obj);
	Short t191(double obj);
	int t192(double obj);
	Integer t193(double obj);
	long t194(double obj);
	Long t195(double obj);
	float t196(double obj);
	Float t197(double obj);

	byte t198(Double obj);
	Byte t199(Double obj);
	short t200(Double obj);
	Short t201(Double obj);
	int t202(Double obj);
	Integer t203(Double obj);
	long t204(Double obj);
	Long t205(Double obj);
	float t206(Double obj);
	Float t207(Double obj);
}
