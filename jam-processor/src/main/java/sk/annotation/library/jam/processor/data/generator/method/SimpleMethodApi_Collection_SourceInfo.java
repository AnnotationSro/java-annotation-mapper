package sk.annotation.library.jam.processor.data.generator.method;

import com.sun.tools.javac.code.Type;
import sk.annotation.library.jam.processor.data.MapperClassInfo;
import sk.annotation.library.jam.processor.data.MethodCallApi;
import sk.annotation.library.jam.processor.data.TypeInfo;
import sk.annotation.library.jam.processor.data.TypeWithVariableInfo;
import sk.annotation.library.jam.processor.data.constructors.TypeConstructorInfo;
import sk.annotation.library.jam.processor.data.keys.MethodConfigKey;
import sk.annotation.library.jam.processor.data.mapi.MethodApiFullSyntax;
import sk.annotation.library.jam.processor.sourcewriter.ImportsTypeDefinitions;
import sk.annotation.library.jam.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.jam.processor.utils.NameUtils;
import sk.annotation.library.jam.processor.utils.TypeUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

public class SimpleMethodApi_Collection_SourceInfo extends AbstractMethodSourceInfo {
    public SimpleMethodApi_Collection_SourceInfo(MapperClassInfo ownerClassInfo, MethodApiFullSyntax methodApiParams) {
        super(ownerClassInfo, methodApiParams);
    }


    private MethodCallApi methodCallApi = null;
    private TypeConstructorInfo listConstructorType = null;
    private boolean analyzeRequired = true;

    private Type dstType = null;
    private Type srcType = null;

    @Override
    protected void analyzeAndGenerateDependMethods(ProcessingEnvironment processingEnv, MethodConfigKey forMethodConfig) {
        //nothing todo
        if (analyzeRequired) {
            analyzeRequired = false;

            listConstructorType = new TypeConstructorInfo(methodApiFullSyntax.getReturnType(), false);

            // Find source and destination types
            dstType = findType(processingEnv, this.methodApiFullSyntax.getReturnType());
            srcType = findType(processingEnv, this.methodApiFullSyntax.getRequiredParams().get(0).getVariableType());

            if (srcType == null || dstType == null) {
                return;	// unknown definition !!!
            }


            /* Maybe it will be needed to find out context of this method */
            methodCallApi = findOrCreateOwnMethod(processingEnv, forMethodConfig, null, srcType, dstType);
        }

        // call reference for type ...
        if (methodCallApi != null && methodCallApi.getOutGeneratedMethod() != null) {
            methodCallApi.getOutGeneratedMethod().analyzeAndGenerateDependMethods(processingEnv, forMethodConfig, this);
        }
    }

    private Type findType(ProcessingEnvironment processingEnv, TypeInfo typeInfo) {
        TypeMirror type = typeInfo.getType(processingEnv);
        if (TypeUtils.isArrayType(processingEnv, type)) {
            ArrayType arrayType = (ArrayType) type;
            if (arrayType.getComponentType() instanceof Type){
                return (Type) arrayType.getComponentType();
            }
            return null;
        }
        List<Type> types = TypeUtils.getParametrizedTypes(type);
        if (types == null || types.size() != 1 ) {
            return null;	// unknown definition !!!
        }
        return types.get(0);
    }

    @Override
    public void registerImports(ProcessingEnvironment processingEnv, ImportsTypeDefinitions imports) {
        super.registerImports(processingEnv, imports);
        if (listConstructorType != null) listConstructorType.registerImports(processingEnv, imports);
    }

    @Override
    protected void writeSourceCodeBody(SourceGeneratorContext ctx) {

        // return
        TypeWithVariableInfo varSrc = this.methodApiFullSyntax.getRequiredParams().get(0);
        String srcVarName = varSrc.getVariableName();
        String dstVarName = this.varRet.getVariableName();

        boolean srcIsArray = varSrc.getVariableType().isArray(ctx.processingEnv);
        boolean dstIsArray = this.varRet.getVariableType().isArray(ctx.processingEnv);


        writeSourceInstanceCacheLoad(ctx, varSrc, varRet);

        if (this.methodApiFullSyntax.isGenerateReturnParamRequired() && !dstIsArray) {
            ctx.pw.print("\nif (");
            ctx.pw.print(dstVarName);
            ctx.pw.print(" == null) {");
            ctx.pw.print("\n\t");
            ctx.pw.print(dstVarName);
        } else {
            // Declare variable ...
            ctx.pw.print("\n");
            varRet.writeSourceCode(ctx, true, false);
            ctx.pw.print(" ");
        }
        ctx.pw.print(" = ");
        if (srcIsArray) {
            listConstructorType.writeSourceCodeWithParams(ctx, srcVarName + ".length");
        }
        else {
            listConstructorType.writeSourceCodeWithParams(ctx, srcVarName + ".size()");
        }
        ctx.pw.print(";");

        if (this.methodApiFullSyntax.isGenerateReturnParamRequired() && !dstIsArray) {
            ctx.pw.print("\n}");
            ctx.pw.print("\nelse {\n\t" + dstVarName + ".clear();\n}");
        }

        writeSourceInstanceCacheRegister(ctx, varSrc, varRet);

        ctx.pw.print("\n\n// Copy values");
        String name = NameUtils.findBestName(this.usedNames, "s");
        this.usedNames.add(name);

        if (dstIsArray) {
            ctx.pw.print("\nint iii_"+srcVarName+" = 0;");
        }
        ctx.pw.print("\nfor (");
        new TypeInfo(srcType).writeSourceCode(ctx);
        ctx.pw.print(" ");
        ctx.pw.print(name);
        ctx.pw.print(" : ");
        ctx.pw.print(srcVarName);
        ctx.pw.print(") {");
        ctx.pw.print("\n\t");
        ctx.pw.print(dstVarName);
        if (dstIsArray) {
            ctx.pw.print("[iii_"+srcVarName+"++] = ");
        }
        else {
            ctx.pw.print(".add(");
        }

        if (methodCallApi != null) {
            List<String> params = new ArrayList<>(2);
            params.add(name);
            params.add("null");
            methodCallApi.genSourceForCallWithStringParam(ctx, params, methodApiFullSyntax.getParams(), this);
        } else {
			ctx.pw.print(name);
        }

        if (dstIsArray) {
            ctx.pw.print(";");
        }
        else {
            ctx.pw.print(");");
        }


        ctx.pw.print("\n}");

        ctx.pw.print(";");
    }

}
