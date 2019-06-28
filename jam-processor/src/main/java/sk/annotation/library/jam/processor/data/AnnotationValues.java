package sk.annotation.library.jam.processor.data;

import org.apache.commons.lang.StringEscapeUtils;
import sk.annotation.library.jam.annotations.Context;
import sk.annotation.library.jam.processor.sourcewriter.SourceGenerator;
import sk.annotation.library.jam.processor.sourcewriter.SourceGeneratorContext;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AnnotationValues implements SourceGenerator {
	final private Map<String, String> values = new LinkedHashMap<>();

	public AnnotationValues withStringValue(String value) {
		return withKeyAndStringValue("value", value);
	}
	public AnnotationValues withKeyAndStringValue(String key, String value) {
		if (key == null) return this;
		if (value == null) {
			values.remove(key);
			return this;
		}

		values.put(key, "\""+ StringEscapeUtils.escapeJava(value) +"\"");
		return this;
	}

	public AnnotationValues mergeValues(AnnotationValues newValues) {
		if (newValues != null) {
			// replacing only :)
			values.putAll(newValues.values);
		}
		return this;
	}


	private static final Map<String, String> customResolver;
	static {
		customResolver = new HashMap<>();
		customResolver.put("\""+ StringEscapeUtils.escapeJava(Context.jamConfif) +"\"", "Context.jamConfif");
		customResolver.put("\""+ StringEscapeUtils.escapeJava(Context.jamContext) +"\"", "Context.jamContext");

	}
	protected String resolveBestValue(String value) {
		return customResolver.getOrDefault(value, value);
	}

	@Override
	public void writeSourceCode(SourceGeneratorContext ctx) {
		if (values.isEmpty()) return;

		if (values.size()==1 && values.containsKey("value")) {
			ctx.pw.print("(");
			ctx.pw.print(resolveBestValue(values.get("value")));
			ctx.pw.print(")");
			return;
		}

		ctx.pw.print("(");
		boolean writeSeparator = false;
		for (Map.Entry<String, String> e : values.entrySet()) {
			if (writeSeparator) ctx.pw.print(", ");
			writeSeparator = true;
			ctx.pw.print(e.getKey());
			ctx.pw.print(" = ");
			ctx.pw.print(resolveBestValue(e.getValue()));
		}
		ctx.pw.print(")");
	}
}
