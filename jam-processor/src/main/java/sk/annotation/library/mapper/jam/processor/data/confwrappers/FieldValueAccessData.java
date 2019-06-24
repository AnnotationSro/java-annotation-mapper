package sk.annotation.library.mapper.jam.processor.data.confwrappers;

import lombok.Getter;
import lombok.Setter;
import sk.annotation.library.mapper.jam.processor.sourcewriter.ImportsTypeDefinitions;
import sk.annotation.library.mapper.jam.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.mapper.jam.processor.sourcewriter.SourceRegisterImports;
import sk.annotation.library.mapper.jam.processor.utils.TypeUtils;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

@Getter
@Setter
public class FieldValueAccessData implements SourceRegisterImports {
	final private String fieldName;
	private VariableElement field = null;
	private ExecutableElement setter = null;
	private ExecutableElement getter = null;

	public void setField(VariableElement field) {
		this.field = field;

		// TODO. Pridaj sem este kontrolu pre lombok Setter d Getter
//		List<? extends AnnotationMirror> annotationMirrors = this.field.getAnnotationMirrors();
//		for (AnnotationMirror annotationMirror : annotationMirrors) {
//			if (annotationMirror.getElementValues())
//		}
	}

	public FieldValueAccessData(String name) {
		this.fieldName = name;
	}

	public boolean isWritable() {
		return field != null && field.getModifiers().contains(Modifier.PUBLIC) || setter != null && setter.getModifiers().contains(Modifier.PUBLIC);
	}

	public boolean isReadable() {
		return field != null && field.getModifiers().contains(Modifier.PUBLIC) || getter != null && getter.getModifiers().contains(Modifier.PUBLIC);
	}

	public TypeMirror getTypeOfGetter() {
		return getType(true);
	}

	public TypeMirror getTypeOfSetter() {
		return getType(false);
	}

	private TypeMirror getType(boolean forGetter) {
		if (forGetter) {
			if (getter != null) getter.getReturnType();
			if (field != null) return TypeUtils.findType(field);
		} else {
			if (setter != null && !setter.getParameters().isEmpty())
				return TypeUtils.findType(setter.getParameters().get(0));
			if (field != null) return TypeUtils.findType(field);
		}

		return null;
	}

	public boolean useField(boolean forGetter) {
		if (forGetter) {
			if (getter != null && isReadable()) return false;
		} else {
			if (setter != null && !setter.getParameters().isEmpty() && isWritable()) return false;
		}
		return true;
	}

	public String getSourceForGetter(String varSrcName) {
		StringBuilder sb = new StringBuilder();
		sb.append(varSrcName);
		sb.append(".");
		if (useField(true)) {
			sb.append(fieldName);
		} else {
			sb.append(getter.getSimpleName().toString());
			sb.append("()");
		}
		return sb.toString();
	}

	public String[] getSourceForSetter(String varDestName) {
		if (useField(false)) {
			return new String[]{
					varDestName + "." + fieldName,
					" = ",
					""
			};
		}
		return new String[]{
				varDestName + "." + setter.getSimpleName(),
				" ( ",
				" )"
		};

	}

	@Override
	public void registerImports(SourceGeneratorContext ctx, ImportsTypeDefinitions imports) {
		imports.registerImports(ctx.processingEnv, getTypeOfGetter());
		imports.registerImports(ctx.processingEnv, getTypeOfSetter());
	}

	@Override
	public String toString() {
		return "field='" + fieldName + '\'';
	}

//	public boolean isSourceSameAsDestination(FieldValueAccessData field) {
//		return false;
//	}
}
