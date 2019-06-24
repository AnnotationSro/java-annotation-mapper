package sk.annotation.library.mapper.jam.processor.data.methodgenerator;

import com.sun.tools.javac.code.Type;
import lombok.Getter;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import sk.annotation.library.mapper.jam.annotations.enums.MapperFeature;
import sk.annotation.library.mapper.jam.processor.Constants;
import sk.annotation.library.mapper.jam.processor.data.*;
import sk.annotation.library.mapper.jam.processor.data.constructors.TypeConstructorInfo;
import sk.annotation.library.mapper.jam.processor.data.keys.MethodConfigKey;
import sk.annotation.library.mapper.jam.processor.data.mapi.MethodApiFullSyntax;
import sk.annotation.library.mapper.jam.processor.data.mapi.MethodApiKey;
import sk.annotation.library.mapper.jam.processor.sourcewriter.ImportsTypeDefinitions;
import sk.annotation.library.mapper.jam.processor.sourcewriter.SourceGenerator;
import sk.annotation.library.mapper.jam.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.mapper.jam.processor.sourcewriter.SourceRegisterImports;
import sk.annotation.library.mapper.jam.processor.utils.NameUtils;
import sk.annotation.library.mapper.jam.processor.utils.TypeUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;
import java.util.*;

abstract public class AbstractMethodSourceInfo implements SourceGenerator, SourceRegisterImports {
    @Getter
    final protected MethodApiFullSyntax methodApiFullSyntax;
    @Getter
    final protected MapperClassInfo ownerClassInfo;
    final protected List<SourceRegisterImports> sourcesForImports = new LinkedList<>();
    final protected Set<String> usedNames = new HashSet<>(); // in method context !!!

    public AbstractMethodSourceInfo(MapperClassInfo ownerClassInfo, MethodApiFullSyntax methodApiFullSyntax) {
        this.ownerClassInfo = ownerClassInfo;
        this.methodApiFullSyntax = methodApiFullSyntax;
        this.usedNames.addAll(ownerClassInfo.getUsedNames());
        for (TypeWithVariableInfo param : this.methodApiFullSyntax.getParams()) {
            this.usedNames.add(param.getVariableName());
        }
    }

    abstract protected void analyzeAndGenerateDependMethods(ProcessingEnvironment processingEnv, MethodConfigKey forMethodConfig);

    @Getter
    protected TypeWithVariableInfo varCtxVariable;
    @Getter
    protected TypeWithVariableInfo varCtxMethodId;
    @Getter
    protected TypeWithVariableInfo varRet;

