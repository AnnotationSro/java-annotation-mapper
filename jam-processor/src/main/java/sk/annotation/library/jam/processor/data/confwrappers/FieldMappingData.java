package sk.annotation.library.jam.processor.data.confwrappers;

import sk.annotation.library.jam.annotations.enums.ApplyFieldStrategy;
import sk.annotation.library.jam.annotations.enums.ConfigErrorReporting;
import sk.annotation.library.jam.processor.data.MethodCallApi;
import sk.annotation.library.jam.processor.data.TypeWithVariableInfo;
import sk.annotation.library.jam.processor.data.generator.method.AbstractMethodSourceInfo;
import sk.annotation.library.jam.processor.data.generator.row.AbstractRowValueTransformator;
import sk.annotation.library.jam.processor.data.keys.MethodConfigKey;
import sk.annotation.library.jam.processor.sourcewriter.SourceGeneratorContext;
import sk.annotation.library.jam.processor.utils.commons.ExceptionUtils;
import sk.annotation.library.jam.processor.utils.commons.ObjectUtils;
import sk.annotation.library.jam.processor.utils.commons.StringUtils;

import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.List;

public class FieldMappingData {
	private FieldValueAccessData src;
	private FieldValueAccessData dst;

	private boolean srcIgnored = false;
	private boolean dstIgnored = false;

	private ApplyFieldStrategy fieldStrategy = ApplyFieldStrategy.ALWAYS;

	// If missing SOURCE or DESTINATION
	private ConfigErrorReporting srcConfigErrorReportingLevel;
	private ConfigErrorReporting dstConfigErrorReportingLevel;

	private String methodNameRequired = null;
	private AbstractRowValueTransformator rowValueTransformator = null;
	private MethodCallApi methodCallApi = null;    // If it is null, transformation is called via this method, otherwise there are used SETTERS/GETTERS directly

	@Override
	public String toString() {
		return "FieldMappingData{" +
				"src=" + src +
				", dst=" + dst + '}';
	}

