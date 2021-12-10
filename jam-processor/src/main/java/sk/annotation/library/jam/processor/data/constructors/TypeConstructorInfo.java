package sk.annotation.library.jam.processor.data.constructors;

import sk.annotation.library.jam.processor.data.TypeInfo;
import sk.annotation.library.jam.processor.sourcewriter.ImportsTypeDefinitions;
import sk.annotation.library.jam.processor.sourcewriter.SourceGenerator;
import sk.annotation.library.jam.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.jam.processor.sourcewriter.SourceRegisterImports;
import sk.annotation.library.jam.processor.utils.ElementUtils;
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
		TypeInfo typeConstructor = getTypeConstructor(ctx.processingEnv);

		// Todo - check Collections & Interfaces & Default Public Constructors !!!
		if (!ElementUtils.hasDefaultConstructor(ctx.processingEnv, typeOriginal.getType(ctx.processingEnv))) {
			ctx.pw.print("null /*NO DEFAULT CONSTRUCTOR*/");
			return;
		}


		boolean withParams = sourceAsParams!=null && sourceAsParams.length>0;
		boolean isArray = typeOriginal.isArray(ctx.processingEnv);

		if (constructorReference && !withParams && !isArray) {
			typeConstructor.writeSourceCode(ctx);
			ctx.pw.print("::new");
			return;
		}

		if (constructorReference) {
			ctx.pw.print("() -> ");
		}

		ctx.pw.print("new ");
//		if (typeOriginal.isArray(ctx.processingEnv)) {
//
//		} else {
			typeConstructor.writeSourceCode(ctx);
//		}
		ctx.pw.print(isArray ? "[" : "(");
		if (withParams) {
			for (int i = 0; i < sourceAsParams.length; i++) {
				if (i>0) ctx.pw.print(",");
				ctx.pw.print(sourceAsParams[i]);
			}
		}
		ctx.pw.print(isArray ? "]" : ")");
	}

	@Override
	public void registerImports(ProcessingEnvironment processingEnv, ImportsTypeDefinitions imports) {
		getTypeConstructor(processingEnv).registerImports(processingEnv, imports);
	}
}
