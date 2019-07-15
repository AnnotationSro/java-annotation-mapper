package sk.annotation.library.jam.processor.data.generator.method;

import sk.annotation.library.jam.processor.data.MapperClassInfo;
import sk.annotation.library.jam.processor.data.keys.MethodConfigKey;
import sk.annotation.library.jam.processor.data.mapi.MethodApiFullSyntax;
import sk.annotation.library.jam.processor.sourcewriter.SourceGeneratorContext;

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
	protected void writeSourceCodeBody(SourceGeneratorContext ctx) {
		// return
		if (methodApiFullSyntax.getReturnType()!=null) {
			if (methodApiFullSyntax.isReturnLastParam()) {
				ctx.pw.print("\nreturn ");
				ctx.pw.print(methodApiFullSyntax.getParams().get(methodApiFullSyntax.getParams().size()-1).getVariableName());
				ctx.pw.print(";");
			}
			else {
				// TODO: possible problem with primitive types
				ctx.pw.print("\nreturn null;");
			}
		}

	}

}