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
import java.util.ArrayList;
import java.util.List;

public class SimpleMethodApi_Collection_SourceInfo extends AbstractMethodSourceInfo {
    public SimpleMethodApi_Collection_SourceInfo(MapperClassInfo ownerClassInfo, MethodApiFullSyntax methodApiParams) {
        super(ownerClassInfo, methodApiParams);
    }


    private MethodCallApi methodCallApi = null;
    private TypeConstructorInfo listConstructorType = null;
    private boolean analyzeRequired = true;

    @Override
    protected void analyzeAndGenerateDependMethods(ProcessingEnvironment processingEnv, MethodConfigKey forMethodConfig) {
        //nothing todo
        if (analyzeRequired) {
            analyzeRequired = false;

            listConstructorType = new TypeConstructorInfo(methodApiFullSyntax.getReturnType(), false);

            // Find source and destination types
            List<Type> dstTypeList = TypeUtils.getParametrizedTypes(this.methodApiFullSyntax.getReturnType().getType(processingEnv));
            List<Type> srcTypeList = TypeUtils.getParametrizedTypes(this.methodApiFullSyntax.getRequiredParams().get(0).getVariableType().getType(processingEnv));
            if (dstTypeList != null && srcTypeList != null && dstTypeList.size() == 1 && srcTypeList.size() == 1) {
                /* Maybe it will be needed to find out context of this method */
                methodCallApi = findOrCreateOwnMethod(processingEnv, null, srcTypeList.get(0), dstTypeList.get(0));
            }
        }

        // call reference for type ...
        if (methodCallApi != null && methodCallApi.getOutGeneratedMethod() != null) {
            methodCallApi.getOutGeneratedMethod().analyzeAndGenerateDependMethods(processingEnv, forMethodConfig);
        }
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


        writeSourceInstanceCacheLoad(ctx, varSrc, varRet);

        if (this.methodApiFullSyntax.isGenerateReturnParamRequired()) {
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
        listConstructorType.writeSourceCodeWithParams(ctx, srcVarName + ".size()");
        ctx.pw.print(";");

        if (this.methodApiFullSyntax.isGenerateReturnParamRequired()) {
            ctx.pw.print("\n}");
            ctx.pw.print("\nelse {\n\t" + dstVarName + ".clear();\n}");
        }

        writeSourceInstanceCacheRegister(ctx, varSrc, varRet);

        ctx.pw.print("\n\n// Copy values");
        String name = NameUtils.findBestName(this.usedNames, "s");
        this.usedNames.add(name);

        ctx.pw.print("\nfor (");
        List<Type> srcTypeList = TypeUtils.getParametrizedTypes(this.methodApiFullSyntax.getRequiredParams().get(0).getVariableType().getType(ctx.processingEnv));
        new TypeInfo(srcTypeList.get(0)).writeSourceCode(ctx);
        ctx.pw.print(" ");
        ctx.pw.print(name);
        ctx.pw.print(" : ");
        ctx.pw.print(srcVarName);
        ctx.pw.print(") {");
        ctx.pw.print("\n\t");
        ctx.pw.print(dstVarName);
        ctx.pw.print(".add(");

        if (methodCallApi != null) {
            List<String> params = new ArrayList<>(2);
            params.add(name);
            params.add("null");
            methodCallApi.genSourceForCallWithStringParam(ctx, params, methodApiFullSyntax.getParams(), this);
        } else {
			ctx.pw.print(name);
        }

        ctx.pw.print(");");


        ctx.pw.print("\n}");

        ctx.pw.print(";");
    }

}
