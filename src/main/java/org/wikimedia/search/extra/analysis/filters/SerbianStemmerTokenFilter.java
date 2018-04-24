package org.wikimedia.search.extra.analysis.filters;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "Standard pattern for token filters.")
public class SerbianStemmerTokenFilter extends TokenFilter {

	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	private static final LjubesicPandzicStemmer STEMMER = new LjubesicPandzicStemmer();

	public SerbianStemmerTokenFilter(TokenStream in) {
		super(in);
	}

	/* Marked final because "the TokenStream-API in Lucene is based on the
	 * decorator pattern. Therefore all non-abstract subclasses must be final
	 * or have at least a final implementation of incrementToken()! This is
	 * checked when Java assertions are enabled."
	 * https://lucene.apache.org/core/7_0_0/core/org/apache/lucene/analysis/TokenStream.html
	 */
	@Override
	public final boolean incrementToken() throws IOException {
		if (input.incrementToken()) {
			String converted = STEMMER.stemWord(termAtt.toString());
			if (converted != null) // if we can't stem it, return unchanged
				termAtt.setEmpty().append(converted);
			return true;
		} else {
			return false;
		}
	}

}
