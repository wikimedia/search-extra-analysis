package org.wikimedia.search.extra.analysis.esperanto;

import java.util.Collections;
import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.indices.analysis.AnalysisModule;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;

/**
 * Setup the Elasticsearch plugin.
 */
public class ExtraAnalysisEsperantoPlugin extends Plugin implements AnalysisPlugin {

    /**
     * Register our stemmer.
     */
    @Override
    public Map<String, AnalysisModule.AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
        return Collections.singletonMap("esperanto_stemmer",
            (ie, e, name, settings) -> new AbstractTokenFilterFactory(ie, name, settings) {
                @Override
                public TokenStream create(TokenStream tokenStream) {
                    return new EsperantoStemmerTokenFilter(tokenStream);
                }
            }
        );
    }
}
