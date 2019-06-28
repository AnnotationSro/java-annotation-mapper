package sk.annotation.library.jam.processor.utils;

abstract public class MsgConstants {
	static final public String errorMethodParamWithReturnIsNotLast =  "Annotation @Return is acceptable on last param only";
	static final public String errorMethodParamBadType =  "Param with annotation @Return must has the same type as returnType";
	static final public String errorMethodParamReturnAndContext =  "Param with annotation @Return and @Context is not acceptable";
}
