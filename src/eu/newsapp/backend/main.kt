package eu.newsapp.backend

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import eu.newsapp.backend.db.*
import eu.newsapp.backend.rss.RssReader
import spark.Spark.*

fun main(args : Array<String>)
{
	initDB()
	
	port(8706)

	get("/api/articles.json") { req, res ->
		val articles = loadArticles()
		res.header("Content-Type", "application/json")
		val out = res.raw().outputStream
		jacksonObjectMapper().writeValue(out, articles)
		out.close()
	}
	
	get("/api/scrape") { req, res ->
		val sources = loadSources()
		res.header("Content-Type", "application/json")
		val out = res.raw().outputStream
		jacksonObjectMapper().writeValue(out, sources)
		out.close()
	}
	
	get("/api/scrape/:source") { req, res ->
		val reader = RssReader()
		val articles = reader.read("https://spiegel.de/index.rss")
		articles.withIndex().groupBy { (i, v) -> i / 5 }.forEach { (i, v) ->
			val translated = v.map { (j, article) -> article.title to article.description }.buildBatch().translate("DE").parseBatch()
			v.map { it.value }.withIndex().map { (j, article) ->
				val (title, description) = translated[j]
				article.enTitle = title
				article.enDescription = description
			}
		}
		store(articles, 100)
	}
}
