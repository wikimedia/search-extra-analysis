package org.wikimedia.search.extra.analysis.serbian;

import static java.util.Collections.unmodifiableSet;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;

//import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * This file was forked from this repo under a GPLv3 license:
 *    https://github.com/Trey314159/SCStemmers
 * which was forked from this repo under a GPLv3 license:
 *    https://github.com/vukbatanovic/SCStemmers
 * <p>
 * Ova klasa implementira stemer za hrvatski "Simple stemmer for
 * Croatian v0.1" Nikole Ljubešića i Ivana Pandžića. Originalna
 * implementacija u Python-u je dostupna na adresi:
 * <br>
 * <a href="http://nlp.ffzg.hr/resources/tools/stemmer-for-croatian/">
 * http://nlp.ffzg.hr/resources/tools/stemmer-for-croatian/</a>
 * </p>
 * <p>
 * Stemer predstavlja poboljšanje ranijeg algoritma opisanog u radu:
 * <br>
 * Retrieving Information in Croatian: Building a Simple and Efficient
 * Rule-Based Stemmer, Nikola Ljubešić, Damir Boras, Ozren Kubelka,
 * Digital Information and Heritage, 313–320 (2007).
 * </p>
 * <br>
 * <p><i>
 * This class implements the "Simple stemmer for Croatian v0.1" by
 * Nikola Ljubešić and Ivan Pandžić. The original implementation in
 * Python is available at:
 * <br>
 * <a href="http://nlp.ffzg.hr/resources/tools/stemmer-for-croatian/">
 * http://nlp.ffzg.hr/resources/tools/stemmer-for-croatian/</a></i>
 * </p>
 * <p><i>
 * The stemmer represents an improvement of an earlier algorithm
 * described in the paper:
 * <br>
 * Retrieving Information in Croatian: Building a Simple and Efficient
 * Rule-Based Stemmer, Nikola Ljubešić, Damir Boras, Ozren Kubelka,
 * Digital Information and Heritage, 313–320 (2007).
 * </i></p>
 *
 * @author Vuk Batanović
 * <br>
 * @see <i>Reliable Baselines for Sentiment Analysis in
 * Resource-Limited Languages: The Serbian Movie Review Dataset</i>, Vuk
 * Batanović, Boško Nikolić, Milan Milosavljević, in Proceedings of the
 * 10th International Conference on Language Resources and Evaluation
 * (LREC 2016), pp. 2688-2696, Portorož, Slovenia (2016).
 * <br>
 * https://github.com/vukbatanovic/SCStemmers
 * <br>
 */
public class LjubesicPandzicStemmer {

    static class Transformations {
        private final Map<String, String> map;
        private final int minLen;
        private final int maxLen;

        Transformations(Map<String, String> transformations) {
            this.map = transformations;
            int min = Integer.MAX_VALUE;
            int max = 0;
            for (String key : map.keySet()) {
                int len = key.length();
                if (len < min) min = len;
                if (len > max) max = len;
            }
            minLen = min;
            maxLen = max;
        }
    }

    /**
     * Mapa sufiksnih transformacija.
     *
     * <i>The map of suffix transformations.</i>
     */
    private static final Transformations TRANSFORMATIONS = new Transformations(unmodifiableMap(initTransformations()));

    /** Lista stop-reči. Korišćena je implementacija u vidu hashseta radi brzine.
     * <p>
     * <i>The list of stop-words. A hashset implementation was used for the sake of efficiency.</i>
     */
    private static final Set<String> STOPSET = unmodifiableSet(initStopSet());

    /** Lista morfoloških obrazaca reči.
     * <p>
     * <i>The list of morphological patterns of words.</i>
     */
    private static final List<Pattern> WORD_PATTERNS = unmodifiableList(initWordPatterns());

    /** Skup samoglasnika.
     * <p>
     * <i>The set of vowels.</i>
     */
    private static final Pattern VOWEL_PATTERN = Pattern.compile("[aeiouR]");

