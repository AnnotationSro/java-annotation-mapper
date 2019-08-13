package sk.annotation.library.jam.processor.utils.annotations.data.fields;

import com.sun.tools.javac.code.Type;
import lombok.Getter;
import lombok.Setter;
import sk.annotation.library.jam.processor.utils.TypeUtils;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.List;

@Getter
@Setter
public class AnnotationFieldId {
	private List<Type> types;
	private String value;

	public boolean isTypeAcceptable(ProcessingEnvironment processingEnv, Type type) {
		if (types == null || types.isEmpty()) return true;
		for (Type tp : types) {
			if (TypeUtils.isAssignable(processingEnv, type, tp)) {
				return true;
			}
		}
		return false;
	}
}