	protected String getCodeNoteWithDetectedProblem(boolean writable) {
		FieldValueAccessData field = writable ? dst : this.src;

		List<String> tags = new ArrayList<>(2);

		if (writable && dstIgnored) tags.add("IGNORED");
		else if (!writable && srcIgnored) tags.add("IGNORED");

		if (tags.isEmpty()) {
			if (field == null) {
				tags.add("UNKNOWN");
			} else {
				if (writable && !field.isWritable()) tags.add("CANNOT WRITE");
				else if (!writable && !field.isReadable()) tags.add("CANNOT READ");
			}
		}


		if (tags.isEmpty()) return "";//tags.add("REQUIRED");

		StringBuilder sb = new StringBuilder();
		// Write tags:
		sb.append(" /*");
		for (int i = 0; i < tags.size(); i++) {
			if (i>0) sb.append(",");
			sb.append(tags.get(i));
		}
		sb.append("*/");

		return sb.toString();
	}
	public void writeSourceCode(SourceGeneratorContext ctx, AbstractMethodSourceInfo ownerMethod, MethodConfigKey methodConfigKey, String varSrcName, String varDstName) {
		try {
			if (writeProblemIfExists(ctx, ownerMethod, methodConfigKey, varSrcName, varDstName)) return;

			// rowValueTransformator
			if (rowValueTransformator != null) {
				ctx.pw.print("\n");
				String[] eee = getDst().getSourceForSetter(varDstName);
				ctx.pw.print(eee[0]);
				ctx.pw.print(eee[1]);

				String varValue = getSrc().getSourceForGetter(varSrcName);
				varValue = rowValueTransformator.generateRowTransform(ctx, src.getTypeOfGetter(), dst.getTypeOfSetter(), varValue);
				ctx.pw.print(varValue);

				ctx.pw.print(eee[2]);
				ctx.pw.print(";");
				return;
			}

			if (getMethodCallApi() != null) {
				writeMethod(ctx, ownerMethod, this, varSrcName, varDstName, ownerMethod.getMethodApiFullSyntax().getParams());
			}

		} catch (Exception ee) {
			ee.printStackTrace();
			ctx.processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, ExceptionUtils.getStackTrace(ee));
		}
	}

	public boolean isWithoutProblemOrNotIgnored() {
		boolean srcProblem = src==null || !src.isReadable();
		boolean dstProblem = dst==null || !dst.isWritable();

		if (!srcProblem && !dstProblem && !srcIgnored && !dstIgnored) return true;
		return false;
	}

	protected boolean writeProblemIfExists(SourceGeneratorContext ctx, AbstractMethodSourceInfo ownerMethod, MethodConfigKey methodConfigKey, String varSrcName, String varDstName) {
/*
 SRC=>DST
 dst.fieldName (IGNORED) = src.???
 dst.fieldName (IGNORED) = src.fieldName (IGNORED)


*/
//		if (rowValueTransformator == null) return false;


		boolean srcProblem = src==null || !src.isReadable();
		boolean dstProblem = dst==null || !dst.isWritable();

		if (isWithoutProblemOrNotIgnored()) return false;

		String rowPrefix;
		String rowPostfix;
		if (srcIgnored && dstIgnored || dstProblem && srcIgnored || srcProblem && dstIgnored) {
			rowPrefix = "//";
			rowPostfix = "\t//INFO:  ignored by configuration";
		}
		else {
			// This is real problem
			rowPrefix = "//";
			rowPostfix = "\t//TODO:  detected problem ";
			// TODO Register problem for
			ConfigErrorReporting bigestLevel = ConfigErrorReporting.NO_REPORT;
			if (srcProblem && bigestLevel.ordinal()< srcConfigErrorReportingLevel.ordinal()) bigestLevel = srcConfigErrorReportingLevel;
			if (dstProblem && bigestLevel.ordinal()< dstConfigErrorReportingLevel.ordinal()) bigestLevel = dstConfigErrorReportingLevel;
			switch (bigestLevel) {
				case COMPILATION_ERROR:
					rowPrefix = "";
					rowPostfix = "\t//FIXME: detected problem ";


				case WARNINGS_ONLY:
					// FIXME: Add information to COMPILATION OUTPUT

					break;
			}
		}


		ctx.pw.printNewLine();


		StringBuilder sb = new StringBuilder();
		sb.append(rowPrefix);
		String[] eee = ObjectUtils.firstNonNull(dst, src).getSourceForSetter(varDstName);
		sb.append(eee[0]);
		sb.append(getCodeNoteWithDetectedProblem(true));
		sb.append(eee[1]);

		String varValue = ObjectUtils.firstNonNull(src, dst).getSourceForGetter(varSrcName);
		if (rowValueTransformator !=null) varValue = rowValueTransformator.generateRowTransform(ctx, src.getTypeOfGetter(), dst.getTypeOfSetter(), varValue);
		sb.append(varValue);


		sb.append(getCodeNoteWithDetectedProblem(false));
		sb.append(eee[2]);
		sb.append(";");

		int len = 60 - sb.length();
		if (len > 0) {
			sb.append(StringUtils.repeat(" ", len));
		}

		ctx.pw.print(sb.toString());
		ctx.pw.print(rowPostfix);

		return true;
	}

	protected boolean resolveWrapFunction(FieldMappingData mappingData, ApplyFieldStrategy testStrategy) {
		if (testStrategy != this.fieldStrategy) return false;

		if (fieldStrategy == ApplyFieldStrategy.NEWVALUE_IS_NOT_NULL) {
			// nemoze nastat, ak zdroj je primitivny typ
			boolean isPrimitiveMapping = mappingData.getMethodCallApi().getSourceType() != null && mappingData.getMethodCallApi().getSourceType().getKind().isPrimitive();
			return !isPrimitiveMapping;
		}
		if (fieldStrategy == ApplyFieldStrategy.OLDVALUE_IS_NULL) {
			// nemoze nastat, ak ciel je primitivny typ
			boolean isPrimitiveMapping = mappingData.getMethodCallApi().getDestinationType() != null && mappingData.getMethodCallApi().getDestinationType().getKind().isPrimitive();
			return !isPrimitiveMapping;
		}
		return false;
	}

	protected void writeMethod(SourceGeneratorContext ctx, AbstractMethodSourceInfo ownerMethod, FieldMappingData mappingData,
		String varSrcName, String varDestName, List<TypeWithVariableInfo> otherVariables) {

		ctx.pw.print("\n");

		if (resolveWrapFunction(mappingData, ApplyFieldStrategy.NEWVALUE_IS_NOT_NULL)) {
			ctx.pw.print("if (");
			ctx.pw.print(mappingData.getSrc().getSourceForGetter(varSrcName));
			ctx.pw.print("!=null) ");
		}
		if (resolveWrapFunction(mappingData, ApplyFieldStrategy.OLDVALUE_IS_NULL)) {
			ctx.pw.print("if (");
			ctx.pw.print(mappingData.getDst().getSourceForGetter(varDestName));
			ctx.pw.print("==null) ");
		}
		String[] eee = mappingData.getDst().getSourceForSetter(varDestName);
		ctx.pw.print(eee[0]);
		ctx.pw.print(eee[1]);
		List<String> params = new ArrayList<>(2);
		params.add(mappingData.getSrc().getSourceForGetter(varSrcName));
		params.add(mappingData.getDst().getSourceForGetter(varDestName));
		mappingData.getMethodCallApi().genSourceForCallWithStringParam(ctx, params, otherVariables, ownerMethod);
		ctx.pw.print(eee[2]);
		ctx.pw.print(";");
	}

	public FieldValueAccessData getSrc() {
		return src;
	}

	public void setSrc(FieldValueAccessData src) {
		this.src = src;
	}

	public FieldValueAccessData getDst() {
		return dst;
	}

	public void setDst(FieldValueAccessData dst) {
		this.dst = dst;
	}

	public boolean isSrcIgnored() {
		return srcIgnored;
	}

	public void setSrcIgnored(boolean srcIgnored) {
		this.srcIgnored = srcIgnored;
	}

	public boolean isDstIgnored() {
		return dstIgnored;
	}

	public void setDstIgnored(boolean dstIgnored) {
		this.dstIgnored = dstIgnored;
	}

	public ApplyFieldStrategy getFieldStrategy() {
		return fieldStrategy;
	}

	public void setFieldStrategy(ApplyFieldStrategy fieldStrategy) {
		this.fieldStrategy = fieldStrategy;
	}

	public ConfigErrorReporting getSrcConfigErrorReportingLevel() {
		return srcConfigErrorReportingLevel;
	}

	public void setSrcConfigErrorReportingLevel(ConfigErrorReporting srcConfigErrorReportingLevel) {
		this.srcConfigErrorReportingLevel = srcConfigErrorReportingLevel;
	}

	public ConfigErrorReporting getDstConfigErrorReportingLevel() {
		return dstConfigErrorReportingLevel;
	}

	public void setDstConfigErrorReportingLevel(ConfigErrorReporting dstConfigErrorReportingLevel) {
		this.dstConfigErrorReportingLevel = dstConfigErrorReportingLevel;
	}

	public String getMethodNameRequired() {
		return methodNameRequired;
	}

	public void setMethodNameRequired(String methodNameRequired) {
		this.methodNameRequired = methodNameRequired;
	}

	public AbstractRowValueTransformator getRowValueTransformator() {
		return rowValueTransformator;
	}

	public void setRowValueTransformator(AbstractRowValueTransformator rowValueTransformator) {
		this.rowValueTransformator = rowValueTransformator;
	}

	public MethodCallApi getMethodCallApi() {
		return methodCallApi;
	}

	public void setMethodCallApi(MethodCallApi methodCallApi) {
		this.methodCallApi = methodCallApi;
	}
}
