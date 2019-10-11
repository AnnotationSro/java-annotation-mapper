package sk.annotation.library.jam.processor.sourcewriter;

import sk.annotation.library.jam.processor.data.FieldInfo;
import sk.annotation.library.jam.processor.data.MapperClassInfo;
import sk.annotation.library.jam.processor.data.TypeInfo;
import sk.annotation.library.jam.processor.data.generator.method.AbstractMethodSourceInfo;
import sk.annotation.library.jam.processor.utils.NameUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class JavaClassWriter implements SourceGenerator {

	final private MapperClassInfo mapperClassInfo;
	final public ImportsTypeDefinitions imports;

	public JavaClassWriter(MapperClassInfo mapperClassInfo) {
		this.mapperClassInfo = mapperClassInfo;

		this.imports = new ImportsTypeDefinitions(mapperClassInfo.parentElement);
	}


	public void writeSourceCode(ProcessingEnvironment processingEnv) {
		try {
			// addMissingImports ...
			TypeInfo parentType = new TypeInfo(mapperClassInfo.parentElement.asType());
			parentType.registerImports(processingEnv, imports);
			mapperClassInfo.generateAnnotations.registerImports(processingEnv, imports);
			for (FieldInfo value : mapperClassInfo.fieldsToImplement) {
				value.registerImports(processingEnv, imports);
			}
			for (AbstractMethodSourceInfo m : mapperClassInfo.getMethodsToImplement()) {
				m.registerImports(processingEnv, imports);
			}

			List<Element> originatingElements = new ArrayList<>();
			originatingElements.addAll(imports.dependOnElements.values());

			// Create source file
			JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(mapperClassInfo.getFullClassName(), originatingElements.toArray(new Element[0]));
			try (
					Writer w = sourceFile.openWriter();
					PrintWriter pw = new PrintWriter(w);
			) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Generating mapper : " + sourceFile.getName() + " - depends on: " + originatingElements);
				writeSourceCode(new SourceGeneratorContext(processingEnv, this, pw));
			}
		} catch (Exception e) {
			throw new IllegalStateException("Errror in " + mapperClassInfo.getFullClassName(), e);
		}
	}

	@Override
	public boolean writeSourceCode(SourceGeneratorContext ctx) {

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
		new TypeInfo(mapperClassInfo.parentElement.asType()).writeSourceCode(ctx);
		ctx.pw.print(" {");
		ctx.pw.printNewLine();

		ctx.pw.levelSpaceUp();

		// Generate Custom Fields
		mapperClassInfo.topMethodsRegistrator.writeSourceCode(ctx);

		// Generate Custom Fields
		for (FieldInfo fieldInfo : mapperClassInfo.fieldsToImplement) {
			if (fieldInfo.writeSourceCode(ctx)) {
				ctx.pw.printNewLine();
			}
		}

		// Generate Methods
		for (AbstractMethodSourceInfo m : mapperClassInfo.getMethodsToImplement()) {
			if (m.writeSourceCode(ctx)) {
				ctx.pw.printNewLine();
			}
		}

		// Class Ends
		ctx.pw.levelSpaceDown();
		ctx.pw.print("\n}");

		ctx.pw.flush();
		return true;
	}

}
