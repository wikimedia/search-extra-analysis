package org.wikimedia.search.extra.analysis.esperanto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class EsperantoStemmerTest {

    EsperantoStemmer stemmer = new EsperantoStemmer();

    private void stemCheck(String word, String stem) {
        assertThat(stemmer.stemWord(word)).isEqualTo(stem);
    }

    private void stemUnchangedCheck(String[] wordList) {
        for (String word : wordList) {
            assertThat(stemmer.stemWord(word)).isEqualTo(word);
        }
    }

    private void stemParadigmCheck(String stem, String[] wordList) {
        for (String word : wordList) {
            assertThat(stemmer.stemWord(word)).isEqualTo(stem);
        }
    }

    @Test
    public void testNouns() {
        stemParadigmCheck("tuŝ", new String[]{"tuŝo", "tuŝon", "tuŝoj", "tuŝojn"});
        stemParadigmCheck("drog", new String[]{"drogo", "drogon", "drogoj", "drogojn"});
        stemParadigmCheck("fluaĵ", new String[]{"fluaĵo", "fluaĵon", "fluaĵoj", "fluaĵojn"});
        stemParadigmCheck("armean", new String[]{"armeano", "armeanon", "armeanoj", "armeanojn"});
        stemParadigmCheck("gardist", new String[]{"gardisto", "gardiston", "gardistoj", "gardistojn"});
        stemParadigmCheck("vikipedi", new String[]{"vikipedio", "vikipedion", "vikipedioj", "vikipediojn"});
    }

    @Test
    public void testAdjectives() {
        stemParadigmCheck("puf", new String[]{"pufa", "pufaj", "pufan", "pufajn"});
        stemParadigmCheck("vort", new String[]{"vorta", "vortaj", "vortan", "vortajn"});
        stemParadigmCheck("kastel", new String[]{"kastela", "kastelaj", "kastelan", "kastelajn"});
        stemParadigmCheck("sunflor", new String[]{"sunflora", "sunfloraj", "sunfloran", "sunflorajn"});
    }

    @Test
    public void testAdverbs() {
        stemCheck("improvize", "improviz");
        stemCheck("libervole", "libervol");
        stemCheck("korege", "koreg");
        stemCheck("time", "tim");
        stemCheck("insiste", "insist");
    }

    @Test
    public void testVerbForms() {
        // test all the forms of muziki from Wiktionary
        stemParadigmCheck("muzik", new String[]{"muzikas", "muzikis", "muzikos", "muzikanta",
            "muzikantaj", "muzikinta", "muzikintaj", "muzikonta", "muzikontaj", "muzikantan",
            "muzikantajn", "muzikintan", "muzikintajn", "muzikontan", "muzikontajn", "muzikanto",
            "muzikantoj", "muzikinto", "muzikintoj", "muzikonto", "muzikontoj", "muzikanton",
            "muzikantojn", "muzikinton", "muzikintojn", "muzikonton", "muzikontojn", "muzikante",
            "muzikinte", "muzikonte", "muziki", "muziku", "muzikus"});
    }

    @Test
    public void testBareSuffixes() {
        // bare suffixes
        stemUnchangedCheck(new String[]{"-a", "-e", "-j", "-o", "-oj", "-ojn", "-ajn", "-ej", "-o"});
    }

    @Test
    public void testInflectedSymbols() {
        // inflected numbers and symbols, expanded from examples found in eowiki
        stemParadigmCheck("1", new String[]{"1", "1-oj", "1-ojn", "1-on", "1-j", "1-e"});
        stemParadigmCheck("20", new String[]{"20", "20-oj", "20-ojn", "20-on", "20-j", "20-e"});
        stemParadigmCheck("23", new String[]{"23", "23-oj", "23-ojn", "23-on", "23-j", "23-e"});
        stemParadigmCheck("30", new String[]{"30", "30-oj", "30-ojn", "30-on", "30-j", "30-e"});
        stemParadigmCheck("642", new String[]{"642", "642-oj", "642-ojn", "642-on", "642-j", "642-e"});
        stemParadigmCheck("1930", new String[]{"1930", "1930-oj", "1930-ojn", "1930-on", "1930-j"});
        stemParadigmCheck("12345", new String[]{"12345", "12345-oj", "12345-ojn", "12345-on", "12345-j"});
        stemParadigmCheck("123456", new String[]{"123456", "123456-oj", "123456-ojn", "123456-on", "123456-j"});

        stemParadigmCheck("%", new String[]{"%", "%-oj", "%-ojn", "%-on", "%-j"});
        stemParadigmCheck("2/3", new String[]{"2/3", "2/3-oj", "2/3-ojn", "2/3-on", "2/3-j"});

        stemParadigmCheck("viii", new String[]{"viii", "viii-a", "viii-an", "viii-e"});
        stemParadigmCheck("xviii", new String[]{"xviii", "xviii-a", "xviii-an", "xviii-e"});

        stemParadigmCheck("20-mm", new String[]{"20-mm", "20-mm-a", "20-mm-aj", "20-mm-ajn"});
        stemParadigmCheck("svr4", new String[]{"svr4", "svr4-oj", "svr4-on", "svr4-j", "svr4-ojn"});
    }

    @Test
    public void testDashes() {
        // these are synthetic examples to text the interaction of dashes early in a word,
        // preceded by a vowel, consonant, or non-letter
        stemParadigmCheck("3-later", new String[]{"3-latero", "3-lateron", "3-lateroj",
            "3-laterojn", "3-latera", "3-latere"});

        stemParadigmCheck("a-later", new String[]{"a-latero", "a-lateron", "a-lateroj",
            "a-laterojn", "a-latera", "a-latere"});

        stemParadigmCheck("b-later", new String[]{"b-latero", "b-lateron", "b-lateroj",
            "b-laterojn", "b-latera", "b-latere"});

        stemParadigmCheck("ab-later", new String[]{"ab-latero", "ab-lateron", "ab-lateroj",
            "ab-laterojn", "ab-latera", "ab-latere"});

        stemParadigmCheck("ba-later", new String[]{"ba-latero", "ba-lateron", "ba-lateroj",
            "ba-laterojn", "ba-latera", "ba-latere"});

        stemParadigmCheck("bb-later", new String[]{"bb-latero", "bb-lateron", "bb-lateroj",
            "bb-laterojn", "bb-latera", "bb-latere"});

        stemParadigmCheck("abb-later", new String[]{"abb-latero", "abb-lateron", "abb-lateroj",
            "abb-laterojn", "abb-latera", "abb-latere"});

        stemParadigmCheck("bab-later", new String[]{"bab-latero", "bab-lateron", "bab-lateroj",
            "bab-laterojn", "bab-latera", "bab-latere"});

        stemParadigmCheck("bba-later", new String[]{"bba-latero", "bba-lateron", "bba-lateroj",
            "bba-laterojn", "bba-latera", "bba-latere"});

        stemParadigmCheck("bbb-later", new String[]{"bbb-latero", "bbb-lateron", "bbb-lateroj",
            "bbb-laterojn", "bbb-latera", "bbb-latere"});
    }

    @Test
    public void testNonWords() {
        // empty string and null checks are probably the most important
        stemUnchangedCheck(new String[]{"", null, ".", ",", "123", "@#$%", "—", "_", "*", "!"});
    }

    @Test
    public void testInflectedLetterNames() {
        // inflected letter names
        stemParadigmCheck("a", new String[]{"a", "a-oj", "a-ojn", "a-on", "a-j", "a-"});
        stemParadigmCheck("lo", new String[]{"lo", "lo-oj", "lo-ojn", "lo-on", "lo-j", "lo-"});
        stemParadigmCheck("ĥi", new String[]{"ĥi", "ĥi-oj", "ĥi-ojn", "ĥi-on", "ĥi-j", "ĥi-"});

        // inflected letters, including one non-Esperanto letter
        stemParadigmCheck("m", new String[]{"m", "m-oj", "m-ojn", "m-on", "m-j", "m-"});
        stemParadigmCheck("ŭ", new String[]{"ŭ", "ŭ-oj", "ŭ-ojn", "ŭ-on", "ŭ-j", "ŭ-"});
        stemParadigmCheck("ē", new String[]{"ē", "ē-oj", "ē-ojn", "ē-on", "ē-j", "ē-"});
    }

    @Test
    public void testNumbers() {
        // spelled out numbers

        // inflections of unu, "one"
        stemParadigmCheck("unu", new String[]{"unu", "unuo", "unue", "unua"});

        // inflections of tri, "three"
        stemParadigmCheck("tri", new String[]{"tri", "tria", "trie", "trio", "tri", "tria",
            "trie", "trio", "trioj", "triaj", "trion", "trian", "triajn", "triojn"});

        // inflections of tricent, "three hundred"
        stemParadigmCheck("tricent", new String[]{"tricent", "tricenta", "tricente", "tricento"});

        // spaceless forms from 11 to 20 + common inflections
        // (only 11, 12, 13, and 14 look like they should be stemmed)
        stemParadigmCheck("dekunu", new String[]{"dekunu", "dekunua", "dekunue", "dekunuo"});       // 11
        stemParadigmCheck("dekdu", new String[]{"dekdu", "dekdua", "dekdue", "dekduo"});            // 12
        stemParadigmCheck("dektri", new String[]{"dektri", "dektria", "dektrie", "dektrio"});       // 13
        stemParadigmCheck("dekkvar", new String[]{"dekkvar", "dekkvara", "dekkvare", "dekkvaro"});  // 14
        stemParadigmCheck("dekkvin", new String[]{"dekkvin", "dekkvina", "dekkvine", "dekkvino"});  // 15

        // misc other numbers — 20, 21, 50, 53, 600, 4000
        stemUnchangedCheck(new String[]{"dudek", "dudekunu", "kvindek", "kvindektri", "sescent",
            "kvarmil"});

        // roman numbers up to 20
        stemUnchangedCheck(new String[]{"i", "ii", "iii", "iv", "v", "vi", "vii", "viii", "ix", "x",
            "xi", "xii", "xiii", "xiv", "xv", "xvi", "xvii", "xviii", "xix", "xx"});
    }

    @Test
    public void testOtherNumbers() {
        // these are forms found in eowiki or online, like 1984, plus adapted versions
        // that are likely to stem poorly (because they end in -e, -i, -u), like 1983
        stemUnchangedCheck(new String[]{"milnaŭcentokdekkvar", "milnaŭcentokdektri",  // 1984, 1983
            "sescentsesdekses", "sescentsesdekunu", "centdektri", "centdekdu", // 666, 661, 113, 111
            "centtri", "mildekdu", "milunu"}); // 103, 1012, 1001

        // misc number-related forms, to make sure we didn't break anything
        stemCheck("dudekuma", "dudekum"); // vigesimal
        stemCheck("dekduuma", "dekduum"); // duodecimal
        stemCheck("duondekduo", "duondekdu"); // half-dozen
        stemCheck("jardekduono", "jardekduon"); // 12-year period?
        stemCheck("granddekduo", "granddekdu"); // gross == 144
        stemCheck("kvindekdujara", "kvindekdujar"); // 52-year
        stemCheck("centdudekokona", "centdudekokon"); // 128th
        stemCheck("stelodekdulatero", "stelodekdulater"); // 12-sided polygon
    }

    @Test
    public void testInflectedNames() {
        stemCheck("stephan-a", "stephan");
        stemCheck("wolfgang-on", "wolfgang");
        stemCheck("bismarck-ajn", "bismarck");
        stemCheck("ĝugaŝvili-on", "ĝugaŝvili");
        stemCheck("post-kant-ajn", "post-kant");
        stemCheck("methuselah-aj", "methuselah");

        stemParadigmCheck("uk", new String[]{"uk-a", "uk-an", "uk-j", "uk-jn", "uk-oj", "uk-ojn"});
        stemParadigmCheck("uk-an", new String[]{"uk-ano", "uk-anoj"});
    }

    @Test
    public void testUnstemmed() {
        stemUnchangedCheck(new String[]{
        // uninflectable words that should not be stemmed
            "la",                           // article
            "kaj", "minus", "plus", "se",   // conjunctions
            "oho", "ve", "ha", "ho", "hu",  // interjections
            "iŝi", "kio", "aliu", "nenio",  // pronouns
            "kelka", "nenia", "ĉies",       // determiners
            "pri", "pro", "ĝis", "ĉe",      // prepositions
            "malplej", "malpli", "plej",    // adverbs
            "pli", "plu", "tamen",          // more adverbs
            "ajn", "ĉu", "jen", "ne"        // particles
        });
    }

    @Test
    public void testPronouns() {
        // pronouns have their own rules and limited inflections
        stemParadigmCheck("ci", new String[]{"ci", "cin", "cia", "cian", "ciaj", "ciajn"});
        stemParadigmCheck("ĉio", new String[]{"ĉio", "ĉioj", "ĉion", "ĉiojn"});
        stemParadigmCheck("ĉiu", new String[]{"ĉiu", "ĉiuj", "ĉiun", "ĉiujn"});
    }

    @Test
    public void testNonEsperantoScripts() {
        // words with characters other than Esperanto characters

        // Georgian, Korean, Amharic, Inuktitut, Mandarin, Hindi, Thai, Arabic x2, Burmese,
        // Greek, Russian, Armenian
        stemUnchangedCheck(new String[]{"ვიკიპედია", "위키백과", "ውክፔዲያ", "ᐅᐃᑭᐱᑎᐊ", "維基百科",
            "विकिपीडिया", "วิกิพีเดีย", "وِيكِيبِيدِيَا‎‎", "ويكيبيديا", "ဝီကီပိဒိယ", "βικιπαίδεια",
            "википедия", "վիքիպեդիա"});

        // non-Esperanto Latin strings may have some final letters may be removed
        // Note: vowels with diacritics are not recognized as vowels by the stemmer, so the
        // final "a" in Sango "wïkïpêdïyäa" is the first "vowel" in the word (and so cannot be
        // stemmed away).
        stemCheck("vikipēdija", "vikipēdij");       // Latvian
        stemCheck("wikiibíídiiya", "wikiibíídiiy"); // Navajo
        stemCheck("wikipèdia", "wikipèdi");         // Occitan
        stemCheck("wikipǣdia", "wikipǣdi");         // Old English
        stemCheck("wïkïpêdïyäa", "wïkïpêdïyäa");    // Sango
    }

}
