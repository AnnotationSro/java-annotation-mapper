package sk.annotation.library.jam.processor.data;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import sk.annotation.library.jam.processor.sourcewriter.ImportsTypeDefinitions;
import sk.annotation.library.jam.processor.sourcewriter.SourceGenerator;
import sk.annotation.library.jam.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.jam.processor.sourcewriter.SourceRegisterImports;
import sk.annotation.library.jam.processor.utils.TypeUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;
import java.util.Objects;

public class TypeInfo implements SourceRegisterImports, SourceGenerator {
	public TypeMirror type;
	@Getter
	private Class clsType;
	private String rawName;

	public TypeInfo(ProcessingEnvironment processingEnv, String cls) {
		this(TypeUtils.convertToType(processingEnv, cls));

		if (clsType==null && type==null) {
			try {
				Class.forName(cls);
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException(e);
			}
		}
	}
	public TypeInfo(Class cls) {
		this(cls,null);
	}
	public TypeInfo(TypeMirror type) {
		this(null, type);
	}
	public TypeInfo(Class cls, TypeMirror type) {
		this.clsType = cls;
		this.type = type;
		if (type!=null) rawName = type.toString();
		else if (clsType != null) rawName = clsType.getCanonicalName();
	}
	static public TypeInfo analyzeReturnType(TypeMirror type) {
		if (type == null) return null;
		if (type instanceof NoType) return null;
		return new TypeInfo(type);
	}

	public TypeMirror getType(ProcessingEnvironment processingEnv) {
		if (type == null && processingEnv!=null) {
			if (clsType == null) return null;
			type = TypeUtils.convertToTypeMirror(processingEnv, clsType);
		}

		return type;
	}

	@Override
	public void registerImports(ProcessingEnvironment processingEnv, ImportsTypeDefinitions imports) {
		imports.registerImports(processingEnv, getType(processingEnv));
	}

	@Override
	public boolean writeSourceCode(SourceGeneratorContext ctx) {
		TypeMirror type = getType(ctx.processingEnv);
		String resolvedType = ctx.javaClassWriter.imports.resolveType(type);
		// https://github.com/AnnotationSro/java-annotation-mapper/issues/28 - remove not supported constructions "? extends "
		resolvedType = StringUtils.replace(resolvedType, "? extends ", "");
		ctx.pw.print(resolvedType);
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TypeInfo typeInfo = (TypeInfo) o;
		return Objects.equals(rawName, typeInfo.rawName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(rawName);
	}

	@Deprecated
	public String getSimpleClassName() {
		return null;
	}

}
