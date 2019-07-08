package sk.annotation.library.jam.processor.data.methodgenerator;

import com.sun.tools.javac.code.Type;
import org.apache.commons.lang.StringUtils;
import sk.annotation.library.jam.processor.data.MapperClassInfo;
import sk.annotation.library.jam.processor.data.MethodCallApi;
import sk.annotation.library.jam.processor.data.TypeInfo;
import sk.annotation.library.jam.processor.data.TypeWithVariableInfo;
import sk.annotation.library.jam.processor.data.confwrappers.FieldConfigurationResolver;
import sk.annotation.library.jam.processor.data.confwrappers.FieldMappingData;
import sk.annotation.library.jam.processor.data.confwrappers.FieldValueAccessData;
import sk.annotation.library.jam.processor.data.constructors.TypeConstructorInfo;
import sk.annotation.library.jam.processor.data.keys.MethodConfigKey;
import sk.annotation.library.jam.processor.data.mapi.MethodApiFullSyntax;
import sk.annotation.library.jam.processor.data.mapi.MethodApiKey;
import sk.annotation.library.jam.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.jam.processor.utils.NameUtils;
import sk.annotation.library.jam.processor.utils.TypeUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.*;

public class SimpleMethodApi_Enum_SourceInfo extends EmptyMethodSourceInfo {

	public SimpleMethodApi_Enum_SourceInfo(MapperClassInfo ownerClassInfo, MethodApiFullSyntax methodApiParams) {
		super(ownerClassInfo, methodApiParams);
	}

	@Override
	protected void analyzeAndGenerateDependMethods(ProcessingEnvironment processingEnv, MethodConfigKey forMethodConfig) {
	}

	@Override
	protected void writeSourceCodeBody(SourceGeneratorContext ctx) {
		List<TypeWithVariableInfo> requiredParams = methodApiFullSyntax.getRequiredParams();
		TypeWithVariableInfo varSrc = requiredParams.get(0);
		TypeWithVariableInfo varDst = requiredParams.get(1);
		String inputVarSrcName = varSrc.getVariableName();
		String inputVarDstName = varRet.getVariableName();
		this.usedNames.add(inputVarDstName);




		// remember Source / Destination
        TypeInfo typeSrc = varSrc.getVariableType();
        TypeInfo typeRet = varRet.getVariableType();

		List<String> enumSrc = TypeUtils.getEnumValues(ctx.processingEnv, typeSrc);
		List<String> enumDst = TypeUtils.getEnumValues(ctx.processingEnv, typeRet);

		ctx.pw.print("\nswitch(");
		ctx.pw.print(inputVarSrcName);
		ctx.pw.print(") {");

		Set<String> allConstants = new TreeSet<>();
		allConstants.addAll(enumSrc);
		allConstants.addAll(enumDst);

		for (String constantName : allConstants) {
			String error = null;
			if (!enumDst.contains(constantName)) {
				error = "Missing in DESTINATION";
			}
			else if (!enumSrc.contains(constantName)) {
				error = "Missing in SOURCE";
			}
			writeOneEnumValues(ctx, typeRet, constantName, error);
		}
		ctx.pw.print("\n}");
		ctx.pw.print("\nreturn null;");
	}


	protected static void writeOneEnumValues (SourceGeneratorContext ctx, TypeInfo typeRet, String src, String errorText) {
		ctx.pw.print("\n\t");
		if (errorText!=null) {
			ctx.pw.print("//todo: ");

			ctx.pw.print(errorText);
			ctx.pw.print(": ");
		}
		ctx.pw.print("case ");
		ctx.pw.print(src);
		ctx.pw.print(": return ");
		typeRet.writeSourceCode(ctx);
		ctx.pw.print(".");
		ctx.pw.print(src);
		ctx.pw.print(";");
	}
}
