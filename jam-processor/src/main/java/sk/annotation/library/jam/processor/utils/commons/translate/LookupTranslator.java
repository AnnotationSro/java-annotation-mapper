package sk.annotation.library.jam.processor.utils.commons.translate;


import java.io.IOException;
import java.io.Writer;
import java.security.InvalidParameterException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/*
    copy from appache commons:
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-text</artifactId>
        <version>1.9</version>
    </dependency>
*/
public class LookupTranslator extends CharSequenceTranslator {
    private final Map<String, String> lookupMap;
    private final BitSet prefixSet;
    private final int shortest;
    private final int longest;

    public LookupTranslator(Map<CharSequence, CharSequence> lookupMap) {
        if (lookupMap == null) {
            throw new InvalidParameterException("lookupMap cannot be null");
        } else {
            this.lookupMap = new HashMap();
            this.prefixSet = new BitSet();
            int currentShortest = 2147483647;
            int currentLongest = 0;
            Iterator var4 = lookupMap.entrySet().iterator();

            while(var4.hasNext()) {
                Entry<CharSequence, CharSequence> pair = (Entry)var4.next();
                this.lookupMap.put(((CharSequence)pair.getKey()).toString(), ((CharSequence)pair.getValue()).toString());
                this.prefixSet.set(((CharSequence)pair.getKey()).charAt(0));
                int sz = ((CharSequence)pair.getKey()).length();
                if (sz < currentShortest) {
                    currentShortest = sz;
                }

                if (sz > currentLongest) {
                    currentLongest = sz;
                }
            }

            this.shortest = currentShortest;
            this.longest = currentLongest;
        }
    }

    public int translate(CharSequence input, int index, Writer out) throws IOException {
        if (this.prefixSet.get(input.charAt(index))) {
            int max = this.longest;
            if (index + this.longest > input.length()) {
                max = input.length() - index;
            }

            for(int i = max; i >= this.shortest; --i) {
                CharSequence subSeq = input.subSequence(index, index + i);
                String result = (String)this.lookupMap.get(subSeq.toString());
                if (result != null) {
                    out.write(result);
                    return i;
                }
            }
        }

        return 0;
    }
}
