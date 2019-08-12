package sk.annotation.library.jam.processor.data.constructors;

import sk.annotation.library.jam.processor.data.TypeInfo;
import sk.annotation.library.jam.processor.sourcewriter.ImportsTypeDefinitions;
import sk.annotation.library.jam.processor.sourcewriter.SourceGenerator;
import sk.annotation.library.jam.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.jam.processor.sourcewriter.SourceRegisterImports;
import sk.annotation.library.jam.processor.utils.TypeUtils;

import javax.annotation.processing.ProcessingEnvironment;

public class TypeConstructorInfo implements SourceGenerator, SourceRegisterImports {
	final private TypeInfo typeOriginal;
	private TypeInfo typeConstructor = null;
	private boolean constructorReference;

	public TypeConstructorInfo(TypeInfo type, boolean constructorReference) {
		this.typeOriginal = type;
		this.constructorReference = constructorReference;

	}

	protected TypeInfo getTypeConstructor(ProcessingEnvironment processingEnv) {
		if (typeConstructor == null) {
			this.typeConstructor = TypeUtils.resolveConstructorType(processingEnv, typeOriginal);
		}
		return typeConstructor;
	}

	@Override
	public boolean writeSourceCode(SourceGeneratorContext ctx) {
		writeSourceCodeWithParams(ctx);
		return true;
	}
	public void writeSourceCodeWithParams(SourceGeneratorContext ctx, String... sourceAsParams) {
		boolean withParams = sourceAsParams!=null && sourceAsParams.length>0;

		if (!withParams && constructorReference) {
			getTypeConstructor(ctx.processingEnv).writeSourceCode(ctx);
			ctx.pw.print("::new");
			return;
		}

		if (constructorReference) {
			ctx.pw.print("() -> ");
		}

		ctx.pw.print("new ");
		getTypeConstructor(ctx.processingEnv).writeSourceCode(ctx);
		ctx.pw.print("(");
		if (withParams) {
			for (int i = 0; i < sourceAsParams.length; i++) {
				if (i>0) ctx.pw.print(",");
				ctx.pw.print(sourceAsParams[i]);
			}
		}
		ctx.pw.print(")");
	}

	@Override
	public void registerImports(ProcessingEnvironment processingEnv, ImportsTypeDefinitions imports) {
		getTypeConstructor(processingEnv).registerImports(processingEnv, imports);
	}
}
