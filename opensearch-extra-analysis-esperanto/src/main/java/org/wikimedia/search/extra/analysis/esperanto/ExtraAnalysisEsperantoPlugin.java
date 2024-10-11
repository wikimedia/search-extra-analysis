package org.wikimedia.search.extra.analysis.esperanto;

import java.util.Collections;
import java.util.List;

import org.opensearch.index.analysis.PreConfiguredTokenFilter;
import org.opensearch.plugins.AnalysisPlugin;
import org.opensearch.plugins.Plugin;

/**
 * Setup the Elasticsearch plugin.
 */
public class ExtraAnalysisEsperantoPlugin extends Plugin implements AnalysisPlugin {

    /**
     * Register our stemmer.
     */
    @Override
    public List<PreConfiguredTokenFilter> getPreConfiguredTokenFilters() {
        return Collections.singletonList(PreConfiguredTokenFilter.singleton("esperanto_stemmer",
                true, EsperantoStemmerTokenFilter::new));
    }

}
