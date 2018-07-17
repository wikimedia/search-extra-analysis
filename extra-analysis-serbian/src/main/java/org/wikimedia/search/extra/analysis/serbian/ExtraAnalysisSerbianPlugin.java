package org.wikimedia.search.extra.analysis.serbian;

import java.util.Collections;
import java.util.List;

import org.elasticsearch.index.analysis.PreConfiguredTokenFilter;
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

}
