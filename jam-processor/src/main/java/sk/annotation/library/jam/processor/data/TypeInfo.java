package sk.annotation.library.jam.processor.data;

import sk.annotation.library.jam.processor.sourcewriter.ImportsTypeDefinitions;
import sk.annotation.library.jam.processor.sourcewriter.SourceGenerator;
import sk.annotation.library.jam.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.jam.processor.sourcewriter.SourceRegisterImports;
import sk.annotation.library.jam.processor.utils.TypeUtils;
import sk.annotation.library.jam.processor.utils.commons.StringUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;
import java.util.Objects;

public class TypeInfo implements SourceRegisterImports, SourceGenerator {
	public TypeMirror type;
	private Class clsType;

	public Class getClsType() {
		return clsType;
	}

	private String rawName;

	public TypeInfo(ProcessingEnvironment processingEnv, String cls) {
		this(TypeUtils.convertToTypeMirror(processingEnv, cls));

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
		this.type = TypeUtils.unwrapWildCardType(type);
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

	private Boolean _isArray = null;
	public boolean isArray(ProcessingEnvironment processingEnv) {
		if (_isArray == null) {
		 _isArray = TypeUtils.isArrayType(processingEnv, getType(processingEnv));
		}
		return _isArray;
	}


	@Override
	public void registerImports(ProcessingEnvironment processingEnv, ImportsTypeDefinitions imports) {
		imports.registerImports(processingEnv, getType(processingEnv));
	}

	public boolean writeSourceCode(SourceGeneratorContext ctx, boolean methodDeclaration) {
		TypeMirror type = getType(ctx.processingEnv);
		String resolvedType = ctx.javaClassWriter.imports.resolveType(type);
		if (!methodDeclaration) {
			// https://github.com/AnnotationSro/java-annotation-mapper/issues/28 - remove not supported constructions "? extends "
			resolvedType = StringUtils.replace(resolvedType, "? extends ", "").trim();
		}
		ctx.pw.print(resolvedType);
		return true;
	}
	@Override
	public boolean writeSourceCode(SourceGeneratorContext ctx) {
		return writeSourceCode(ctx, false);
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
