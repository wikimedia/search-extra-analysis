package org.wikimedia.search.extra.analysis.esperanto;

import static java.util.Collections.unmodifiableSet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;

/*
 * This file was forked from this repo under a GPLv3 license:
 *    https://github.com/wjdeclan/esperanto_stemmer
 *
 *  Copyright (C) 2018 Declan Whitford Jones
 *
 *  Licensed under GPLv3
 *
 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
 *
 * Modified to pass WMF style checks, convert to WMF best practices, and use unmodifiable sets.
 * Significant refactoring done to reduce complexity of stemWord(), and to front-load hyphen
 * processing to reduce overall complexity.
 *
 * All modifications are also available under a GPLv3 license.
 *
 */

public class EsperantoStemmer {

    private static final Set<String> STEMMER_SUFFIXES = unmodifiableSet(initStemmerSuffixes());
    private static final Set<String> STEMMER_EXCEPTIONS = unmodifiableSet(initStemmerExceptions());
    private static final Set<String> BASIC_NUMERALS = unmodifiableSet(initNumerals());

    // words with limited inflections
    private static final Set<String> PLURAL_DIRECT_CHECKS = unmodifiableSet(initPluralDirectChecks());

    // bare suffixes: assumes that -j and -n (as in -oj, -on, -ojn) are stripped elsewhere
    private static final Set<String> BARE_SUFFIXES = unmodifiableSet(new HashSet<String>(
            Arrays.asList("-o", "-a", "-e", "-")));

    private static final Set<Character> VOWELS = unmodifiableSet(new HashSet<Character>(
            Arrays.asList('a', 'e', 'i', 'o', 'u')));

    private static final String[] BIG_NUMBER_WORDS = {"mil", "cent", "dek"}; // 1000, 100, 10

    private static final String[] PLURAL_DIR_OBJ_SUFFIXES = {"jn", "n", "j"}; // longest first

    private static final int MAX_SUFFIX_LENGTH = initMaxSuffixLength();
    private static final int MIN_STEM_LENGTH = 2;

    private static final Pattern INFLECTED_NUMBER_PAT = Pattern.compile("^(.*[0-9])(a|an|aj|ajn|j|oj|ojn)$");

    // Given a word, return its stemmed form
    public String stemWord(String word) {

        // Check if it is an exception to stemming
        if (isExceptionOrNumber(word)) {
            return word;
        }

        // match strings ending in numbers that are inflected without a hyphen
        // they really should use a hyphen, but we know what they meant
        Matcher inflectedNumberMatcher = INFLECTED_NUMBER_PAT.matcher(word);
        if (inflectedNumberMatcher.matches()) {
            return inflectedNumberMatcher.group(1);
        }

        int localMinStemLength = Math.max(MIN_STEM_LENGTH, firstVowelPos(word) + 1);
        int pluralDirectOffset = calcPluralDirectOffset(word);

        String stem = word; // make a copy to pare down to the stem

        if (pluralDirectOffset > 0) {
            // remove plural (-j) and direct object (-n) suffixes and check for exceptions
            stem = word.substring(0, word.length() - pluralDirectOffset);
            if (PLURAL_DIRECT_CHECKS.contains(stem) || STEMMER_EXCEPTIONS.contains(stem)) {
                return stem;
            }
        }

        if (BARE_SUFFIXES.contains(stem)) {
            // if the token is a bare suffix, like -o, -a, -e, etc., return original word
            // to include -j and -n removed via pluralDirectOffset
            return word;
        }

        // deal with hyphens, which are used to offset inflections for non-standard words
        // like "1-oj" as the plural of "1". Compare to English apostrophe, as in "3's".
        int lastDash = stem.lastIndexOf('-'); // find the last dash, if there is one

        if (lastDash != -1) {
            String ending = stem.substring(lastDash);
            // if everything after the last dash is a bare suffix, strip it and we are done.
            if (BARE_SUFFIXES.contains(ending)) {
                return stem.substring(0, lastDash);
            }
        }

        // find and remove any known suffix, along with any plural/direct object endings
        return word.substring(0, word.length() - pluralDirectOffset -
            findSuffixLength(stem, localMinStemLength));
    }

