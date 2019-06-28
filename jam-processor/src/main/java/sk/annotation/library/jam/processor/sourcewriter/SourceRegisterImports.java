package sk.annotation.library.jam.processor.sourcewriter;

public interface SourceRegisterImports {
	public void registerImports(SourceGeneratorContext ctx, ImportsTypeDefinitions imports);


	static public void runIfPresent(SourceRegisterImports value, SourceGeneratorContext ctx, ImportsTypeDefinitions imports) {
		if (value != null) value.registerImports(ctx, imports);
	}
}
