package sk.annotation.library.mapper.fast.processor.data.methodgenerator;

import sk.annotation.library.mapper.fast.processor.data.MapperClassInfo;
import sk.annotation.library.mapper.fast.processor.data.keys.MethodConfigKey;
import sk.annotation.library.mapper.fast.processor.data.mapi.MethodApiFullSyntax;
import sk.annotation.library.mapper.fast.processor.sourcewriter.SourceGeneratorContext;

import javax.annotation.processing.ProcessingEnvironment;

public class EmptyMethodSourceInfo extends AbstractMethodSourceInfo {
	public EmptyMethodSourceInfo(MapperClassInfo ownerClassInfo, MethodApiFullSyntax methodApiParams) {
		super(ownerClassInfo, methodApiParams);
	}

	@Override
	protected void analyzeAndGenerateDependMethods(ProcessingEnvironment processingEnv, MethodConfigKey forMethodConfig) {
		//nothing todo
	}

	@Override
	public void writeSourceCode(SourceGeneratorContext ctx) {
		super.writeSourceCode(ctx);
	}

	@Override
	protected void writeSourceCodeBody(SourceGeneratorContext ctx) {
		// return
		if (methodApiFullSyntax.getReturnType()!=null) {
			if (methodApiFullSyntax.isReturnLastParam()) {
				ctx.pw.print("\nreturn ");
				ctx.pw.print(methodApiFullSyntax.getParams().get(methodApiFullSyntax.getParams().size()-1).getVariable().getName());
				ctx.pw.print(";");
			}
			else {
				// todo: mozny problem s primitivnymi typmi
				ctx.pw.print("\nreturn null;");
			}
		}

	}

}
