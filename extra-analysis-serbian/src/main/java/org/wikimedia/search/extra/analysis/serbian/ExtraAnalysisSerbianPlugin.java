package org.wikimedia.search.extra.analysis.serbian;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.index.analysis.PreConfiguredTokenFilter;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.indices.analysis.AnalysisModule;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;

/**
 * Setup the Elasticsearch plugin.
 */
public class ExtraAnalysisSerbianPlugin extends Plugin implements AnalysisPlugin {

    /**
     * Register our stemmer.
     */
    @Override
    public List<PreConfiguredTokenFilter> getPreConfiguredTokenFilters() {
        return Collections.singletonList(PreConfiguredTokenFilter.singleton("serbian_stemmer",
                true, SerbianStemmerTokenFilter::new));
    }

    @Override
    public Map<String, AnalysisModule.AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
        return Collections.singletonMap("serbian_stemmer", (isettings, env, name, settings) -> new TokenFilterFactory() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public TokenStream create(TokenStream tokenStream) {
                return new SerbianStemmerTokenFilter(tokenStream);
            }
        });
    }
}
