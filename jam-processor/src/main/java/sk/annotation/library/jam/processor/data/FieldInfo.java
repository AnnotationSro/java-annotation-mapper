package sk.annotation.library.jam.processor.data;

import org.apache.commons.lang.StringUtils;
import sk.annotation.library.jam.processor.sourcewriter.ImportsTypeDefinitions;
import sk.annotation.library.jam.processor.sourcewriter.SourceGenerator;
import sk.annotation.library.jam.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.jam.processor.sourcewriter.SourceRegisterImports;

import javax.lang.model.element.Modifier;
import java.util.Optional;

public class FieldInfo implements SourceGenerator, SourceRegisterImports {
	final private TypeWithVariableInfo variable;

	public FieldInfo(TypeInfo type) {
		this.variable =  new TypeWithVariableInfo(type);
		this.variable.modifiers.add(Modifier.PROTECTED);
		this.variable.inlineMode=false;
	}
	public FieldInfo(String name, TypeInfo type) {
		this.variable =  new TypeWithVariableInfo(name, type);
		this.variable.modifiers.add(Modifier.PROTECTED);
	}

	public FieldInfo withInjections(AnnotationsInfo injections) {
		this.variable.withAnnotations(injections);
		this.variable.inlineMode=false;
		return this;
	}


	public String getName() {
		return variable.getVariableName();
	}


	private Optional<TypeInfo> customFieldInstance = null;
	protected TypeInfo getFieldTypeForInstance(SourceGeneratorContext ctx) {
		if (true) return null;
//		if (customFieldInstance  == null) {
//			TypeInfo type = variable.getType();
//			if (type != null && type.getParameterTypes().isEmpty()) {
//				String fullName = ElementUtils.findGeneratedMapperClass(ctx.processingEnv, type.getFullClassName());
//				if (fullName!=null) customFieldInstance = Optional.of(new TypeInfo(fullName));
//			}
//			if (customFieldInstance  == null) {
//				customFieldInstance = Optional.empty();
//			}
//		}
		return customFieldInstance.orElse(null);
	}

	@Override
	public boolean writeSourceCode(SourceGeneratorContext ctx) {
		variable.writeSourceCode(ctx);

		// If this is generated mapper - we can create instance of this
		TypeInfo typeForInstance = getFieldTypeForInstance(ctx);
		if (typeForInstance != null) {
			// TODO: Check how we can INITIALIZE it
			ctx.pw.print(" = new ");
			typeForInstance.writeSourceCode(ctx);
			ctx.pw.print("()");
		}
		ctx.pw.print(";");

		// Setter
		ctx.pw.print("\npublic void set");
		ctx.pw.print(StringUtils.capitalize(variable.getVariableName()));
		ctx.pw.print("(");
		variable.writeSourceCode(ctx, true, false);
		ctx.pw.print(") {");

		ctx.pw.print("\n\tthis.");
		ctx.pw.print(variable.getVariableName());
		ctx.pw.print(" = ");
		ctx.pw.print(variable.getVariableName());
		ctx.pw.print(";\n}");
		return true;
	}

	@Override
	public void registerImports(SourceGeneratorContext ctx, ImportsTypeDefinitions imports) {
		variable.registerImports(ctx, imports);
		SourceRegisterImports.runIfPresent(getFieldTypeForInstance(ctx), ctx, imports);
	}

}
