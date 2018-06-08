package eu.newsapp.backend

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import spark.Spark.*

fun main(args : Array<String>) {
	port(8706)

	get("/api/articles.json") { req, res ->
		val articles = getArticles()
		res.header("Content-Type", "application/json")
		val out = res.raw().outputStream
		jacksonObjectMapper().writeValue(out, articles)
		out.close()
	}

	val text = buildBatch(
			listOf(
					"Google faces record fine" to "The EU wants to break the market power of Google's Android operating system. According to the Financial Times, such antitrust proceedings could end with a record fine in the billions.",
					"Guzzetti on Piersanti Mattarella: \"More respect for the lay martyrs of our society\"." to "The president of Acre recounts his relationship with the brother of the head of state: \"I was president of Lombardy, we tried to increase trade between our two regions\".",
					"Austria takes action against « political Islam » by expelling dozens of imams" to "Following the announcement of these evictions accompanied by the closure of seven mosques financed by Turkey, Ankara denounced an « Islamophobic » and « racist » measure."
			)
	)

	val translated = translate(text)
	println(translated)

	val x = translated.parseBatch()
	println(x)
}