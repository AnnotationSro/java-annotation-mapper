package sk.annotation.library.jam.processor.data.mapi;

import com.sun.tools.javac.code.Type;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import sk.annotation.library.jam.processor.data.AnnotationsInfo;
import sk.annotation.library.jam.processor.data.TypeInfo;
import sk.annotation.library.jam.processor.data.TypeWithVariableInfo;
import sk.annotation.library.jam.processor.sourcewriter.ImportsTypeDefinitions;
import sk.annotation.library.jam.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.jam.processor.sourcewriter.SourceRegisterImports;
import sk.annotation.library.jam.processor.utils.MsgConstants;
import sk.annotation.library.jam.processor.utils.TypeUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.tools.Diagnostic;
import java.util.*;

@Getter
public class MethodApiFullSyntax implements SourceRegisterImports {
	@Setter
	private String name;
	private final Set<Modifier> modifiers = new HashSet<>();
	final AnnotationsInfo annotations = new AnnotationsInfo();

	// contains full api of Method
	private List<TypeWithVariableInfo> params;
	@Setter
	private boolean returnLastParam = false;
	@Setter
	private boolean returnLastParamRequired = false;			//
	private TypeInfo returnType;

	final private MethodApiKey apiKey;

	public boolean isGenerateReturnParamRequired() {
		return isReturnLastParam() && isReturnLastParamRequired();
	}



	public MethodApiFullSyntax(ProcessingEnvironment processingEnv, String methodName, TypeInfo returnType, List<TypeWithVariableInfo> params, boolean returnLastParamRequired) {
		this.name = methodName;
		this.returnType = returnType;
		if (params == null) params = Collections.emptyList();
		this.params = params;

		if (!params.isEmpty()) {
			int paramSize = params.size();
			returnLastParam = params.get(paramSize-1).isMarkedAsReturn();

			int i = 0;
			for (TypeWithVariableInfo param : params) {
				int paramIndex = i++;

				// Neccessary Validation
				if (param.isMarkedAsReturn()) {
					if (paramIndex < paramSize-1) {
						throw new IllegalArgumentException(MsgConstants.errorMethodParamWithReturnIsNotLast);
					}
					else if (!TypeUtils.isSame(processingEnv, returnType, param.getVariableType())) {
						throw new IllegalArgumentException(MsgConstants.errorMethodParamBadType);
					}
				}
			}
		}

		this.returnLastParamRequired = returnLastParamRequired && returnLastParam;
		apiKey = new MethodApiKey(returnType, params);
	}

	public List<TypeWithVariableInfo> getRequiredParams() {
		List<TypeWithVariableInfo> _getNeccessaryParams = new ArrayList<>(params.size());
		for (TypeWithVariableInfo param : params) {
			if (StringUtils.isNotEmpty(param.getHasContextKey())) continue;
			_getNeccessaryParams.add(param);
		}
		return _getNeccessaryParams;
	}

	static public MethodApiFullSyntax analyze(ProcessingEnvironment processingEnv, Type methodDeclaredInType, ExecutableElement method) {
		try {
			String name = method.getSimpleName().toString();
			ExecutableType methodType = TypeUtils.findType(processingEnv, methodDeclaredInType, method);

			TypeInfo returnType = TypeInfo.analyzeReturnType(methodType.getReturnType());

			List<TypeWithVariableInfo> params = new LinkedList<>();
			if (method.getParameters()!=null) {

				for (int i=0; i<method.getParameters().size(); i++) {
					VariableElement variableElement = method.getParameters().get(i);
					TypeWithVariableInfo param = TypeWithVariableInfo.analyze(variableElement, (Type) methodType.getParameterTypes().get(i));
					params.add(param);
				}
			}
			return new MethodApiFullSyntax(processingEnv, name, returnType, params, true);
		}
		catch (Exception e) {
			e.printStackTrace();
			processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, ExceptionUtils.getFullStackTrace(e), method);
		}

		/*try {
			String name = method.getSimpleName().toString();

			TypeInfo returnType = TypeInfo.analyzeReturnType(method.getReturnType());
			List<TypeWithVariableInfo> params = new LinkedList<>();
			if (method.getParameters()!=null) {

				for (VariableElement variableElement : method.getParameters()) {
					TypeWithVariableInfo param = TypeWithVariableInfo.analyze(variableElement);
					params.add(param);
				}
			}
			return new MethodApiFullSyntax(processingEnv, name, returnType, params, true);
		}
		catch (Exception e) {
			e.printStackTrace();
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, ExceptionUtils.getFullStackTrace(e), method);
			return null;
		}*/
		return null;
	}

	@Override
	public void registerImports(ProcessingEnvironment processingEnv, ImportsTypeDefinitions imports) {
		SourceRegisterImports.runIfPresent(returnType, processingEnv, imports);
		annotations.registerImports(processingEnv, imports);
		params.forEach(param -> SourceRegisterImports.runIfPresent(param, processingEnv, imports));
	}

	public void writeMethodDeclaration(SourceGeneratorContext ctx) {
		annotations.writeSourceCode(ctx);
		ctx.pw.print("\n");
		if (modifiers.isEmpty() || modifiers.contains(Modifier.PROTECTED)) ctx.pw.print("protected ");
		else if (modifiers.contains(Modifier.PUBLIC)) ctx.pw.print("public ");

		if (returnType!=null) {
			returnType.writeSourceCode(ctx);
		}
		else ctx.pw.print("void");
		ctx.pw.print(" ");
		ctx.pw.print(name);

		ctx.pw.print("(");
		boolean writeSeparator = false;
		for (TypeWithVariableInfo param : params) {
            if (param.isMarkedAsReturn() && !returnLastParamRequired) {
                continue;
            }

			if (writeSeparator) ctx.pw.print(", ");
			writeSeparator = true;
			param.writeSourceCode(ctx, true, true);

			if (param.isMarkedAsReturn()) {
				ctx.pw.print( " /* returnLastParamRequired="+returnLastParamRequired+" */ ");
			}
		}

		ctx.pw.print(")");
	}

}
