package org.wikimedia.search.extra.analysis.serbian;

import static org.hamcrest.CoreMatchers.equalTo;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.elasticsearch.Version;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.lucene.all.AllTokenStream;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.IndexAnalyzers;
import org.elasticsearch.test.ESTestCase;
import org.elasticsearch.test.IndexSettingsModule;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

public class SerbianStemmerFilterESTest extends ESTestCase {
    private IndexAnalyzers indexAnalyzers;

    @Test
    public void testPrebuilt() throws IOException {
        assertAnalyzerAvailable("serbian_prebuilt", "prebuilt.json");
    }

    @Test
    public void testRedefined() throws IOException {
        assertAnalyzerAvailable("serbian_redefined", "redefined.json");
    }

    private void assertAnalyzerAvailable(String analyzerName, String analysisResource) throws IOException {
        Settings indexSettings = settings(Version.CURRENT)
                .loadFromStream(analysisResource, this.getClass().getResourceAsStream(analysisResource), false)
                .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
                .build();
        IndexSettings indexProps = IndexSettingsModule.newIndexSettings("test", indexSettings);
        Settings settings = Settings.builder()
                .put(Environment.PATH_HOME_SETTING.getKey(), createTempDir())
                .build();
        indexAnalyzers = createTestAnalysis(indexProps, settings, new ExtraAnalysisSerbianPlugin()).indexAnalyzers;
        match(analyzerName, "Добро дошли на Википедију", "Dobr došl na Vikipedij");
    }

    private void match(String analyzerName, String source, String target) throws IOException {
        Analyzer analyzer = indexAnalyzers.get(analyzerName).analyzer();

        TokenStream stream = AllTokenStream.allTokenStream("_all", source, 1.0f, analyzer);
        stream.reset();
        CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);

        StringBuilder sb = new StringBuilder();
        while (stream.incrementToken()) {
            sb.append(termAtt.toString()).append(" ");
        }

        MatcherAssert.assertThat(target, equalTo(sb.toString().trim()));
    }

}