    // find the position of the first vowel in the word; it must be part of the stem
    private static int firstVowelPos(String word) {
        for (int i = 0; i < word.length(); i++) {
            if (VOWELS.contains(word.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    // check exceptions: null, on explicit list, or ends with what looks like a complex number
    private static boolean isExceptionOrNumber(String word) {
        // check lists of explicit exceptions
        if (word == null || STEMMER_EXCEPTIONS.contains(word) || BASIC_NUMERALS.contains(word)) {
            return true;
        }

        // find the last big number element (ten, hundred, thousand) and check if the rest
        // of the word is a basic number. if so, we're done.
        int numeralIndex = -1;
        for (String bigNumWord : BIG_NUMBER_WORDS) {
            int index = word.lastIndexOf(bigNumWord);
            if (index != -1 && index >= numeralIndex) {
                numeralIndex = index + bigNumWord.length();
            }
        }
        if (numeralIndex != -1) {
            if (BASIC_NUMERALS.contains(word.substring(numeralIndex))) {
                return true;
            }
        }
        return false;
    }

    // calculate the offset of plural marker (-j) and direct object marker (-n) from end of string
    private static int calcPluralDirectOffset(String word) {
        for (String suffix : PLURAL_DIR_OBJ_SUFFIXES) {
            if (word.equals(suffix)) {
                // don't trim the whole string as a suffix
                return 0;
            }
            if (word.endsWith(suffix)) {
                int suffixLength = suffix.length();
                char prevChar = word.charAt(word.length() - suffixLength - 1);
                if (prevChar == '-' || VOWELS.contains(prevChar)) {
                    return suffixLength;
                }
            }
        }
        return 0;
    }

    /*  String findSuffixLength()
     *
     *  find the length of the longest remaining known suffix of input string "stem"
     *
     *  stem: a copy of the word minus any plural -j or direct obj -n that we found, which
     *      we are going to trim down to the best suffix
     *  localMinStemLength: minimum stem length for this word == position of first vowel in
     *      the word or the global MIN_STEM_LENGTH, whichever is greater
     */
    private static int findSuffixLength(String stem, int localMinStemLength) {
        int initialStemLength = stem.length();

        // skip to either the maximum suffix, or after the min stem length,
        // whichever is further along
        int skipOver = Math.max(initialStemLength - MAX_SUFFIX_LENGTH, localMinStemLength);

        if (initialStemLength >= skipOver) {
            // if there's anything we should be skipping, skip it
            stem = stem.substring(skipOver);
        } else {
            // otherwise, there's no plausible suffix
            return 0;
        }

        while ((!STEMMER_SUFFIXES.contains(stem) && !stem.isEmpty())
               || (initialStemLength - stem.length() < localMinStemLength)) {
            // while not a known suffix and not empty, or stem is too short for this suffix,
            // keep trying...
            stem = stem.substring(1);
        }

        return stem.length();
    }

    // Suffixes are sourced from https://en.wikipedia.org/wiki/Esperanto_grammar
    private static Set<String> initStemmerSuffixes() {
        return new HashSet<String>(Arrays.asList(
            // Part of speech suffixes
            "o", "a", "e", "i",
            // Verb conjugations
            // Mood
            "u", "us",
            // Indicative
            "is", "as", "os",
            // Voice
            "inta", "anta", "onta", "ita", "ata", "ota",
            // Compound Tense
            "intas", "antas", "ontas", "itas", "atas", "otas", "intis", "antis", "ontis",
            "itis", "atis", "otis", "intos", "antos", "ontos", "itos", "atos", "otos", "intus",
            "antus", "ontus", "itus", "atus", "otus",
            // Nominal participles
            "inte", "ante", "onte", "ite", "ate", "ote", "into", "anto", "onto", "ito", "ato", "oto"
        ));
    }

    // words that look like they get stemmed, but don't
    private static Set<String> initStemmerExceptions() {
        return new HashSet<String>(Arrays.asList(
            // The article
            "la",
            // Conjunctions
            "kaj", "ke", "kie", "minus", "plus", "se",
            // Interjections
            "aha", "bis", "damne", "dirlididi", "fi", "forfikiĝu", "ha", "ho", "hola", "hu",
            "hura", "muu", "nedankinde", "nu", "oho", "ve",
            // Pronouns
            "aliu", "ĉio", "ĉiu", "ili", "io", "iŝi", "iu", "kio", "kiu", "nenio", "neniu",
            "oni", "tio", "tiu",
            // Determiners
            "ĉies", "ia", "kelka", "kia", "nenia", "tia", "tie",
            // Prepositions
            "cis", "ĉe", "da", "de", "disde", "ekde", "en", "ĝis", "je", "kun", "na", "po",
            "pri", "pro", "sen", "tra",
            // Adverbs
            "malplej", "malpli", "plej", "pli", "plu", "tamen",
            // Particles
            "ajn", "ĉu", "ĉi", "jen", "ju", "ne",
            // Dates
            "a", "an",
            // Roman numerals to 20
            "i", "ii", "iii", "vi", "vii", "viii", "xi", "xii", "xiii", "xvi", "xvii", "xviii",
            // irregular numeral
            "unu"
        ));
    }

    // list of words with limited inflections
    private static Set<String> initPluralDirectChecks() {
        return new HashSet<String>(Arrays.asList(
            // pronouns
            "ci", "ĝi", "gi", "iŝi", "li", "mi", "ni", "ri", "ŝi", "si", "ŝli", "vi",
            // determiners
            "ia", "io", "iu"
        ));
    }

    // basic numerals 1-9
    private static Set<String> initNumerals() {
        return new HashSet<String>(Arrays.asList(
            "unu", "du", "tri", "kvar", "kvin", "ses", "sep", "ok", "naŭ"
        ));
    }

    // find the length of the longest suffix on our list of suffixes
    private static int initMaxSuffixLength() {
        int maxLen = -1;
        for (String suffix : STEMMER_SUFFIXES) {
            if (suffix.length() > maxLen) {
                maxLen = suffix.length();
            }
        }
        return maxLen;
    }

}