    /** Pattern for matching Syllabic R.
     */
    private static final Pattern SYLLABIC_R_PATTERN = Pattern.compile("(^|[^aeiou])r($|[^aeiou])");

    /** String transformations should be localized to Serbian.
     */
    private static final Locale SR_LOCALE = new Locale("sr");

    /** Mapping from Latin to Cyrillic characters.
     */
    private static final Map<Character, String> CYR_2_LAT_MAP = unmodifiableMap(initCyr2LatMap());

    /**
     * Ako se naiđe na neku od stop-reči, ona se preskače. U suprotnom, sufiks reči se najpre transformiše a zatim i uklanja.
     * <p>
     * <i>If a stop-word is encountered, it is skipped. Otherwise, the suffix of the word is first transformed and then removed.</i>
     * @param word Reč koju treba obraditi
     * <br><i>The word that should be processed</i>
     * @return Stemovana reč
     * <br><i> The stemmed word</i>
     */
    public String stemWord(String word) {
        word = convertCyrrilicToLatinString(word);
        if (STOPSET.contains(word.toLowerCase(SR_LOCALE)))
            return word;
        String stemmed = transform(word);
        for (Pattern pattern : WORD_PATTERNS) {
            Matcher matcher = pattern.matcher(stemmed);
            if (matcher.matches()) {
                String wordStem = matcher.group(1);
                if (hasAVowel(wordStem) && wordStem.length() > 1)
                    return wordStem;
            }
        }
        return stemmed;
    }

    /**
     * Zamenjuje sufiks reči transformisanom varijantom tog sufiksa.
     * <p>
     * <i>Replaces the word suffix with a transformed variant of that suffix.</i>
     * @param word Reč koju treba obraditi
     * <br><i>The word that should be processed</i>
     * @return Transformisana reč
     * <br><i> The transformed word</i>
     */
    private String transform(String word) {
        int wordLength = word.length();
        if (wordLength < TRANSFORMATIONS.minLen) {
            // word is too short to have a suffix to transform
            return word;
        }
        // process suffixes longest to shortest to get most relevant match
        for (int i = Math.min(wordLength, TRANSFORMATIONS.maxLen); i >= TRANSFORMATIONS.minLen; i--) {
            String wordEnding = word.substring(wordLength - i);
            String replacement = TRANSFORMATIONS.map.get(wordEnding);
            if (replacement != null) {
                return word.substring(0, wordLength - i) + replacement;
            }
        }
        return word;
    }

    /**
     * Kapitalizuje slogotvorno R u zadatoj reči, ako postoji.
     * <p>
     * <i>Capitalizes the syllabic R in the given word, if it exists.</i>
     *
     * @param word Reč koju treba obraditi
     * <br><i>The word that should be processed</i>
     * @return Reč sa kapitalizovanim slogotvornim R
     * <br><i>The word with the syllabic R capitalized</i>
     */
    private String capitalizeSyllabicR(String word) {
        return SYLLABIC_R_PATTERN.matcher(word).replaceAll("$1R$2");
    }

    /**
     * Proverava da li reč sadrži samoglasnik/slogotvorno R.
     * <p>
     * <i>Checks whether the word contains a vowel/syllabic R.</i>
     * @param word Reč koju treba obraditi
     * <br><i>The word that should be processed</i>
     * @return True ako reč sadrži samoglasnik/slogotvorno R, false u suprotnom
     * <br><i>True if the word contains a vowel/syllabic R, false otherwise</i>
     */
    private boolean hasAVowel(String word) {
        Matcher matcher = VOWEL_PATTERN.matcher(capitalizeSyllabicR(word));
        return matcher.find();
    }

