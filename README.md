Extra (GPL) Analysis Modules for Elasticsearch
=========================

This is a collection of GNU [General Public License](https://www.gnu.org/licenses/gpl.html) (GPL) Elasticsearch [analysis modules](https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-lang-analyzer.html) (currently at *n* = 1) built around other GPL-licensed open-source morphological analysis software (e.g., [stemmers](https://en.wikipedia.org/wiki/Stemming) and such). The primary goal of this collection is to make these language analysis modules available for use in [CirrusSearch](https://www.mediawiki.org/wiki/Extension:CirrusSearch) (the MediaWiki extension that provides search to Wikimedia projects—e.g., Wikipedia and its sister projects), though of course it would be great if anyone else found them useful.

Current contents include:

* [serbian_stemmer](docs/serbian_stemmer.md)—A filter that provides Cyrillic-to-Latin transliteration and stemming for the [Bosnian-Croatian-Montenegrin-Serbian](https://en.wikipedia.org/wiki/Serbo-Croatian) language.

Version Information
------------

| Extra Analysis Plugin |  ElasticSearch  |
|-----------------------|-----------------|
| 5.5.2                 | 5.5.2           |
