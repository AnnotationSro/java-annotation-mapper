package sk.annotation.library.jam.processor.sourcewriter;

import javax.annotation.processing.ProcessingEnvironment;

public interface SourceRegisterImports {
	public void registerImports(ProcessingEnvironment processingEnv, ImportsTypeDefinitions imports);


	static public void runIfPresent(SourceRegisterImports value, ProcessingEnvironment processingEnv, ImportsTypeDefinitions imports) {
		if (value != null) value.registerImports(processingEnv, imports);
	}
}