    @Override
    public void writeSourceCode(SourceGeneratorContext ctx) {
        methodApiFullSyntax.writeMethodDeclaration(ctx);
        ctx.pw.print(" {");
        ctx.pw.levelSpaceUp();

        List<TypeWithVariableInfo> requiredParams = methodApiFullSyntax.getRequiredParams();
        if (!requiredParams.isEmpty()) {
            ctx.pw.print("\n// check null inputs ");
            ctx.pw.print("\nif (");
            boolean addAnd = false;
            for (TypeWithVariableInfo requiredParam : requiredParams) {
                if (requiredParam.isMarkedAsReturn()) continue;
                if (addAnd) ctx.pw.print(" && ");
                addAnd = true;
                ctx.pw.print(requiredParam.getVariableName());
                ctx.pw.print("==null");
            }
            ctx.pw.print(") return");
            if (methodApiFullSyntax.getReturnType() != null) {
                ctx.pw.print(" null");
            }
            ctx.pw.print(";\n");
        }


        this.usedNames.clear();
        this.usedNames.addAll(ownerClassInfo.getUsedNames());
        for (TypeWithVariableInfo param : this.methodApiFullSyntax.getParams()) {
            this.usedNames.add(param.getVariableName());
        }
        varCtxMethodId = MethodCallApi.ctx_findVariable(Constants.methodParamInfo_ctxForMethodId, methodApiFullSyntax.getParams());
        if (varCtxMethodId != null) usedNames.add(varCtxMethodId.getVariableName());

        varCtxVariable = MethodCallApi.ctx_findVariable(Constants.methodParamInfo_ctxForRunData, methodApiFullSyntax.getParams());
        if (varCtxVariable != null) usedNames.add(varCtxVariable.getVariableName());

        varRet = null;
        if (methodApiFullSyntax.getReturnType() != null) {
            for (TypeWithVariableInfo param : methodApiFullSyntax.getParams()) {
                if (param.isMarkedAsReturn()) {
                    varRet = param;
                    break;
                }
            }
            if (varRet == null) {
                String bestRetName = NameUtils.findBestNameAndUpdateSet(this.usedNames, "ret");
                varRet = new TypeWithVariableInfo(bestRetName, methodApiFullSyntax.getReturnType(), null, false);

                if (!(this instanceof DeclaredMethodSourceInfo)) {
                    ctx.pw.printNewLine();
                    varRet.writeSourceCode(ctx, true, false);
                    ctx.pw.print(" = null;");
                }
            }
        }
        if (varRet != null) usedNames.add(varRet.getVariableName());

        //// Tu bude test
        writeSourceCodeBody(ctx);

        if (!(this instanceof DeclaredMethodSourceInfo)) {
            if (methodApiFullSyntax.getReturnType() != null) {
                ctx.pw.print("\nreturn " + varRet.getVariableName() + ";");
            }
        }

        ctx.pw.levelSpaceDown();
        ctx.pw.print("\n}");
    }

    protected void writeSourceInstanceCacheLoad(SourceGeneratorContext ctx, TypeWithVariableInfo input, TypeWithVariableInfo varRet) {
        if (!ownerClassInfo.getFeatures().isDisabled_CYCLIC_MAPPING()) {
            ctx.pw.printNewLine();
            ctx.pw.print("\n// Check cyclic mapping - can disable " + ownerClassInfo.getFeatures().getInfoHowCanBeDisabled(MapperFeature.PREVENT_CYCLIC_MAPPING));
            ctx.pw.print("\n" + Constants.typeInstanceCacheValue.getClsType().getSimpleName() + "<");
            varRet.getVariableType().writeSourceCode(ctx);
            ctx.pw.print(
                    "> cacheValue = "
                            + varCtxVariable.getVariableName()
                            + ".getInstanceCache().getCacheValues(\""+ StringEscapeUtils.escapeJava(methodApiFullSyntax.getName())
                            +"\", "
                            + input.getVariableName()
                            + ");"
            );

            ctx.pw.print("\nif ("+varRet.getVariableName()+"==null) ");
            ctx.pw.print("\n\tif (cacheValue.isRegisteredAnyValue()) return cacheValue.getValue();");
            ctx.pw.print("\n");
            ctx.pw.print("\nelse if (cacheValue.isRegistered(" + varRet.getVariableName() + ")");
            ctx.pw.print(") \n\treturn " + varRet.getVariableName() + ";\n\n");
        }
    }
    protected void writeSourceInstanceCacheRegister(SourceGeneratorContext ctx, TypeWithVariableInfo input, TypeWithVariableInfo varRet) {
        if (!ownerClassInfo.getFeatures().isDisabled_CYCLIC_MAPPING()) {
            if (varCtxVariable==null) throw new IllegalStateException("Illegal state work");
                ctx.pw.print("\ncacheValue.registerValue("+ varRet.getVariableName()+ ");"
            );
        }
    }

