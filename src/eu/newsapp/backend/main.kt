package eu.newsapp.backend

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import spark.Spark.*

fun main(args : Array<String>)
{
	port(8706)
	
	get("/api/articles.json") { req, res ->
		val articles = getArticles()
		res.header("Content-Type", "application/json")
		val out = res.raw().outputStream
		jacksonObjectMapper().writeValue(out, articles)
		out.close()
	}
}
