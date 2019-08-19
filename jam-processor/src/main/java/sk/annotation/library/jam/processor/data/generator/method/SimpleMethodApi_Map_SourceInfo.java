package sk.annotation.library.jam.processor.data.generator.method;

import com.sun.tools.javac.code.Type;
import sk.annotation.library.jam.processor.data.MapperClassInfo;
import sk.annotation.library.jam.processor.data.MethodCallApi;
import sk.annotation.library.jam.processor.data.TypeInfo;
import sk.annotation.library.jam.processor.data.TypeWithVariableInfo;
import sk.annotation.library.jam.processor.data.constructors.TypeConstructorInfo;
import sk.annotation.library.jam.processor.data.generator.row.AbstractRowValueTransformator;
import sk.annotation.library.jam.processor.data.keys.MethodConfigKey;
import sk.annotation.library.jam.processor.data.mapi.MethodApiFullSyntax;
import sk.annotation.library.jam.processor.utils.NameUtils;
import sk.annotation.library.jam.processor.sourcewriter.ImportsTypeDefinitions;
import sk.annotation.library.jam.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.jam.processor.utils.TypeUtils;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.ArrayList;
import java.util.List;

public class SimpleMethodApi_Map_SourceInfo extends AbstractMethodSourceInfo {
    public SimpleMethodApi_Map_SourceInfo(MapperClassInfo ownerClassInfo, MethodApiFullSyntax methodApiParams) {
        super(ownerClassInfo, methodApiParams);
    }


    private MethodCallApi methodCallApiKeys = null;
    private MethodCallApi methodCallApiValues = null;

	private AbstractRowValueTransformator rowFieldGeneratorForKeys = null;
	private AbstractRowValueTransformator rowFieldGeneratorForValues = null;

    private TypeConstructorInfo mapConstructorType = null;
    private boolean analyzeRequired = true;
	private List<Type> dstTypeList = null;
    private List<Type> srcTypeList = null;

    @Override
    protected void analyzeAndGenerateDependMethods(ProcessingEnvironment processingEnv, MethodConfigKey forMethodConfig) {
        if (analyzeRequired) {
            analyzeRequired = false;

            mapConstructorType = new TypeConstructorInfo(methodApiFullSyntax.getReturnType(), false);

            // Find source and destination types
            dstTypeList = TypeUtils.getParametrizedTypes(this.methodApiFullSyntax.getReturnType().getType(processingEnv));
            srcTypeList = TypeUtils.getParametrizedTypes(this.methodApiFullSyntax.getRequiredParams().get(0).getVariableType().getType(processingEnv));
            if (dstTypeList != null && srcTypeList != null && dstTypeList.size() == 2 && srcTypeList.size() == 2) {
                /* Maybe it will be needed to find out context of this method */
				rowFieldGeneratorForKeys = AbstractRowValueTransformator.findRowFieldGenerator(processingEnv, ownerClassInfo, srcTypeList.get(0), dstTypeList.get(0));
				if (rowFieldGeneratorForKeys == null) {
					methodCallApiKeys = findOrCreateOwnMethod(processingEnv, null, srcTypeList.get(0), dstTypeList.get(0));
				}

				rowFieldGeneratorForValues = AbstractRowValueTransformator.findRowFieldGenerator(processingEnv, ownerClassInfo, srcTypeList.get(1), dstTypeList.get(1));
				if (rowFieldGeneratorForValues == null) {
					methodCallApiValues = findOrCreateOwnMethod(processingEnv, null, srcTypeList.get(1), dstTypeList.get(1));
				}
            }
        }

        // call reference for type ...
        if (methodCallApiKeys != null && methodCallApiKeys.getOutGeneratedMethod() != null) {
            methodCallApiKeys.getOutGeneratedMethod().analyzeAndGenerateDependMethods(processingEnv, forMethodConfig);
        }
        if (methodCallApiValues != null && methodCallApiValues.getOutGeneratedMethod() != null) {
            methodCallApiValues.getOutGeneratedMethod().analyzeAndGenerateDependMethods(processingEnv, forMethodConfig);
        }
    }

    @Override
    public void registerImports(ProcessingEnvironment processingEnv, ImportsTypeDefinitions imports) {
        super.registerImports(processingEnv, imports);
        if (mapConstructorType != null) mapConstructorType.registerImports(processingEnv, imports);
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
        }
        else {
            // Declare variable ...
            ctx.pw.print("\n");
            varRet.writeSourceCode(ctx, true, false);
            ctx.pw.print(" ");
        }
        ctx.pw.print(" = ");
        mapConstructorType.writeSourceCodeWithParams(ctx);
        ctx.pw.print(";");
        if (this.methodApiFullSyntax.isGenerateReturnParamRequired()) {
            ctx.pw.print("\n}");
            ctx.pw.print("\nelse {\n\t" + dstVarName + ".clear();\n}");
        }

        writeSourceInstanceCacheRegister(ctx, varSrc, varRet);

        ctx.pw.print("\n\n// Copy values");
        String tmpLocalProperty = NameUtils.findBestNameAndUpdateSet(this.usedNames, "entry");

        ctx.pw.print("\nfor (Map.Entry<");
        new TypeInfo(srcTypeList.get(0)).writeSourceCode(ctx);
        ctx.pw.print(",");
        new TypeInfo(srcTypeList.get(1)).writeSourceCode(ctx);
        ctx.pw.print("> ");
        ctx.pw.print(tmpLocalProperty);
        ctx.pw.print(" : ");
        ctx.pw.print(srcVarName);
        ctx.pw.print(".entrySet()) {");
        ctx.pw.print("\n\t");
        ctx.pw.print(dstVarName);
        ctx.pw.print(".put(");

        if (rowFieldGeneratorForKeys!=null) {
			ctx.pw.print(rowFieldGeneratorForKeys.generateRowTransform(ctx, srcTypeList.get(0), dstTypeList.get(0), tmpLocalProperty + ".getKey()"));
		}
        else if (methodCallApiKeys == null) {
            ctx.pw.print(tmpLocalProperty + ".getKey()");
        } else {
            List<String> params = new ArrayList<>(2);
            params.add(tmpLocalProperty + ".getKey()");
            params.add("null");
            methodCallApiKeys.genSourceForCallWithStringParam(ctx, params, methodApiFullSyntax.getParams(), this);
        }

        ctx.pw.print(", ");

		if (rowFieldGeneratorForValues!=null) {
			ctx.pw.print(rowFieldGeneratorForValues.generateRowTransform(ctx, srcTypeList.get(1), dstTypeList.get(1), tmpLocalProperty + ".getValue()"));
		}
		else if (methodCallApiValues == null) {
            ctx.pw.print(tmpLocalProperty + ".getValue()");
        } else {
            List<String> params = new ArrayList<>(2);
            params.add(tmpLocalProperty + ".getValue()");
            params.add("null");
            methodCallApiValues.genSourceForCallWithStringParam(ctx, params, methodApiFullSyntax.getParams(), this);
        }
        ctx.pw.print(");");


        ctx.pw.print("\n}");

        ctx.pw.print(";");
    }

}