    protected void writeConstructor(SourceGeneratorContext ctx, TypeWithVariableInfo field) {
        MethodApiKey constructorApiKey = new MethodApiKey(field.getVariableType(), Collections.emptyList());
        MethodCallApi methodCallApi = ownerClassInfo.findMethodApiToCall(constructorApiKey);
        if (methodCallApi != null) {
            if (StringUtils.isNotEmpty(methodCallApi.getPathToSyntax())) {
                ctx.pw.print(methodCallApi.getPathToSyntax());
                ctx.pw.print(".");
                ctx.pw.print(methodCallApi.getMethodSyntax().getName());
                ctx.pw.print("()");
                return;
            }

            ctx.pw.print(ownerClassInfo.getSimpleClassName());
            ctx.pw.print(".this.");
            ctx.pw.print(methodCallApi.getMethodSyntax().getName());
            ctx.pw.print("()");
            return;
        }

        // Todo - check Collections & Interfaces & Default Public Constructors !!!
        new TypeConstructorInfo(varRet.getVariableType(), false).writeSourceCode(ctx);
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
        if (!ownerClassInfo.getFeatures().isDisabled_CYCLIC_MAPPING()) {
            Constants.typeInstanceCacheValue.registerImports(ctx, imports);
        }
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
        TypeInfo inType = new TypeInfo(sourceType);
        TypeInfo retType = new TypeInfo(destinationType);

        // Complete same types +
        if (TypeUtils.isSame(processingEnv, inType, retType) && TypeUtils.isKnownImmutableType(processingEnv, inType)) return null;

        List<TypeWithVariableInfo> subMethodParams = new LinkedList<>();
        subMethodParams.add(Constants.methodParamInfo_ctxForMethodId);
        if (!ownerClassInfo.getFeatures().isDisabledToUseMapperRunCtxData()) {
            subMethodParams.add(Constants.methodParamInfo_ctxForRunData);
        }
        subMethodParams.add(new TypeWithVariableInfo("in", inType, null, false));
        subMethodParams.add(new TypeWithVariableInfo("out", retType, null, true));
        MethodApiKey transformApiKey = new MethodApiKey(retType, subMethodParams);

        // Vyhladame metodu, ale ak neexistuje, vytvorime si svoju vlastnu verziu
        MethodCallApi methodCallApi = ownerClassInfo.findMethodApiToCall(transformApiKey);
        if (methodCallApi != null) return methodCallApi;


        // vytvorime vlastny mapper pre metodu ...
        String subMethodName = ownerClassInfo.findBestNewMethodName_transformFromTo(processingEnv, transformApiKey);
        MethodApiFullSyntax subMethodApiSyntax = new MethodApiFullSyntax(processingEnv, subMethodName, retType, subMethodParams);
        methodCallApi = ownerClassInfo.registerNewGeneratedMethod(findBestMethodGenerator(processingEnv, ownerClassInfo, subMethodApiSyntax, sourceType, destinationType));

        if (methodCallApi == null) throw new IllegalStateException("Unexpected situation !!!");


        return methodCallApi;
    }

    protected static AbstractMethodSourceInfo findBestMethodGenerator(ProcessingEnvironment processingEnv, MapperClassInfo ownerClassInfo, MethodApiFullSyntax subMethodApiSyntax, TypeMirror srcType, TypeMirror dstType) {

        // Zistenie, ci ide o kolekciu na kolekciu
        if (srcType == null || dstType == null) return new EmptyMethodSourceInfo(ownerClassInfo, subMethodApiSyntax);

        Type[] types = new Type[]{
                TypeUtils.getBaseTypeWithoutParametrizedFields(srcType),
                TypeUtils.getBaseTypeWithoutParametrizedFields(dstType)
        };

        // Implemented List
        if (isSameType(processingEnv, List.class, types)) {
            return new SimpleMethodApi_List_SourceInfo(ownerClassInfo, subMethodApiSyntax);
        }


        // Implemented List
        if (isSameType(processingEnv, Map.class, types)) {
            return new SimpleMethodApi_Map_SourceInfo(ownerClassInfo, subMethodApiSyntax);
        }


        // Defautl generator ...
        return new SimpleMethodApi_CopyField_SourceInfo(ownerClassInfo, subMethodApiSyntax);
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