    /* Convert a string from Cyrillic to Latin.
     */
    private String convertCyrrilicToLatinString(String word) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            sb.append(convertCyrillicToLatinCharacter(ch));
        }
        return sb.toString();
    }

    /* Convert a single Cyrillic character to Latin character or digraph.
     */
    private String convertCyrillicToLatinCharacter(char character) {
        String latinChar = CYR_2_LAT_MAP.get(character);
        if (latinChar != null) return latinChar;

        return Character.toString(character);
    }

    /* Initialize the set of "stop" words, which are uninflected, but still returned.
     */
    private static Set<String> initStopSet() {
        // STOPSET
        Set<String> stops = new HashSet<>();
        stops.add("biti");
        stops.add("jesam");
        stops.add("budem");
        stops.add("sam");
        stops.add("jesi");
        stops.add("budeš");
        stops.add("si");
        stops.add("jesmo");
        stops.add("budemo");
        stops.add("smo");
        stops.add("jeste");
        stops.add("budete");
        stops.add("ste");
        stops.add("jesu");
        stops.add("budu");
        stops.add("su");
        stops.add("bih");
        stops.add("bijah");
        stops.add("bjeh");
        stops.add("bijaše");
        stops.add("bi");
        stops.add("bje");
        stops.add("bješe");
        stops.add("bijasmo");
        stops.add("bismo");
        stops.add("bjesmo");
        stops.add("bijaste");
        stops.add("biste");
        stops.add("bjeste");
        stops.add("bijahu");
    //    stops.add("biste");    // Batanović: Ponavljanja
    //    stops.add("bjeste");    //          Repetitions
    //    stops.add("bijahu");
    //    stops.add("bi");
        stops.add("biše");
        stops.add("bjehu");
    //    stops.add("bješe");
        stops.add("bio");
        stops.add("bili");
        stops.add("budimo");
        stops.add("budite");
        stops.add("bila");
        stops.add("bilo");
        stops.add("bile");
        stops.add("ću");
        stops.add("ćeš");
        stops.add("će");
        stops.add("ćemo");
        stops.add("ćete");
        stops.add("želim");
        stops.add("želiš");
        stops.add("želi");
        stops.add("želimo");
        stops.add("želite");
        stops.add("žele");
        stops.add("moram");
        stops.add("moraš");
        stops.add("mora");
        stops.add("moramo");
        stops.add("morate");
        stops.add("moraju");
        stops.add("trebam");
        stops.add("trebaš");
        stops.add("treba");
        stops.add("trebamo");
        stops.add("trebate");
        stops.add("trebaju");
        stops.add("mogu");
        stops.add("možeš");
        stops.add("može");
        stops.add("možemo");
        stops.add("možete");

        return stops;
    }

    /* Initialize the Cyrillic to Latin mapping.
     */
    private static Map<Character, String> initCyr2LatMap() {
        Map<Character, String> c2l = new HashMap<>();
        c2l.put('а', "a");        c2l.put('А', "A");
        c2l.put('б', "b");        c2l.put('Б', "B");
        c2l.put('в', "v");        c2l.put('В', "V");
        c2l.put('г', "g");        c2l.put('Г', "G");
        c2l.put('д', "d");        c2l.put('Д', "D");
        c2l.put('ђ', "đ");        c2l.put('Ђ', "Đ");
        c2l.put('е', "e");        c2l.put('Е', "E");
        c2l.put('ж', "ž");        c2l.put('Ж', "Ž");
        c2l.put('з', "z");        c2l.put('З', "Z");
        c2l.put('и', "i");        c2l.put('И', "I");
        c2l.put('ј', "j");        c2l.put('Ј', "J");
        c2l.put('к', "k");        c2l.put('К', "K");
        c2l.put('л', "l");        c2l.put('Л', "L");
        c2l.put('љ', "lj");        c2l.put('Љ', "Lj");
        c2l.put('м', "m");        c2l.put('М', "M");
        c2l.put('н', "n");        c2l.put('Н', "N");
        c2l.put('њ', "nj");        c2l.put('Њ', "Nj");
        c2l.put('о', "o");        c2l.put('О', "O");
        c2l.put('п', "p");        c2l.put('П', "P");
        c2l.put('р', "r");        c2l.put('Р', "R");
        c2l.put('с', "s");        c2l.put('С', "S");
        c2l.put('т', "t");        c2l.put('Т', "T");
        c2l.put('ћ', "ć");        c2l.put('Ћ', "Ć");
        c2l.put('у', "u");        c2l.put('У', "U");
        c2l.put('ф', "f");        c2l.put('Ф', "F");
        c2l.put('х', "h");        c2l.put('Х', "H");
        c2l.put('ц', "c");        c2l.put('Ц', "C");
        c2l.put('ч', "č");        c2l.put('Ч', "Č");
        c2l.put('џ', "dž");        c2l.put('Џ', "Dž");
        c2l.put('ш', "š");        c2l.put('Ш', "Š");

        return c2l;
    }

    /* Initialize the word ending transformations.
     */
    private static Map<String, String> initTransformations() {
        // TRANSFORMATIONS
        Map<String, String> transforms = new HashMap<>();
        transforms.put("lozi", "loga");
        transforms.put("lozima", "loga");
        transforms.put("pjesi", "pjeh");
        transforms.put("pjesima", "pjeh");
        transforms.put("vojci", "vojka");
        transforms.put("bojci", "bojka");
        transforms.put("jaci", "jak");
        transforms.put("jacima", "jak");
        transforms.put("čajan", "čajni");
        transforms.put("ijeran", "ijerni");
        transforms.put("laran", "larni");
        transforms.put("ijesan", "ijesni");
        transforms.put("ajac", "ajca");
        transforms.put("ajaca", "ajca");
        transforms.put("ljaca", "ljca");
        transforms.put("ljac", "ljca");
        transforms.put("ejac", "ejca");
        transforms.put("ejaca", "ejca");
        transforms.put("ojac", "ojca");
        transforms.put("ojaca", "ojca");
        transforms.put("ajaka", "ajka");
        transforms.put("ojaka", "ojka");
        transforms.put("šaca", "šca");
        transforms.put("šac", "šca");
        transforms.put("inzima", "ing");
        transforms.put("inzi", "ing");
        transforms.put("tvenici", "tvenik");
        transforms.put("tetici", "tetika");
        transforms.put("teticima", "tetika");
        transforms.put("nstava", "nstva");
        transforms.put("nicima", "nik");
        transforms.put("ticima", "tik");
        transforms.put("zicima", "zik");
        transforms.put("snici", "snik");
        transforms.put("kuse", "kusi");
        transforms.put("kusan", "kusni");
        transforms.put("kustava", "kustva");
        transforms.put("dušan", "dušni");
        transforms.put("antan", "antni");
        transforms.put("bilan", "bilni");
        transforms.put("tilan", "tilni");
        transforms.put("avilan", "avilni");
        transforms.put("silan", "silni");
        transforms.put("gilan", "gilni");
        transforms.put("rilan", "rilni");
        transforms.put("nilan", "nilni");
        transforms.put("alan", "alni");
        transforms.put("ozan", "ozni");
        transforms.put("rave", "ravi");
        transforms.put("stavan", "stavni");
        transforms.put("pravan", "pravni");
        transforms.put("tivan", "tivni");
        transforms.put("sivan", "sivni");
        transforms.put("atan", "atni");
        transforms.put("cenata", "centa");
        transforms.put("denata", "denta");
        transforms.put("genata", "genta");
        transforms.put("lenata", "lenta");
        transforms.put("menata", "menta");
        transforms.put("jenata", "jenta");
        transforms.put("venata", "venta");
        transforms.put("tetan", "tetni");
        transforms.put("pletan", "pletni");
        transforms.put("šave", "šavi");
        transforms.put("manata", "manta");
        transforms.put("tanata", "tanta");
        transforms.put("lanata", "lanta");
        transforms.put("sanata", "santa");
        transforms.put("ačak", "ačka");
        transforms.put("ačaka", "ačka");
        transforms.put("ušak", "uška");
        transforms.put("atak", "atka");
        transforms.put("ataka", "atka");
        transforms.put("atci", "atka");
        transforms.put("atcima", "atka");
        transforms.put("etak", "etka");
        transforms.put("etaka", "etka");
        transforms.put("itak", "itka");
        transforms.put("itaka", "itka");
        transforms.put("itci", "itka");
        transforms.put("otak", "otka");
        transforms.put("otaka", "otka");
        transforms.put("utak", "utka");
        transforms.put("utaka", "utka");
        transforms.put("utci", "utka");
        transforms.put("utcima", "utka");
        transforms.put("eskan", "eskna");
        transforms.put("tičan", "tični");
        transforms.put("ojsci", "ojska");
        transforms.put("esama", "esma");
        transforms.put("metara", "metra");
        transforms.put("centar", "centra");
        transforms.put("centara", "centra");
        transforms.put("istara", "istra");
        transforms.put("istar", "istra");
        transforms.put("ošću", "osti");
        transforms.put("daba", "dba");
        transforms.put("čcima", "čka");
        transforms.put("čci", "čka");
        transforms.put("mac", "mca");
        transforms.put("maca", "mca");
        transforms.put("naca", "nca");
        transforms.put("nac", "nca");
        transforms.put("voljan", "voljni");
        transforms.put("anaka", "anki");
        transforms.put("vac", "vca");
        transforms.put("vaca", "vca");
        transforms.put("saca", "sca");
        transforms.put("sac", "sca");
    //    transforms.put("naca", "nca");        // Batanović: Ponavljanja
    //    transforms.put("nac", "nca");        //               Repetitions
        transforms.put("raca", "rca");
        transforms.put("rac", "rca");
        transforms.put("aoca", "alca");
        transforms.put("alaca", "alca");
        transforms.put("alac", "alca");
        transforms.put("elaca", "elca");
        transforms.put("elac", "elca");
        transforms.put("olaca", "olca");
        transforms.put("olac", "olca");
        transforms.put("olce", "olca");
        transforms.put("njac", "njca");
        transforms.put("njaca", "njca");
        transforms.put("ekata", "ekta");
        transforms.put("ekat", "ekta");
        transforms.put("izam", "izma");
        transforms.put("izama", "izma");
        transforms.put("jebe", "jebi");
        transforms.put("ašan", "ašni");

        return transforms;
    }

    /* Initialize the whole-word patterns used to strip suffixes.
     */
    @SuppressWarnings("checkstyle:linelength")
    private static List<Pattern> initWordPatterns() {
        // RULES

        /* Lista početnih delova reči.
         * The list of word beginnings.
         */
        List<String> wordStart = new ArrayList<>();

        /* Lista završetaka reči.
         * The list of word endings.
         */
        List<String> wordEnd = new ArrayList<>();

        wordStart.add(".+(s|š)k"); wordEnd.add("ijima|ijega|ijemu|ijem|ijim|ijih|ijoj|ijeg|iji|ije|ija|oga|ome|omu|ima|og|om|im|ih|oj|i|e|o|a|u");
        wordStart.add(".+(s|š)tv"); wordEnd.add("ima|om|o|a|u");
        wordStart.add(".+(t|m|p|r|g)anij"); wordEnd.add("ama|ima|om|a|u|e|i|");
        wordStart.add(".+an"); wordEnd.add("inom|ina|inu|ine|ima|in|om|u|i|a|e|");
        wordStart.add(".+in"); wordEnd.add("ima|ama|om|a|e|i|u|o|");
        wordStart.add(".+on"); wordEnd.add("ovima|ova|ove|ovi|ima|om|a|e|i|u|");
        wordStart.add(".+n"); wordEnd.add("ijima|ijega|ijemu|ijeg|ijem|ijim|ijih|ijoj|iji|ije|ija|iju|ima|ome|omu|oga|oj|om|ih|im|og|o|e|a|u|i|");
        wordStart.add(".+(a|e|u)ć"); wordEnd.add("oga|ome|omu|ega|emu|ima|oj|ih|om|eg|em|og|uh|im|e|a");
        wordStart.add(".+ugov"); wordEnd.add("ima|i|e|a");
        wordStart.add(".+ug"); wordEnd.add("ama|om|a|e|i|u|o");
        wordStart.add(".+log"); wordEnd.add("ama|om|a|u|e|");
        wordStart.add(".+[^eo]g"); wordEnd.add("ovima|ama|ovi|ove|ova|om|a|e|i|u|o|");
        wordStart.add(".+(rrar|ott|ss|ll)i"); wordEnd.add("jem|ja|ju|o|");
        wordStart.add(".+uj"); wordEnd.add("ući|emo|ete|mo|em|eš|e|u|");
        wordStart.add(".+(c|č|ć|đ|l|r)aj"); wordEnd.add("evima|evi|eva|eve|ama|ima|em|a|e|i|u|");
        wordStart.add(".+(b|c|d|l|n|m|ž|g|f|p|r|s|t|z)ij"); wordEnd.add("ima|ama|om|a|e|i|u|o|");
        wordStart.add(".+[^z]nal"); wordEnd.add("ima|ama|om|a|e|i|u|o|");
        wordStart.add(".+ijal"); wordEnd.add("ima|ama|om|a|e|i|u|o|");
        wordStart.add(".+ozil"); wordEnd.add("ima|om|a|e|u|i|");
        wordStart.add(".+olov"); wordEnd.add("ima|i|a|e");
        wordStart.add(".+ol"); wordEnd.add("ima|om|a|u|e|i|");
        wordStart.add(".+lem"); wordEnd.add("ama|ima|om|a|e|i|u|o|");
        wordStart.add(".+ram"); wordEnd.add("ama|om|a|e|i|u|o");
        wordStart.add(".+(a|d|e|o)r"); wordEnd.add("ama|ima|om|u|a|e|i|");
        wordStart.add(".+(e|i)s"); wordEnd.add("ima|om|e|a|u");
        wordStart.add(".+(t|n|j|k|j|t|b|g|v)aš"); wordEnd.add("ama|ima|om|em|a|u|i|e|");
        wordStart.add(".+(e|i)š"); wordEnd.add("ima|ama|om|em|i|e|a|u|");
        wordStart.add(".+ikat"); wordEnd.add("ima|om|a|e|i|u|o|");
        wordStart.add(".+lat"); wordEnd.add("ima|om|a|e|i|u|o|");
        wordStart.add(".+et"); wordEnd.add("ama|ima|om|a|e|i|u|o|");
        wordStart.add(".+(e|i|k|o)st"); wordEnd.add("ima|ama|om|a|e|i|u|o|");
        wordStart.add(".+išt"); wordEnd.add("ima|em|a|e|u");
        wordStart.add(".+ova"); wordEnd.add("smo|ste|hu|ti|še|li|la|le|lo|t|h|o");
        wordStart.add(".+(a|e|i)v"); wordEnd.add("ijemu|ijima|ijega|ijeg|ijem|ijim|ijih|ijoj|oga|ome|omu|ima|ama|iji|ije|ija|iju|im|ih|oj|om|og|i|a|u|e|o|");
        wordStart.add(".+[^dkml]ov"); wordEnd.add("ijemu|ijima|ijega|ijeg|ijem|ijim|ijih|ijoj|oga|ome|omu|ima|iji|ije|ija|iju|im|ih|oj|om|og|i|a|u|e|o|");
        wordStart.add(".+(m|l)ov"); wordEnd.add("ima|om|a|u|e|i|");
        wordStart.add(".+el"); wordEnd.add("ijemu|ijima|ijega|ijeg|ijem|ijim|ijih|ijoj|oga|ome|omu|ima|iji|ije|ija|iju|im|ih|oj|om|og|i|a|u|e|o|");
        wordStart.add(".+(a|e|š)nj"); wordEnd.add("ijemu|ijima|ijega|ijeg|ijem|ijim|ijih|ijoj|oga|ome|omu|ima|iji|ije|ija|iju|ega|emu|eg|em|im|ih|oj|om|og|a|e|i|o|u");
        wordStart.add(".+čin"); wordEnd.add("ama|ome|omu|oga|ima|og|om|im|ih|oj|a|u|i|o|e|");
        wordStart.add(".+roši"); wordEnd.add("vši|smo|ste|še|mo|te|ti|li|la|lo|le|m|š|t|h|o");
        wordStart.add(".+oš"); wordEnd.add("ijemu|ijima|ijega|ijeg|ijem|ijim|ijih|ijoj|oga|ome|omu|ima|iji|ije|ija|iju|im|ih|oj|om|og|i|a|u|e|");
        wordStart.add(".+(e|o)vit"); wordEnd.add("ijima|ijega|ijemu|ijem|ijim|ijih|ijoj|ijeg|iji|ije|ija|oga|ome|omu|ima|og|om|im|ih|oj|i|e|o|a|u|");
        wordStart.add(".+ast"); wordEnd.add("ijima|ijega|ijemu|ijem|ijim|ijih|ijoj|ijeg|iji|ije|ija|oga|ome|omu|ima|og|om|im|ih|oj|i|e|o|a|u|");
        wordStart.add(".+k"); wordEnd.add("ijemu|ijima|ijega|ijeg|ijem|ijim|ijih|ijoj|oga|ome|omu|ima|iji|ije|ija|iju|im|ih|oj|om|og|i|a|u|e|o|");
        wordStart.add(".+(e|a|i|u)va"); wordEnd.add("jući|smo|ste|jmo|jte|ju|la|le|li|lo|mo|na|ne|ni|no|te|ti|še|hu|h|j|m|n|o|t|v|š|");
        wordStart.add(".+ir"); wordEnd.add("ujemo|ujete|ujući|ajući|ivat|ujem|uješ|ujmo|ujte|avši|asmo|aste|ati|amo|ate|aju|aše|ahu|ala|alo|ali|ale|uje|uju|uj|al|an|am|aš|at|ah|ao");
        wordStart.add(".+ač"); wordEnd.add("ismo|iste|iti|imo|ite|iše|eći|ila|ilo|ili|ile|ena|eno|eni|ene|io|im|iš|it|ih|en|i|e");
        wordStart.add(".+ača"); wordEnd.add("vši|smo|ste|smo|ste|hu|ti|mo|te|še|la|lo|li|le|ju|na|no|ni|ne|o|m|š|t|h|n");
        wordStart.add(".+n"); wordEnd.add("uvši|usmo|uste|ući|imo|ite|emo|ete|ula|ulo|ule|uli|uto|uti|uta|em|eš|uo|ut|e|u|i");
        wordStart.add(".+ni"); wordEnd.add("vši|smo|ste|ti|mo|te|mo|te|la|lo|le|li|m|š|o");
        wordStart.add(".+((a|r|i|p|e|u)st|[^o]g|ik|uc|oj|aj|lj|ak|ck|čk|šk|uk|nj|im|ar|at|et|št|it|ot|ut|zn|zv)a"); wordEnd.add("jući|vši|smo|ste|jmo|jte|jem|mo|te|je|ju|ti|še|hu|la|li|le|lo|na|no|ni|ne|t|h|o|j|n|m|š");
        wordStart.add(".+ur"); wordEnd.add("ajući|asmo|aste|ajmo|ajte|amo|ate|aju|ati|aše|ahu|ala|ali|ale|alo|ana|ano|ani|ane|al|at|ah|ao|aj|an|am|aš");
        wordStart.add(".+(a|i|o)staj"); wordEnd.add("asmo|aste|ahu|ati|emo|ete|aše|ali|ući|ala|alo|ale|mo|ao|em|eš|at|ah|te|e|u|");
        wordStart.add(".+(b|c|č|ć|d|e|f|g|j|k|n|r|t|u|v)a"); wordEnd.add("lama|lima|lom|lu|li|la|le|lo|l");
        wordStart.add(".+(t|č|j|ž|š)aj"); wordEnd.add("evima|evi|eva|eve|ama|ima|em|a|e|i|u|");
        wordStart.add(".+([^o]m|ič|nč|uč|b|c|ć|d|đ|h|j|k|l|n|p|r|s|š|v|z|ž)a"); wordEnd.add("jući|vši|smo|ste|jmo|jte|mo|te|ju|ti|še|hu|la|li|le|lo|na|no|ni|ne|t|h|o|j|n|m|š");
        wordStart.add(".+(a|i|o)sta"); wordEnd.add("dosmo|doste|doše|nemo|demo|nete|dete|nimo|nite|nila|vši|nem|dem|neš|deš|doh|de|ti|ne|nu|du|la|li|lo|le|t|o");
        wordStart.add(".+ta"); wordEnd.add("smo|ste|jmo|jte|vši|ti|mo|te|ju|še|la|lo|le|li|na|no|ni|ne|n|j|o|m|š|t|h");
        wordStart.add(".+inj"); wordEnd.add("asmo|aste|ati|emo|ete|ali|ala|alo|ale|aše|ahu|em|eš|at|ah|ao");
        wordStart.add(".+as"); wordEnd.add("temo|tete|timo|tite|tući|tem|teš|tao|te|li|ti|la|lo|le");
        wordStart.add(".+(elj|ulj|tit|ac|ič|od|oj|et|av|ov)i"); wordEnd.add("vši|eći|smo|ste|še|mo|te|ti|li|la|lo|le|m|š|t|h|o");
        wordStart.add(".+(tit|jeb|ar|ed|uš|ič)i"); wordEnd.add("jemo|jete|jem|ješ|smo|ste|jmo|jte|vši|mo|še|te|ti|ju|je|la|lo|li|le|t|m|š|h|j|o");
        wordStart.add(".+(b|č|d|l|m|p|r|s|š|ž)i"); wordEnd.add("jemo|jete|jem|ješ|smo|ste|jmo|jte|vši|mo|lu|še|te|ti|ju|je|la|lo|li|le|t|m|š|h|j|o");
        wordStart.add(".+luč"); wordEnd.add("ujete|ujući|ujemo|ujem|uješ|ismo|iste|ujmo|ujte|uje|uju|iše|iti|imo|ite|ila|ilo|ili|ile|ena|eno|eni|ene|uj|io|en|im|iš|it|ih|e|i");
        wordStart.add(".+jeti"); wordEnd.add("smo|ste|še|mo|te|ti|li|la|lo|le|m|š|t|h|o");
        wordStart.add(".+e"); wordEnd.add("lama|lima|lom|lu|li|la|le|lo|l");
        wordStart.add(".+i"); wordEnd.add("lama|lima|lom|lu|li|la|le|lo|l");
        wordStart.add(".+at"); wordEnd.add("ijega|ijemu|ijima|ijeg|ijem|ijih|ijim|ima|oga|ome|omu|iji|ije|ija|iju|oj|og|om|im|ih|a|u|i|e|o|");
        wordStart.add(".+et"); wordEnd.add("avši|ući|emo|imo|em|eš|e|u|i");
        wordStart.add(".+"); wordEnd.add("ajući|alima|alom|avši|asmo|aste|ajmo|ajte|ivši|amo|ate|aju|ati|aše|ahu|ali|ala|ale|alo|ana|ano|ani|ane|am|aš|at|ah|ao|aj|an");
        wordStart.add(".+"); wordEnd.add("anje|enje|anja|enja|enom|enoj|enog|enim|enih|anom|anoj|anog|anim|anih|eno|ovi|ova|oga|ima|ove|enu|anu|ena|ama");
        wordStart.add(".+"); wordEnd.add("nijega|nijemu|nijima|nijeg|nijem|nijim|nijih|nima|niji|nije|nija|niju|noj|nom|nog|nim|nih|an|na|nu|ni|ne|no");
        wordStart.add(".+"); wordEnd.add("om|og|im|ih|em|oj|an|u|o|i|e|a");

        assert wordStart.size() == wordEnd.size();

        /* Lista morfoloških obrazaca reči.
         * The list of morphological patterns of words.
         */
        List<Pattern> wordPats = new ArrayList<>(wordStart.size());

        for (int i = 0; i < wordStart.size(); i++) {
            String pattern = "^(" + wordStart.get(i) + ")(" + wordEnd.get(i) + ")$";
            wordPats.add(Pattern.compile(pattern));
        }

        return wordPats;
    }
}
