package org.wikimedia.search.extra.analysis.filters;

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;

public class SerbianStemmerTokenFilterFactory extends AbstractTokenFilterFactory {

	public SerbianStemmerTokenFilterFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
		super(indexSettings, name, settings);
	}

	@Override public TokenStream create(TokenStream tokenStream) {
		return new SerbianStemmerTokenFilter(tokenStream);
	}
}
