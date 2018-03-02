`serbian_stemmer`
=================

The `serbian_stemmer` is an Elasticsearch filter that provides stemming for the [Bosnian-Croatian-Montenegrin-Serbian](https://en.wikipedia.org/wiki/Serbo-Croatian) language (BCMS).

The stemmer converts tokens from Cyrillic script to Latin script, based on the [Serbian alphabet mapping](https://en.wikipedia.org/wiki/Serbian_Cyrillic_alphabet#Modern_alphabet). The stemmer only returns tokens for BCMS words in the Latin script, which allows for cross-script indexing and searching.

Analyzer Notes
--------------

* **Mixed script tokens:** Note that Cyrillic characters that are not part of the Serbian alphabet are not converted to Latin, so that, for example, some characters from the [Russian](https://en.wikipedia.org/wiki/Russian_alphabet) and [Ukrainian](https://en.wikipedia.org/wiki/Ukrainian_alphabet) alphabets, like *ґ, ё, і, ї, й, щ, ъ, ь, ю,* and *я* will not be converted. This means that some Russian or Ukrainian words sent to the `serbian_stemmer` can generate mixed-script tokens.
* **Diacritics:** Serbian dictionaries and encyclopedias often use diacritics (ácute, gràve, double grȁve, mācron, and inverted brȇve) as a pronunciation guide for the [pitch accent](https://en.wikipedia.org/wiki/Serbo-Croatian_phonology#Pitch_accent) of the word. The `serbian_stemmer` doesn't currently handle those accents, and they can lead to poor stemming. They should be removed before stemming.
* **Folding:** If you use generic folding (ICU folding conveniently handles both combining and precomposed diacrtics), be sure not to fold *Ć/ć, Č/č, Đ/đ, Š/š,* or *Ž/ž,* which should be kept distinct from *C/c, D/d, S/s,* and *Z/z.*
 * Note that some non-Serbian Cyrillic characters can be folded to Serbian Cyrillic characters (*ґ* to *г, ё* to *е, й* to *и*) and then they would get converted to the corresponding Serbian Latin characters.



Implementation History
----------------------

* The original python implementation is "[Simple stemmer for Croatian v0.1](http://nlp.ffzg.hr/resources/tools/stemmer-for-croatian/)", by Nikola Ljubešić and Ivan Pandžić, based on a paper by Ljubešić, et al.

* The python version was [ported to Java](https://github.com/vukbatanovic/SCStemmers) (along with several other Serbian and Croatian stemmers in the collection "SCStemmers") by Vuk Batanović.
 * SCStemmers includes [WEKA](https://en.wikipedia.org/wiki/Weka_%28machine_learning%29) integration, and has a dependency on WEKA.
 * SCStemmers adds support for Cyrillic-to-Latin mapping for this originally Croatian stemmer, and better handling of non-BCMS characters. (Serbian is [digraphic](https://en.wikipedia.org/wiki/Digraphia) and uses both Cyrillic and Latin script. Croatian is written only in [Latin script](https://en.wikipedia.org/wiki/Gaj%27s_Latin_alphabet). Stemming algorithms can work across BCMS varieties as long as they are transliterated into the right character set for the stemmer.)

* A [WEKA-free version](https://github.com/Trey314159/SCStemmers) of the SCStemmers collection was forked by Trey Jones.

* The WEKA-free version of just the Ljubešić-Pandžić stemmer was wrapped into this Elasticsearch plugin by Trey Jones to provide the `serbian_stemmer` filter.
 * Only the Ljubešić-Pandžić stemmer was ported because it [performed the best](https://www.mediawiki.org/wiki/User:TJones_%28WMF%29/Notes/Serbian_Stemmer_Analysis/Stemmer_No._4) on stemming corpora from the Serbian-language Wikipedia and Wiktionary projects.








