package sk.annotation.library.jam.processor.utils.commons.translate;


import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
    copy from appache commons:
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-text</artifactId>
        <version>1.9</version>
    </dependency>
*/
public class AggregateTranslator extends CharSequenceTranslator {
    private final List<CharSequenceTranslator> translators = new ArrayList();

    public AggregateTranslator(CharSequenceTranslator... translators) {
        if (translators != null) {
            CharSequenceTranslator[] var2 = translators;
            int var3 = translators.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                CharSequenceTranslator translator = var2[var4];
                if (translator != null) {
                    this.translators.add(translator);
                }
            }
        }

    }

    public int translate(CharSequence input, int index, Writer out) throws IOException {
        Iterator var4 = this.translators.iterator();

        int consumed;
        do {
            if (!var4.hasNext()) {
                return 0;
            }

            CharSequenceTranslator translator = (CharSequenceTranslator)var4.next();
            consumed = translator.translate(input, index, out);
        } while(consumed == 0);

        return consumed;
    }
}
