package org.wikimedia.search.extra.analysis.esperanto;

import static java.util.Collections.singletonList;

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

public class EsperantoStemmerFilterTest extends BaseTokenStreamTestCase {

    @Test
    public void simpleTest() throws IOException {
        String input = "Bonvenon al Vikipedio";
        try (Analyzer ws = newEsperantoStemmer()) {
            TokenStream ts = ws.tokenStream("", input);
            assertTokenStreamContents(ts,
                    new String[]{"bonven", "al", "vikipedi"},
                    new int[]{0, 9, 12}, // start offsets
                    new int[]{8, 11, 21}, // end offsets
                    null, // types, not supported
                    new int[]{1, 1, 1}, // pos increments
                    null, // pos size (unsupported)
                    21, // last offset
                    null, //keywordAtts, (unsupported)
                    true);
        }
    }

    private Analyzer newEsperantoStemmer() {
        return new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                Tokenizer tok = new WhitespaceTokenizer();
                TokenStream ts = new LowerCaseFilter(tok);
                ts = new EsperantoStemmerTokenFilter(ts);
                return new TokenStreamComponents(tok, ts);
            }
        };
    }

    @Test
    public void simpleTestWithStop() throws IOException {
        // Same test but with a stop filter wrapped
        // testing that if a term is removed our states are still valid
        String input = "Bonvenon al Vikipedio";
        try (Analyzer ws = newEsperantoStemmerWithStop()) {
            TokenStream ts = ws.tokenStream("", input);
            assertTokenStreamContents(ts,
                    new String[]{"bonven", "vikipedi"},
                    new int[]{0, 12}, // start offsets
                    new int[]{8, 21}, // end offsets
                    null, // types, not supported
                    new int[]{1, 2}, // pos increments
                    null, // pos size (unsupported)
                    21, // last offset
                    null, //keywordAtts, (unsupported)
                    true);
        }
    }

    private Analyzer newEsperantoStemmerWithStop() {
        return new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                Tokenizer tok = new WhitespaceTokenizer();
                TokenStream ts = new LowerCaseFilter(tok);
                ts = new EsperantoStemmerTokenFilter(ts);
                ts = new StopFilter(ts, new CharArraySet(new HashSet<>(singletonList("al")), true));
                return new TokenStreamComponents(tok, ts);
            }
        };
    }

    @Test
    public void simpleTestWithFolding() throws IOException {
        // Same test but with folding
        String input = "Bönvenon al Víkìpēdio";
        try (Analyzer ws = newEsperantoStemmerWithFolding()) {
            TokenStream ts = ws.tokenStream("", input);
            assertTokenStreamContents(ts,
                    new String[]{"bonven", "al", "vikipedi"},
                    new int[]{0, 9, 12}, // start offsets
                    new int[]{8, 11, 21}, // end offsets
                    null, // types, not supported
                    new int[]{1, 1, 1}, // pos increments
                    null, // pos size (unsupported)
                    21, // last offset
                    null, //keywordAtts, (unsupported)
                    true);
        }
    }

    private Analyzer newEsperantoStemmerWithFolding() {
        return new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                Tokenizer tok = new WhitespaceTokenizer();
                TokenStream ts = new LowerCaseFilter(tok);
                ts = new EsperantoStemmerTokenFilter(ts);
                ts = new ASCIIFoldingFilter(ts, false);
                return new TokenStreamComponents(tok, ts);
            }
        };
    }

}
