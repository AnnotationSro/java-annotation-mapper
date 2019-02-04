package sk.annotation.library.mapper.fast.processor.data.methodgenerator;

import com.sun.tools.javac.code.Type;
import lombok.Getter;
import sk.annotation.library.mapper.fast.processor.data.*;
import sk.annotation.library.mapper.fast.processor.data.keys.MethodConfigKey;
import sk.annotation.library.mapper.fast.processor.data.mapi.MethodApiFullSyntax;
import sk.annotation.library.mapper.fast.processor.data.mapi.MethodApiKey;
import sk.annotation.library.mapper.fast.processor.sourcewriter.ImportsTypeDefinitions;
import sk.annotation.library.mapper.fast.processor.sourcewriter.SourceGenerator;
import sk.annotation.library.mapper.fast.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.mapper.fast.processor.sourcewriter.SourceRegisterImports;
import sk.annotation.library.mapper.fast.processor.utils.NameUtils;
import sk.annotation.library.mapper.fast.processor.utils.TypeUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;
import java.util.*;

abstract public class AbstractMethodSourceInfo implements SourceGenerator, SourceRegisterImports {
	@Getter
	final protected MethodApiFullSyntax methodApiFullSyntax;
	final protected MapperClassInfo ownerClassInfo;
	final protected List<SourceRegisterImports> sourcesForImports = new LinkedList<>();
	final protected Set<String> usedNames = new HashSet<>(); // in method context !!!

	public AbstractMethodSourceInfo(MapperClassInfo ownerClassInfo, MethodApiFullSyntax methodApiFullSyntax) {
		this.ownerClassInfo = ownerClassInfo;
		this.methodApiFullSyntax = methodApiFullSyntax;
		this.usedNames.addAll(ownerClassInfo.getUsedNames());
		for (MethodParamInfo param : this.methodApiFullSyntax.getParams()) {
			this.usedNames.add(param.getVariable().getName());
		}
	}

	abstract protected void analyzeAndGenerateDependMethods(ProcessingEnvironment processingEnv, MethodConfigKey forMethodConfig);

	@Override
	public void writeSourceCode(SourceGeneratorContext ctx) {
		methodApiFullSyntax.writeMethodDeclaration(ctx);
		ctx.pw.print(" {");
		ctx.pw.levelSpaceUp();

		List<MethodParamInfo> requiredParams = methodApiFullSyntax.getRequiredParams();
		if (!requiredParams.isEmpty()) {
			ctx.pw.print("\n// check null inputs ");
			ctx.pw.print("\nif (");
			boolean addAnd = false;
			for (MethodParamInfo requiredParam : requiredParams) {
				if (addAnd) ctx.pw.print(" && ");
				addAnd = true;
				ctx.pw.print(requiredParam.getVariable().getName());
				ctx.pw.print("==null");
			}
			ctx.pw.print(") return");
			if (methodApiFullSyntax.getReturnType()!=null) {
				ctx.pw.print(" null");
			}
			ctx.pw.print(";\n");
		}







		writeSourceCodeBody(ctx);

		ctx.pw.levelSpaceDown();
		ctx.pw.print("\n}");
	}
	abstract protected void writeSourceCodeBody(SourceGeneratorContext ctx);
//	{
//		if (!bodyGenerator.isEmpty()) {
//			boolean advanceModeRequied = bodyGenerator.size() == 1;
//
//			boolean separatorMethodsRequired = false;
//			for (Map.Entry<MethodConfigKey, BodyGenerator> entry : bodyGenerator.entrySet()) {
//				MethodConfigKey key = entry.getKey();
//				BodyGenerator body = entry.getValue();
//
//				if (separatorMethodsRequired) {
//					ctx.pw.printNewLine();
//				}
//				separatorMethodsRequired = true;
//
//				if (advanceModeRequied) {
//					ctx.pw.print("\n// TODO: Implements for ");
//					ctx.pw.print(key.getForTopMethod());
//					ctx.pw.print("\n{");
//					ctx.pw.levelSpaceUp();
//				}
//				body.writeSourceCode(ctx);
//				if (advanceModeRequied) {
//					ctx.pw.levelSpaceDown();
//					ctx.pw.print("\n}");
//				}
//			}
//		}
//
//	}

