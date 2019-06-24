package sk.annotation.library.mapper.jam.processor.sourcewriter;

import sk.annotation.library.mapper.jam.processor.data.FieldInfo;
import sk.annotation.library.mapper.jam.processor.data.MapperClassInfo;
import sk.annotation.library.mapper.jam.processor.data.TypeInfo;
import sk.annotation.library.mapper.jam.processor.data.methodgenerator.AbstractMethodSourceInfo;
import sk.annotation.library.mapper.jam.processor.utils.NameUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

public class JavaClassWriter implements SourceGenerator {

	final private MapperClassInfo mapperClassInfo;
	final public ImportsTypeDefinitions imports;

	public JavaClassWriter(MapperClassInfo mapperClassInfo) {
		this.mapperClassInfo = mapperClassInfo;

		this.imports = new ImportsTypeDefinitions(mapperClassInfo.parentElement);
	}


	public void writeSourceCode(ProcessingEnvironment processingEnv) {
		try {
			JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(mapperClassInfo.getFullClassName());
			try (
					Writer w = sourceFile.openWriter();
					PrintWriter pw = new PrintWriter(w);
			) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Generating mapper : " + sourceFile.getName());
				writeSourceCode(new SourceGeneratorContext(processingEnv, this, pw));
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void writeSourceCode(SourceGeneratorContext ctx) {

		// addMissingImports ...
		TypeInfo parentType = new TypeInfo(mapperClassInfo.parentElement.asType());
		parentType.registerImports(ctx, imports);
		mapperClassInfo.generateAnnotations.registerImports(ctx, imports);
		for (FieldInfo value : mapperClassInfo.fieldsToImplement) {
			value.registerImports(ctx, imports);
		}
		for (AbstractMethodSourceInfo m : mapperClassInfo.getMethodsToImplement().values()) {
			m.registerImports(ctx, imports);
		}

		// Package
		String packageName = NameUtils.getUpperPackage(mapperClassInfo.getFullClassName());
		if (packageName != null) {
			ctx.pw.print("package " + packageName + ";");
			ctx.pw.printNewLine();
		}

		// imports
		imports.writeSourceCode(ctx);

		// Class Annotations
		mapperClassInfo.generateAnnotations.writeSourceCode(ctx);

		// Class
		ctx.pw.printNewLine();
		if (mapperClassInfo.parentElement.getModifiers().contains(Modifier.PUBLIC)) ctx.pw.print("public ");
		if (mapperClassInfo.parentElement.getModifiers().contains(Modifier.PROTECTED)) ctx.pw.print("protected ");
//TODO: later		if (mapperClassInfo.parentElement.getModifiers().contains(Modifier.ABSTRACT)) ctx.pw.print("abstract ");
		ctx.pw.print("class " + mapperClassInfo.getSimpleClassName() + " ");
		ctx.pw.print(mapperClassInfo.parentTypeAsAbstractClass ? "extends " : "implements ");
		parentType.writeSourceCode(ctx);
		ctx.pw.print(" {");
		ctx.pw.printNewLine();

		ctx.pw.levelSpaceUp();

		// Generate Custom Fields
		mapperClassInfo.topMethodsRegistrator.writeSourceCode(ctx);

		// Generate Custom Fields
		for (FieldInfo fieldInfo : mapperClassInfo.fieldsToImplement) {
			fieldInfo.writeSourceCode(ctx);
			ctx.pw.printNewLine();
		}

		// Generate Methods
		for (AbstractMethodSourceInfo m : mapperClassInfo.getMethodsToImplement().values()) {
			m.writeSourceCode(ctx);
			ctx.pw.printNewLine();
		}

		// Class Ends
		ctx.pw.levelSpaceDown();
		ctx.pw.print("\n}");

		ctx.pw.flush();
	}

}
