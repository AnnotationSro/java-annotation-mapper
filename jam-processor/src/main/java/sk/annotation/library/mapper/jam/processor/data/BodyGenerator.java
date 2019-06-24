package sk.annotation.library.mapper.jam.processor.data;

import sk.annotation.library.mapper.jam.processor.sourcewriter.ImportsTypeDefinitions;
import sk.annotation.library.mapper.jam.processor.sourcewriter.SourceGenerator;
import sk.annotation.library.mapper.jam.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.mapper.jam.processor.sourcewriter.SourceRegisterImports;

import java.util.LinkedList;
import java.util.List;

public class BodyGenerator implements SourceRegisterImports, SourceGenerator {
	private final List<Object> objs = new LinkedList<>();

	public void add(Object ... objs) {
		if (objs == null) return;
		for (Object obj : objs) {
			if (obj == null) continue;
			this.objs.add(obj);
		}
	}


	@Override
	public void writeSourceCode(SourceGeneratorContext ctx) {
		for (Object obj : objs) {
			if (obj instanceof SourceGenerator) {
				((SourceGenerator) obj).writeSourceCode(ctx);
				continue;
			}
			ctx.pw.print(obj.toString());
		}
	}

	@Override
	public void registerImports(SourceGeneratorContext ctx, ImportsTypeDefinitions imports) {
		for (Object obj : objs) {
			if (obj instanceof SourceRegisterImports) {
				((SourceRegisterImports) obj).registerImports(ctx, imports);
			}
		}
	}
}
