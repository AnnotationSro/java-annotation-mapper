package sk.annotation.library.mapper.fast.processor.data;

import sk.annotation.library.mapper.fast.processor.sourcewriter.ImportsTypeDefinitions;
import sk.annotation.library.mapper.fast.processor.sourcewriter.SourceGenerator;
import sk.annotation.library.mapper.fast.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.mapper.fast.processor.sourcewriter.SourceRegisterImports;
import sk.annotation.library.mapper.fast.processor.utils.TypeUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;
import java.util.Objects;

public class TypeInfo implements SourceRegisterImports, SourceGenerator {
	public TypeMirror type;
	private Class clsType;
	private String rawName;

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
			type = TypeUtils.convertToType(processingEnv, clsType);
		}

		return type;
	}

	@Override
	public void registerImports(SourceGeneratorContext ctx, ImportsTypeDefinitions imports) {
		imports.registerImports(ctx.processingEnv, getType(ctx.processingEnv));
	}

	@Override
	public void writeSourceCode(SourceGeneratorContext ctx) {
		TypeMirror type = getType(ctx.processingEnv);
		String resolvedType = ctx.javaClassWriter.imports.resolveType(type);
		ctx.pw.print(resolvedType);
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
