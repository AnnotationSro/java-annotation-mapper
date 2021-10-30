package sk.annotation.library.jam.processor.data;

import sk.annotation.library.jam.processor.sourcewriter.ImportsTypeDefinitions;
import sk.annotation.library.jam.processor.sourcewriter.SourceGenerator;
import sk.annotation.library.jam.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.jam.processor.sourcewriter.SourceRegisterImports;
import sk.annotation.library.jam.processor.utils.commons.StringEscapeUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConstantsMethodGeneratorInfo implements SourceGenerator, SourceRegisterImports {

	//	for Every Constants: protected static final int {constanctsForTopMethods.key} = "{constanctsForTopMethods.value}".hashCode();
	final public Map<String, String> constanctsForTopMethods = new LinkedHashMap<>();

	public String registerTopMethod (ExecutableElement method, MapperClassInfo ownerClassInfo) {
		String constantFieldName = "__constantMethod" + (constanctsForTopMethods.size() + 1) + "_" + method.getSimpleName();
		constanctsForTopMethods.put(constantFieldName, method.toString());
		return constantFieldName;
	}

	@Override
	public boolean writeSourceCode(SourceGeneratorContext ctx) {

		// static fields !!!
		for (Map.Entry<String, String> entry : constanctsForTopMethods.entrySet()) {
			ctx.pw.print("\nprotected static final int ");
			ctx.pw.print(entry.getKey());
			ctx.pw.print(" = ");
			ctx.pw.print("\"");
			ctx.pw.print(StringEscapeUtils.escapeJava(entry.getValue()));
			ctx.pw.print("\".hashCode();");
		}

		if (!constanctsForTopMethods.isEmpty())	ctx.pw.print("\n");

		return true;
	}

	@Override
	public void registerImports(ProcessingEnvironment processingEnv, ImportsTypeDefinitions imports) {
	}



}
