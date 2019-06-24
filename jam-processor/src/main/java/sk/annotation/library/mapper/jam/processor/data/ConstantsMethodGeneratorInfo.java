package sk.annotation.library.mapper.jam.processor.data;

import org.apache.commons.lang.StringEscapeUtils;
import sk.annotation.library.mapper.jam.processor.sourcewriter.ImportsTypeDefinitions;
import sk.annotation.library.mapper.jam.processor.sourcewriter.SourceGenerator;
import sk.annotation.library.mapper.jam.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.mapper.jam.processor.sourcewriter.SourceRegisterImports;

import javax.lang.model.element.ExecutableElement;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConstantsMethodGeneratorInfo implements SourceGenerator, SourceRegisterImports {

	//	for Every Constants: protected static final int {constanctsForTopMethods.key} = "{constanctsForTopMethods.value}".hashCode();
	final public Map<String, String> constanctsForTopMethods = new LinkedHashMap<>();

	public String registerTopMethod (ExecutableElement method) {
		String constantFieldName = "__constantMethod" + (constanctsForTopMethods.size()+1) + "_" + method.getSimpleName();
		constanctsForTopMethods.put(constantFieldName,method.toString());
		return constantFieldName;
	}

	@Override
	public void writeSourceCode(SourceGeneratorContext ctx) {

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
	}

	@Override
	public void registerImports(SourceGeneratorContext ctx, ImportsTypeDefinitions imports) {
	}



}
