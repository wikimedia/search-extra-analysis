`esperanto_stemmer`
=================

The `esperanto_stemmer` is an Elasticsearch filter that provides stemming for the
	[Esperanto](https://en.wikipedia.org/wiki/Esperanto) language.

Analyzer Notes
--------------

* **Folding:** If you use generic folding (ICU folding conveniently handles both combining
	and precomposed diacrtics), be sure not to fold *Ĉ/ĉ, Ĝ/ĝ, Ĥ/ĥ, Ĵ/ĵ, Ŝ/ŝ, and Ŭ/ŭ* which
	should be kept distinct from *C/c, G/G. H/h, J/j, S/s,* and *U/u.* See more about
	[Esperanto orthogrpahy](https://en.wikipedia.org/wiki/Esperanto_orthography) on Wikipedia.
* **Transliterations:** The stemmer does not support
	[H-system](https://en.wikipedia.org/wiki/Esperanto_orthography#H-system) or
	[X-system](https://en.wikipedia.org/wiki/Esperanto_orthography#X-system) transliterations.
	This affect stemming exceptions and number recognition.


Implementation History
----------------------

* The original implementation in Java is "[Esperanto
	Stemmer](https://github.com/wjdeclan/esperanto_stemmer/)", by Declan Whitford Jones.

* That stemmer was wrapped into this Elasticsearch plugin by Trey Jones to provide the
	`esperanto_stemmer` filter.








