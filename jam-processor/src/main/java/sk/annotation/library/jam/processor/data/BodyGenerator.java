package sk.annotation.library.jam.processor.data;

import sk.annotation.library.jam.processor.sourcewriter.ImportsTypeDefinitions;
import sk.annotation.library.jam.processor.sourcewriter.SourceGenerator;
import sk.annotation.library.jam.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.jam.processor.sourcewriter.SourceRegisterImports;

import javax.annotation.processing.ProcessingEnvironment;
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
	public boolean writeSourceCode(SourceGeneratorContext ctx) {
		for (Object obj : objs) {
			if (obj instanceof SourceGenerator) {
				((SourceGenerator) obj).writeSourceCode(ctx);
				continue;
			}
			ctx.pw.print(obj.toString());
		}
		return true;
	}

	@Override
	public void registerImports(ProcessingEnvironment processingEnv, ImportsTypeDefinitions imports) {
		for (Object obj : objs) {
			if (obj instanceof SourceRegisterImports) {
				((SourceRegisterImports) obj).registerImports(processingEnv, imports);
			}
		}
	}
}
