package sk.annotation.library.mapper.fast.processor.data;

import sk.annotation.library.mapper.fast.processor.data.TypeInfo;
import sk.annotation.library.mapper.fast.processor.sourcewriter.ImportsTypeDefinitions;
import sk.annotation.library.mapper.fast.processor.sourcewriter.SourceGenerator;
import sk.annotation.library.mapper.fast.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.mapper.fast.processor.sourcewriter.SourceRegisterImports;
import sk.annotation.library.mapper.fast.processor.utils.TypeUtils;

import javax.annotation.processing.ProcessingEnvironment;

public class TypeConstructorInfo implements SourceGenerator, SourceRegisterImports {
	final private TypeInfo typeOriginal;
	private TypeInfo typeConstructor = null;
	private boolean constructorReference = false;

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
	public void writeSourceCode(SourceGeneratorContext ctx) {
		writeSourceCodeWithParams(ctx);
	}
	public void writeSourceCodeWithParams(SourceGeneratorContext ctx, String... params) {
		boolean withParams = params!=null && params.length>0;

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
			for (int i = 0; i < params.length; i++) {
				if (i>0) ctx.pw.print(",");
				ctx.pw.print(params[i]);
			}
		}
		ctx.pw.print(")");
	}

	@Override
	public void registerImports(SourceGeneratorContext ctx, ImportsTypeDefinitions imports) {
		getTypeConstructor(ctx.processingEnv).registerImports(ctx, imports);
	}
}
