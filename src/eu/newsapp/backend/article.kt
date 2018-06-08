package eu.newsapp.backend

enum class IsoAlpha2
{
	BG,
	DE,
	FR,
	GK,
	IT,
	NL,
	NZ,
	PL,
	RO,
	SP,
	UK,
}

data class Article(
		val title : String,
		val headline : String,
		val country : IsoAlpha2,
		val source : String,
		val link : String,
		val img : String,
		val `title-en` : String? = null,
		val `headline-en` : String? = null
)

// TODO this is just a dummy
fun getArticles() : List<Article> = listOf(
		Article("Hallo Welt", "Dies ist ein Platzhalter", IsoAlpha2.DE, "spiegel.de", "spiegel.de/asdfg", "ichbineinbild.png"),
		Article("Hello World", "This is a dummy", IsoAlpha2.NZ, "stuff.co.nz", "stuff.co.nz/asdfg", "imanimage.png")
)
