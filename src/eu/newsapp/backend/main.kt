package eu.newsapp.backend

import spark.Spark.*

fun main(args : Array<String>)
{
	port(8706)
	
	get("/api/articles.json") { req, res ->
		"""{"dummy":true}"""
	}
}
