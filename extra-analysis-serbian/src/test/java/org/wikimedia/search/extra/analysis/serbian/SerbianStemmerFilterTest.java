package org.wikimedia.search.extra.analysis.serbian;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.util.HashSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.junit.Test;

public class SerbianStemmerFilterTest extends BaseTokenStreamTestCase {

    @Test
    public void simpleTest() throws IOException {
        String input = "Добро дошли на Википедију";
        try (Analyzer ws = newSerbianStemmer()) {
            TokenStream ts = ws.tokenStream("", input);
            assertTokenStreamContents(ts,
                    new String[]{"dobr", "došl", "na", "vikipedij"},
                    new int[]{0, 6, 12, 15}, // start offsets
                    new int[]{5, 11, 14, 25}, // end offsets
                    null, // types, not supported
                    new int[]{1, 1, 1, 1}, // pos increments
                    null, // pos size (unsupported)
                    25, // last offset
                    null, //keywordAtts, (unsupported)
                    true);
        }
    }

    private Analyzer newSerbianStemmer() {
        return new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                Tokenizer tok = new WhitespaceTokenizer();
                TokenStream ts = new LowerCaseFilter(tok);
                ts = new SerbianStemmerTokenFilter(ts);
                return new TokenStreamComponents(tok, ts);
            }
        };
    }

    @Test
    public void simpleTestWithStop() throws IOException {
        // Same test but with a stop filter wrapped
        // testing that if a term is removed our states are still valid
        String input = "Добро дошли на Википедију";
        try (Analyzer ws = newSerbianStemmerWithStop()) {
            TokenStream ts = ws.tokenStream("", input);
            assertTokenStreamContents(ts,
                    new String[]{"dobr", "došl", "vikipedij"},
                    new int[]{0, 6, 15}, // start offsets
                    new int[]{5, 11, 25}, // end offsets
                    null, // types, not supported
                    new int[]{1, 1, 2}, // pos increments
                    null, // pos size (unsupported)
                    25, // last offset
                    null, //keywordAtts, (unsupported)
                    true);
        }
    }

    private Analyzer newSerbianStemmerWithStop() {
        return new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                Tokenizer tok = new WhitespaceTokenizer();
                TokenStream ts = new LowerCaseFilter(tok);
                ts = new SerbianStemmerTokenFilter(ts);
                ts = new StopFilter(ts, new CharArraySet(new HashSet<>(asList("na")), true));
                return new TokenStreamComponents(tok, ts);
            }
        };
    }

    @Test
    public void simpleTestWithFolding() throws IOException {
        // Same test but with folding
        String input = "Dobro došli na Vikipediju";
        try (Analyzer ws = newSerbianStemmerWithFolding()) {
            TokenStream ts = ws.tokenStream("", input);
            assertTokenStreamContents(ts,
                    new String[]{"dobr", "dosl", "na", "vikipedij"},
                    new int[]{0, 6, 12, 15}, // start offsets
                    new int[]{5, 11, 14, 25}, // end offsets
                    null, // types, not supported
                    new int[]{1, 1, 1, 1}, // pos increments
                    null, // pos size (unsupported)
                    25, // last offset
                    null, //keywordAtts, (unsupported)
                    true);
        }
    }

    private Analyzer newSerbianStemmerWithFolding() {
        return new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                Tokenizer tok = new WhitespaceTokenizer();
                TokenStream ts = new LowerCaseFilter(tok);
                ts = new SerbianStemmerTokenFilter(ts);
                ts = new ASCIIFoldingFilter(ts, false);
                return new TokenStreamComponents(tok, ts);
            }
        };
    }

}
