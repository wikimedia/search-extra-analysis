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

	@Override
	public boolean incrementToken() throws IOException {
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
