package sk.annotation.library.jam.processor.utils.commons.translate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;

/*
    copy from appache commons:
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-text</artifactId>
        <version>1.9</version>
    </dependency>
*/
public abstract class CharSequenceTranslator {
    static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public CharSequenceTranslator() {
    }

    public abstract int translate(CharSequence var1, int var2, Writer var3) throws IOException;

    public final String translate(CharSequence input) {
        if (input == null) {
            return null;
        } else {
            try {
                StringWriter writer = new StringWriter(input.length() * 2);
                this.translate(input, writer);
                return writer.toString();
            } catch (IOException var3) {
                throw new RuntimeException(var3);
            }
        }
    }

    public final void translate(CharSequence input, Writer out) throws IOException {
        if (input != null) {
            int pos = 0;
            int len = input.length();

            while(true) {
                while(pos < len) {
                    int consumed = this.translate(input, pos, out);
                    if (consumed == 0) {
                        char c1 = input.charAt(pos);
                        out.write(c1);
                        ++pos;
                        if (Character.isHighSurrogate(c1) && pos < len) {
                            char c2 = input.charAt(pos);
                            if (Character.isLowSurrogate(c2)) {
                                out.write(c2);
                                ++pos;
                            }
                        }
                    } else {
                        for(int pt = 0; pt < consumed; ++pt) {
                            pos += Character.charCount(Character.codePointAt(input, pos));
                        }
                    }
                }

                return;
            }
        }
    }

    public final CharSequenceTranslator with(CharSequenceTranslator... translators) {
        CharSequenceTranslator[] newArray = new CharSequenceTranslator[translators.length + 1];
        newArray[0] = this;
        System.arraycopy(translators, 0, newArray, 1, translators.length);
        return new AggregateTranslator(newArray);
    }

    public static String hex(int codepoint) {
        return Integer.toHexString(codepoint).toUpperCase(Locale.ENGLISH);
    }
}
