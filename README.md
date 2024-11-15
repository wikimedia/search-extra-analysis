Extra (GPL) Analysis Modules for OpenSearch
=========================

This is a collection of GNU [General Public
License](https://www.gnu.org/licenses/gpl.html) (GPL) OpenSearch [analysis
plugins](https://opensearch.org/docs/latest/analyzers/language-analyzers/)
(currently at *n* = 2) built around other GPL-licensed open-source morphological
analysis software (e.g., [stemmers](https://en.wikipedia.org/wiki/Stemming) and
such). The primary goal of this collection is to make these language analysis
modules available for use in
[CirrusSearch](https://www.mediawiki.org/wiki/Extension:CirrusSearch) (the
MediaWiki extension that provides search to Wikimedia projects—e.g., Wikipedia
and its sister projects), though of course it would be great if anyone else
found them useful.

Current contents include:

* [esperanto_stemmer](docs/esperanto_stemmer.md) in
`extra-analysis-esperanto`—A filter that provides stemming for the
[Esperanto](https://en.wikipedia.org/wiki/Esperanto) language.

* [serbian_stemmer](docs/serbian_stemmer.md) in
`extra-analysis-serbian`—A filter that provides Cyrillic-to-Latin
transliteration and stemming for the
[Bosnian-Croatian-Montenegrin-Serbian](https://en.wikipedia.org/wiki/
Serbo-Croatian) language.

Installation
------------

| Extra Analysis Plugin |   ElasticSearch   |
|-----------------------|-------------------|
| 1.3.19                | OpenSearch 1.3.19 |
| 6.3.1.1               | Elastic 6.3.1     |
| 5.5.2                 | Elastic 5.5.2     |

Install it like so for Elasticsearch x.y.z:

\>= 5.1.2

```bash
./bin/elasticsearch-plugin install org.wikimedia.search:extra-analysis-serbian:x.y.z
```

Install it like so for OpenSearch x.y.z:
```bash
./bin/opensearch-plugin install org.wikimedia.search:opensearch-extra-analysis-serbian:x.y.z
```

Build
-----
[Spotbugs](https://spotbugs.github.io/) is run during the `verify` phase of the
build to find common issues. The build will break if any issue is found. The
issues will be reported on the console.

To run just the check, use `mvn spotbugs:check` on a project that was already
compiled (`mvn compile`). `mvn spotbugs:gui` will provide a graphical UI that
might be easier to read.

Like all tools, spotbugs is much dumber than you. If you find a false positive,
you can ignore it with the `@SuppressFBWarnings` annotation. You can provide a
justification to make document why this rule should be ignored in this specific
case.
