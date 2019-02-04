package sk.annotation.library.mapper.fast.processor.sourcewriter;

import org.apache.commons.lang.StringUtils;
import sk.annotation.library.mapper.fast.processor.utils.ElementUtils;

import javax.annotation.processing.ProcessingEnvironment;
import java.io.PrintWriter;

public class SourceGeneratorContext {
	public ProcessingEnvironment processingEnv;
	public JavaClassWriter javaClassWriter;
	public SourceWriter pw;



	public SourceGeneratorContext(ProcessingEnvironment processingEnv, JavaClassWriter javaClassWriter, PrintWriter pw) {
		this.processingEnv = processingEnv;
		this.javaClassWriter = javaClassWriter;
		this.pw = new SourceWriter(pw);
	}
}
