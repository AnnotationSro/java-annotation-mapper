package sk.annotation.library.jam.processor.utils.commons;

import sk.annotation.library.jam.processor.utils.commons.translate.CharSequenceTranslator;
import sk.annotation.library.jam.processor.utils.commons.translate.EntityArrays;
import sk.annotation.library.jam.processor.utils.commons.translate.JavaUnicodeEscaper;
import sk.annotation.library.jam.processor.utils.commons.translate.LookupTranslator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/*
    copy from appache commons:
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-text</artifactId>
        <version>1.9</version>
    </dependency>
*/
abstract public class StringEscapeUtils {
    private StringEscapeUtils() {}
    public static final sk.annotation.library.jam.processor.utils.commons.translate.CharSequenceTranslator ESCAPE_JAVA;
    static {
        Map<CharSequence, CharSequence> unescapeJavaMap = new HashMap();
        unescapeJavaMap.put("\"", "\\\"");
        unescapeJavaMap.put("\\", "\\\\");
        ESCAPE_JAVA = new sk.annotation.library.jam.processor.utils.commons.translate.AggregateTranslator(new CharSequenceTranslator[]{new LookupTranslator(Collections.unmodifiableMap(unescapeJavaMap)), new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE), JavaUnicodeEscaper.outsideOf(32, 127)});
    }

    public static final String escapeJava(String input) {
        return ESCAPE_JAVA.translate(input);
    }

}
