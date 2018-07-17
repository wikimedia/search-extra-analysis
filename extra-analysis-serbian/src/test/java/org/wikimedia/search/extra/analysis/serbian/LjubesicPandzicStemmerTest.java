package org.wikimedia.search.extra.analysis.serbian;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class LjubesicPandzicStemmerTest {

	LjubesicPandzicStemmer stemmer = new LjubesicPandzicStemmer();

	@Test
	public void testGeneralStemming() {
		// General random Latin and Cyrillic words
		assertThat(stemmer.stemWord("abdominalni")).isEqualTo("abdominaln");
		assertThat(stemmer.stemWord("абдоминалне")).isEqualTo("abdominaln");
		assertThat(stemmer.stemWord("zabave")).isEqualTo("zabav");
		assertThat(stemmer.stemWord("забавама")).isEqualTo("zabav");
		assertThat(stemmer.stemWord("ћаф")).isEqualTo("ćaf");
		assertThat(stemmer.stemWord("жућкастосмеђим")).isEqualTo("žućkastosmeđ");
		assertThat(stemmer.stemWord("јужночешком")).isEqualTo("južnočešk");
		assertThat(stemmer.stemWord("ђуричковић")).isEqualTo("đuričković");
	}

	@Test
	public void testSuffixTransformations() {
		// words that use transformations
		assertThat(stemmer.stemWord("vašljivac")).isEqualTo("vašljivc");
		assertThat(stemmer.stemWord("bezvoljan")).isEqualTo("bezvoljn");
		assertThat(stemmer.stemWord("нормалан")).isEqualTo("normaln");

		// words that have transformation "suffixes" repeated earlier in the word
		assertThat(stemmer.stemWord("kovacevac")).isEqualTo("kovacevc"); // -vac
		assertThat(stemmer.stemWord("tractrac")).isEqualTo("tractrc"); // -rac
		assertThat(stemmer.stemWord("raveraverave")).isEqualTo("raveraverav"); // -rave
	}

	@Test
	public void testStopWords() {
		// "stop" words, should be unchanged
		assertThat(stemmer.stemWord("jesam")).isEqualTo("jesam");
		assertThat(stemmer.stemWord("moramo")).isEqualTo("moramo");
		assertThat(stemmer.stemWord("bijaše")).isEqualTo("bijaše");
		assertThat(stemmer.stemWord("želimo")).isEqualTo("želimo");
		assertThat(stemmer.stemWord("možeš")).isEqualTo("možeš");
	}

	@Test
	public void testNonSerbianLatinAndCyrillic() {
		// Latin and Cyrillic words with non-Serbian characters
		assertThat(stemmer.stemWord("waxy")).isEqualTo("waxy"); // English
		assertThat(stemmer.stemWord("əliağa")).isEqualTo("əliağ"); // Azerbaijani
		assertThat(stemmer.stemWord("año")).isEqualTo("añ"); // Spanish
		assertThat(stemmer.stemWord("аблютомания")).isEqualTo("ablюtomaniя"); // Russian
		assertThat(stemmer.stemWord("вищій")).isEqualTo("viщій"); // Ukrainian (that second і is not an i!)
	}

	@Test
	public void testNonLatinNonCyrillic() {
		// words with characters other than Latin or Cyrillic
		assertThat(stemmer.stemWord("βικιπαίδεια")).isEqualTo("βικιπαίδεια"); // Greek
		assertThat(stemmer.stemWord("ვიკიპედია")).isEqualTo("ვიკიპედია"); // Georgian
		assertThat(stemmer.stemWord("위키백과")).isEqualTo("위키백과"); // Korean
		assertThat(stemmer.stemWord("ውክፔዲያ")).isEqualTo("ውክፔዲያ"); // Amharic
		assertThat(stemmer.stemWord("ᐅᐃᑭᐱᑎᐊ")).isEqualTo("ᐅᐃᑭᐱᑎᐊ"); // Inuktitut
	}
}
