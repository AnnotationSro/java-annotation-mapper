package sk.annotation.library.jam.processor.data.confwrappers;

import lombok.Getter;
import lombok.Setter;
import sk.annotation.library.jam.processor.sourcewriter.ImportsTypeDefinitions;
import sk.annotation.library.jam.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.jam.processor.sourcewriter.SourceRegisterImports;
import sk.annotation.library.jam.processor.utils.LombokUtil;
import sk.annotation.library.jam.processor.utils.TypeUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.Set;

@Getter
public class FieldValueAccessData implements SourceRegisterImports {
	@Getter
	public static class AccessableTypeName {
		final private String name;
		final private boolean accessable;
		final private TypeMirror type;

		public AccessableTypeName(String name, boolean accessable, TypeMirror type) {
			this.name = name;
			this.accessable = accessable;
			this.type = type;
		}

		static boolean isAccessable(AccessableTypeName value) {
			return value!=null && value.accessable;
		}
	}


	final private String fieldName;
//	private Boolean fieldNamePublic = false;

	private AccessableTypeName field = null;
	private AccessableTypeName setter = null;
	private AccessableTypeName getter = null;

	public FieldValueAccessData(String name) {
		this.fieldName = name;
	}
	public void setField(VariableElement field) {
//		this.field = field;
		this.field = new AccessableTypeName(field.getSimpleName().toString(), field.getModifiers().contains(Modifier.PUBLIC), TypeUtils.findType(field));

		if (this.setter==null) {
			String setterName = LombokUtil.findLombokPublicSetter(field);
			if (setterName != null) {
				this.setter = new AccessableTypeName(setterName, true, this.field.getType());
			}
		}

		if (this.getter==null) {
			String getterName = LombokUtil.findLombokPublicGetter(field);
			if (getterName != null) {
				this.getter = new AccessableTypeName(getterName, true, this.field.getType());
			}
		}
	}



	public void setSetter(ExecutableElement value) {
		if (value.getParameters().size()!=1) return;
		this.setter = new AccessableTypeName(value.getSimpleName().toString(), value.getModifiers().contains(Modifier.PUBLIC), TypeUtils.findType(value.getParameters().get(0)));
	}
	public void setGetter(ExecutableElement value) {
		this.getter = new AccessableTypeName(value.getSimpleName().toString(), value.getModifiers().contains(Modifier.PUBLIC), value.getReturnType());
	}


	public boolean isWritable() {
		return AccessableTypeName.isAccessable(setter) || AccessableTypeName.isAccessable(field);
	}

	public boolean isReadable() {
		return AccessableTypeName.isAccessable(getter) || AccessableTypeName.isAccessable(field);
	}

	public TypeMirror getTypeOfGetter() {
		return getType(true);
	}

	public TypeMirror getTypeOfSetter() {
		return getType(false);
	}

	private TypeMirror getType(boolean forGetter) {
		if (forGetter) {
			if (getter != null) getter.getType();
			if (field != null) return field.getType();
		} else {
			if (setter != null) setter.getType();
			if (field != null) return field.getType();
		}

		return null;
	}

	private boolean useField(boolean forGetter) {
		if (forGetter) {
			if (getter != null && getter.isAccessable()) return false;
		} else {
			if (setter != null && setter.isAccessable()) return false;
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
			sb.append(getter.getName());
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
				varDestName + "." + setter.getName(),
				" ( ",
				" )"
		};

	}

	@Override
	public void registerImports(ProcessingEnvironment processingEnv, ImportsTypeDefinitions imports) {
		imports.registerImports(processingEnv, getTypeOfGetter());
		imports.registerImports(processingEnv, getTypeOfSetter());
	}

	@Override
	public String toString() {
		return "field='" + fieldName + '\'';
	}

//	public boolean isSourceSameAsDestination(FieldValueAccessData field) {
//		return false;
//	}
}
