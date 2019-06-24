package sk.annotation.library.mapper.jam.processor.data.confwrappers;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import sk.annotation.library.mapper.jam.annotations.enums.ConfigErrorReporting;
import sk.annotation.library.mapper.jam.processor.data.MethodCallApi;
import sk.annotation.library.mapper.jam.processor.data.TypeWithVariableInfo;
import sk.annotation.library.mapper.jam.processor.data.keys.MethodConfigKey;
import sk.annotation.library.mapper.jam.processor.data.methodgenerator.AbstractMethodSourceInfo;
import sk.annotation.library.mapper.jam.processor.sourcewriter.SourceGeneratorContext;

import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FieldMappingData {
	private FieldValueAccessData src;
	private FieldValueAccessData dst;

	private boolean srcIgnored = false;
	private boolean dstIgnored = false;

	// If missing SOURCE or DESTINATION
	private ConfigErrorReporting srcConfigErrorReportingLevel;
	private ConfigErrorReporting dstConfigErrorReportingLevel;

	private String methodNameRequired = null;
	private MethodCallApi methodCallApi = null;    // ak je NULL, tak sa vola transformacie cez tuto metodu, inak sa pouzivaju priamo SETTRE/GETRE

	@Override
	public String toString() {
		return "FieldMappingData{" +
				"src=" + src +
				", dst=" + dst + '}';
	}

	public String resolveFieldNameForNote(SourceGeneratorContext ctx, String varSrcName, boolean writable) {
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

			if (getMethodCallApi() == null) {
				ctx.pw.print("\n");
				String[] eee = getDst().getSourceForSetter(varDstName);
				ctx.pw.print(eee[0]);
				ctx.pw.print(eee[1]);
				ctx.pw.print(getSrc().getSourceForGetter(varSrcName));
				ctx.pw.print(eee[2]);
				ctx.pw.print(";");
				return;
			}

			writeMethod(ctx, ownerMethod, this, varSrcName, varDstName, ownerMethod.getMethodApiFullSyntax().getParams());

		} catch (Exception ee) {
			ee.printStackTrace();
			ctx.processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, ExceptionUtils.getFullStackTrace(ee));
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
		sb.append(resolveFieldNameForNote(ctx, varDstName, true));
		sb.append(eee[1]);
		sb.append(ObjectUtils.firstNonNull(src, dst).getSourceForGetter(varSrcName));
		sb.append(resolveFieldNameForNote(ctx, varSrcName, false));
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



	protected void writeMethod(SourceGeneratorContext ctx, AbstractMethodSourceInfo ownerMethod, FieldMappingData mappingData, String varSrcName, String varDestName, List<TypeWithVariableInfo> otherVariables) {
		ctx.pw.print("\n");
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

}
