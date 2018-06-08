package eu.newsapp.backend

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import eu.newsapp.backend.db.initDB
import eu.newsapp.backend.db.store
import eu.newsapp.backend.rss.RssReader
import spark.Spark.*

fun main(args : Array<String>)
{
	initDB()
	
	port(8706)
	
	get("/api/articles.json") { req, res ->
		val articles = getArticles()
		res.header("Content-Type", "application/json")
		val out = res.raw().outputStream
		jacksonObjectMapper().writeValue(out, articles)
		out.close()
	}
	
	val reader = RssReader()
	val articles = reader.read("https://spiegel.de/index.rss")
	store(articles, 100)
}
