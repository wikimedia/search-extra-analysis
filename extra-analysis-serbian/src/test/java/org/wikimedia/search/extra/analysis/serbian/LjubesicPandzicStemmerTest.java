package org.wikimedia.search.extra.analysis.serbian;

import static org.junit.Assert.assertEquals;

import junit.framework.TestCase;

public class LjubesicPandzicStemmerTest extends TestCase {

	LjubesicPandzicStemmer stemmer = new LjubesicPandzicStemmer();

	public void testGeneralStemming() throws Exception {
		// General random Latin and Cyrillic words
		assertEquals(stemmer.stemWord("abdominalni"), "abdominaln");
		assertEquals(stemmer.stemWord("абдоминалне"), "abdominaln");
		assertEquals(stemmer.stemWord("zabave"), "zabav");
		assertEquals(stemmer.stemWord("забавама"), "zabav");
		assertEquals(stemmer.stemWord("ћаф"), "ćaf");
		assertEquals(stemmer.stemWord("жућкастосмеђим"), "žućkastosmeđ");
		assertEquals(stemmer.stemWord("јужночешком"), "južnočešk");
		assertEquals(stemmer.stemWord("ђуричковић"), "đuričković");
	}

	public void testSuffixTransformations() throws Exception {
		// words that use transformations
		assertEquals(stemmer.stemWord("vašljivac"), "vašljivc");
		assertEquals(stemmer.stemWord("bezvoljan"), "bezvoljn");
		assertEquals(stemmer.stemWord("нормалан"), "normaln");

		// words that have transformation "suffixes" repeated earlier in the word
		assertEquals(stemmer.stemWord("kovacevac"), "kovacevc"); // -vac
		assertEquals(stemmer.stemWord("tractrac"), "tractrc"); // -rac
		assertEquals(stemmer.stemWord("raveraverave"), "raveraverav"); // -rave
	}

	public void testStopWords() throws Exception {
		// "stop" words, should be unchanged
		assertEquals(stemmer.stemWord("jesam"), "jesam");
		assertEquals(stemmer.stemWord("moramo"), "moramo");
		assertEquals(stemmer.stemWord("bijaše"), "bijaše");
		assertEquals(stemmer.stemWord("želimo"), "želimo");
		assertEquals(stemmer.stemWord("možeš"), "možeš");
	}

	public void testNonSerbianLatinAndCyrillic() throws Exception {
		// Latin and Cyrillic words with non-Serbian characters
		assertEquals(stemmer.stemWord("waxy"), "waxy"); // English
		assertEquals(stemmer.stemWord("əliağa"), "əliağ"); // Azerbaijani
		assertEquals(stemmer.stemWord("año"), "añ"); // Spanish
		assertEquals(stemmer.stemWord("аблютомания"), "ablюtomaniя"); // Russian
		assertEquals(stemmer.stemWord("вищій"), "viщій"); // Ukrainian (that second і is not an i!)
	}

	public void testNonLatinNonCyrillic() throws Exception {
		// words with characters other than Latin or Cyrillic
		assertEquals(stemmer.stemWord("βικιπαίδεια"), "βικιπαίδεια"); // Greek
		assertEquals(stemmer.stemWord("ვიკიპედია"), "ვიკიპედია"); // Georgian
		assertEquals(stemmer.stemWord("위키백과"), "위키백과"); // Korean
		assertEquals(stemmer.stemWord("ውክፔዲያ"), "ውክፔዲያ"); // Amharic
		assertEquals(stemmer.stemWord("ᐅᐃᑭᐱᑎᐊ"), "ᐅᐃᑭᐱᑎᐊ"); // Inuktitut
	}
}