	@Override
	public void registerImports(SourceGeneratorContext ctx, ImportsTypeDefinitions imports) {
		methodApiFullSyntax.registerImports(ctx, imports);
		for (SourceRegisterImports v : sourcesForImports) {
			v.registerImports(ctx, imports);
		}
	}



	protected MethodCallApi findOrCreateOwnMethod(ProcessingEnvironment processingEnv, String requiredMethodName, TypeInfo sourceType, TypeInfo destinationType) {
		return findOrCreateOwnMethod(processingEnv, requiredMethodName, sourceType.getType(processingEnv), destinationType.getType(processingEnv));
	}
	protected MethodCallApi findOrCreateOwnMethod(ProcessingEnvironment processingEnv, String requiredMethodName, TypeMirror sourceType, TypeMirror destinationType) {
		// Create transform value
		TypeInfo retType = new TypeInfo(destinationType);
		List<MethodParamInfo> subMethodParams = new LinkedList<>();
		subMethodParams.add(new MethodParamInfo(new TypeWithVariableInfo("in", new TypeInfo(sourceType)), null, false));
		subMethodParams.add(new MethodParamInfo(new TypeWithVariableInfo("out", retType), null, true));
		MethodApiKey transformApiKey = new MethodApiKey(retType, subMethodParams);

		// Vyhladame metodu, ale ak neexistuje, vytvorime si svoju vlastnu verziu
		MethodCallApi methodCallApi = ownerClassInfo.findMethodApiToCall(transformApiKey);
		if (methodCallApi!=null) return methodCallApi;


		// vytvorime vlastny mapper pre metodu ...
		String subMethodName = ownerClassInfo.findBestNewMethodName_transformFromTo(processingEnv, transformApiKey);
		MethodApiFullSyntax subMethodApiSyntax = new MethodApiFullSyntax(processingEnv, subMethodName, retType, subMethodParams);
		methodCallApi = ownerClassInfo.registerNewGeneratedMethod(findBestMethodGenerator(processingEnv, ownerClassInfo, subMethodApiSyntax, sourceType, destinationType));

		if (methodCallApi == null) throw new IllegalStateException("Unexpected situation !!!");


		return methodCallApi;
	}
	protected static AbstractMethodSourceInfo findBestMethodGenerator(ProcessingEnvironment processingEnv, MapperClassInfo ownerClassInfo, MethodApiFullSyntax subMethodApiSyntax, TypeMirror srcType, TypeMirror dstType) {

		// Zistenie, ci ide o kolekciu na kolekciu
		if (srcType==null || dstType==null) return new EmptyMethodSourceInfo(ownerClassInfo, subMethodApiSyntax);

		Type[] types = new Type[] {
				TypeUtils.getBaseTypeWithoutParametrizedFields(srcType),
				TypeUtils.getBaseTypeWithoutParametrizedFields(dstType)
		};

		// Implemented List
		if (isSameType(processingEnv, List.class, types)) {
			return new ListMethodSourceInfo(ownerClassInfo, subMethodApiSyntax);
		}


		// Defautl generator ...
		return new CopyFieldMethodSourceInfo(ownerClassInfo, subMethodApiSyntax);
	}
	protected static boolean isSameType(ProcessingEnvironment processingEnv, Class clsType, Type... types) {
		if (types == null || types.length == 0) return false;

		TypeMirror type = processingEnv.getElementUtils().getTypeElement(clsType.getCanonicalName()).asType();
		for (Type tp : types) {
			if (!processingEnv.getTypeUtils().isSameType(type, tp)) return false;
		}

		return true;
	}
}
