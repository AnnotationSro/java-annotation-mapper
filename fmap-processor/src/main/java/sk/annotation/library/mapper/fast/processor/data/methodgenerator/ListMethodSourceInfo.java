package sk.annotation.library.mapper.fast.processor.data.methodgenerator;

import com.sun.tools.javac.code.Type;
import sk.annotation.library.mapper.fast.processor.data.MapperClassInfo;
import sk.annotation.library.mapper.fast.processor.data.MethodCallApi;
import sk.annotation.library.mapper.fast.processor.data.TypeConstructorInfo;
import sk.annotation.library.mapper.fast.processor.data.confwrappers.FieldMappingData;
import sk.annotation.library.mapper.fast.processor.data.keys.MethodConfigKey;
import sk.annotation.library.mapper.fast.processor.data.mapi.MethodApiFullSyntax;
import sk.annotation.library.mapper.fast.processor.sourcewriter.ImportsTypeDefinitions;
import sk.annotation.library.mapper.fast.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.mapper.fast.processor.utils.NameUtils;
import sk.annotation.library.mapper.fast.processor.utils.TypeUtils;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListMethodSourceInfo extends AbstractMethodSourceInfo {
	public ListMethodSourceInfo(MapperClassInfo ownerClassInfo, MethodApiFullSyntax methodApiParams) {
		super(ownerClassInfo, methodApiParams);
	}


	private MethodCallApi methodCallApi = null;
	private TypeConstructorInfo listConstructorType = null;
	private boolean analyzeRequired = true;

	@Override
	protected void analyzeAndGenerateDependMethods(ProcessingEnvironment processingEnv, MethodConfigKey forMethodConfig) {
		//nothing todo
		if (analyzeRequired) {
			analyzeRequired = false;

			listConstructorType = new TypeConstructorInfo(methodApiFullSyntax.getReturnType(), false);

			// Najdeme typy (z akeho na aky)
			List<Type> dstTypeList = TypeUtils.getParametrizedTypes(this.methodApiFullSyntax.getReturnType().getType(processingEnv));
			List<Type> srcTypeList = TypeUtils.getParametrizedTypes(this.methodApiFullSyntax.getParams().get(0).getVariable().getType().getType(processingEnv));
			if (dstTypeList!=null && srcTypeList!=null && dstTypeList.size()==1 && srcTypeList.size()==1) {
				/*  Mozno tu bude treba zistit context tejto metody */
				methodCallApi = findOrCreateOwnMethod(processingEnv, null, srcTypeList.get(0), dstTypeList.get(0));
			}
		}

		// volaj referenciu pre typ ...
		if (methodCallApi != null && methodCallApi.getOutGeneratedMethod() != null) {
			methodCallApi.getOutGeneratedMethod().analyzeAndGenerateDependMethods(processingEnv, forMethodConfig);
		}
	}

	@Override
	public void registerImports(SourceGeneratorContext ctx, ImportsTypeDefinitions imports) {
		super.registerImports(ctx, imports);
		if (listConstructorType!=null) listConstructorType.registerImports(ctx, imports);
	}

	@Override
	protected void writeSourceCodeBody(SourceGeneratorContext ctx) {
		// return
		String srcVarName = this.methodApiFullSyntax.getRequiredParams().get(0).getVariable().getName();
		String dstVarName = this.methodApiFullSyntax.getRequiredParams().get(1).getVariable().getName();

		ctx.pw.print("\nif (");
		ctx.pw.print(srcVarName);
		ctx.pw.print(" == null) return out;");

		ctx.pw.print("\nif (");
		ctx.pw.print(dstVarName);
		ctx.pw.print(" == null) {");
		ctx.pw.print("\n\t");
		ctx.pw.print(dstVarName);
		ctx.pw.print(" = ");
		listConstructorType.writeSourceCodeWithParams(ctx, srcVarName + " == null ? 0 : " + srcVarName + ".size()");
		ctx.pw.print(";");
		ctx.pw.print("\n}");
		ctx.pw.print("\nelse {\n\t" + dstVarName  + ".clear();\n}");

		ctx.pw.print("\n\n// Copy values");
		if (methodCallApi!=null) {
			String name = NameUtils.findBestName(this.usedNames, "s");
			this.usedNames.add(name);

			ctx.pw.print("\nfor (");
			methodCallApi.getMethodSyntax().getParams().get(0).getVariable().getType().writeSourceCode(ctx);
			ctx.pw.print(" ");
			ctx.pw.print(name);
			ctx.pw.print(" : ");
			ctx.pw.print(srcVarName);
			ctx.pw.print(") {");
			ctx.pw.print("\n\t");
			ctx.pw.print(dstVarName);
			ctx.pw.print(".add(");

			List<String> params = new ArrayList<>(2);
			params.add(name);
			params.add("null");
			methodCallApi.genSourceForCall(ctx, params, Collections.emptyMap());

			ctx.pw.print(");");


			ctx.pw.print("\n}");
		}

		ctx.pw.print(";");

		ctx.pw.print("\nreturn ");
		ctx.pw.print(dstVarName);
		ctx.pw.print(";");
	}

}
