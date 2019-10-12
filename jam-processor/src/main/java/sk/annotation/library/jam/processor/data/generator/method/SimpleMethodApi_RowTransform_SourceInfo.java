package sk.annotation.library.jam.processor.data.generator.method;

import sk.annotation.library.jam.processor.data.MapperClassInfo;
import sk.annotation.library.jam.processor.data.TypeWithVariableInfo;
import sk.annotation.library.jam.processor.data.generator.row.AbstractRowValueTransformator;
import sk.annotation.library.jam.processor.data.keys.MethodConfigKey;
import sk.annotation.library.jam.processor.data.mapi.MethodApiFullSyntax;
import sk.annotation.library.jam.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.jam.processor.utils.NameUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;

public class SimpleMethodApi_RowTransform_SourceInfo extends EmptyMethodSourceInfo {

	final AbstractRowValueTransformator rowFieldGenerator;
	public SimpleMethodApi_RowTransform_SourceInfo(MapperClassInfo ownerClassInfo, MethodApiFullSyntax methodApiParams, AbstractRowValueTransformator rowFieldGenerator) {
		super(ownerClassInfo, methodApiParams);
		this.rowFieldGenerator = rowFieldGenerator;
	}

	@Override
	protected void analyzeAndGenerateDependMethods(ProcessingEnvironment processingEnv, MethodConfigKey forMethodConfig) {
	}

	@Override
	public boolean writeSourceCode(SourceGeneratorContext ctx) {
		methodApiFullSyntax.writeMethodDeclaration(ctx);
		ctx.pw.print(" {\n\t");
		ctx.pw.levelSpaceUp();

		this.writeSourceCodeBody(ctx);

		ctx.pw.levelSpaceDown();
		ctx.pw.print("\n}");

		return true;
	}

	@Override
	protected void writeSourceCodeBody(SourceGeneratorContext ctx) {
		if (varRet == null) {
			if (methodApiFullSyntax.getReturnType() != null) {
				for (TypeWithVariableInfo param : methodApiFullSyntax.getParams()) {
					if (param.isMarkedAsReturn()) {
						varRet = param;
						break;
					}
				}
				if (varRet == null) {
					String bestRetName = NameUtils.findBestNameAndUpdateSet(this.usedNames, "ret");
					varRet = new TypeWithVariableInfo(bestRetName, methodApiFullSyntax.getReturnType(), null, false);
				}
			}
			if (varRet != null) usedNames.add(varRet.getVariableName());
		}


		TypeWithVariableInfo varSrc = methodApiFullSyntax.getRequiredParams().get(0);
		TypeMirror srcType = varSrc.getVariableType().getType(ctx.processingEnv);
		TypeMirror dstType = methodApiFullSyntax.getReturnType().getType(ctx.processingEnv);

		varRet.writeSourceCode(ctx);
		ctx.pw.print(" = ");
		ctx.pw.print(rowFieldGenerator.generateRowTransform(ctx, srcType, dstType, varSrc.getVariableName()));
		ctx.pw.print(";");

		writeInterceptors(ctx, varSrc, varRet);

		ctx.pw.printNewLine();
		ctx.pw.print("return ");
		ctx.pw.print(varRet.getVariableName());
		ctx.pw.print(";");
	}
}
