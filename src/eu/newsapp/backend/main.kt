package eu.newsapp.backend

import com.bugsnag.Bugsnag
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import de.msrd0.matrix.client.*
import de.msrd0.matrix.client.util.OkHttpTarget.Companion.client
import eu.newsapp.backend.Configuration.elasticsearch
import eu.newsapp.backend.Configuration.matrix
import eu.newsapp.backend.daemon.startDaemon
import eu.newsapp.backend.db.*
import eu.newsapp.backend.rss.RssReader
import org.slf4j.LoggerFactory
import org.slf4j.bridge.SLF4JBridgeHandler
import spark.Spark.*
import java.net.URI
import java.time.LocalDate

private val logger = LoggerFactory.getLogger("eu.newsapp.backend")

fun main(args : Array<String>)
{
	SLF4JBridgeHandler.removeHandlersForRootLogger()
	SLF4JBridgeHandler.install()
	
	Configuration.loadConfig(args.getOrNull(0))
	
	bugsnag = Bugsnag(Configuration.bugsnag.key)
	bugsnag.setAppVersion(LocalDate.now().toString())
	bugsnag.setReleaseStage(Configuration.bugsnag.stage)
	bugsnag.setSendThreads(true)
	
	val matrixClient = object : MatrixClient(matrix.homeserver, matrix.localpart, hsBaseUri = URI(matrix.uri)) {
		override val nextTxnId : Long
			get() = System.nanoTime()
	}
	matrixClient.userData = MatrixUserData(matrix.token, matrix.device)
	matrixRoom = Room(matrixClient, RoomId.fromString(matrix.room))
	
	initDB()
	
	setElasticsearchCredentials(elasticsearch.username, elasticsearch.password)
	
	port(8706)

	get("/api/articles.json") { req, res ->
		val articles = loadArticles(req.queryParams("limit")?.toIntOrNull() ?: 25)
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
		res.header("Content-Type", "application/json")
		val source = loadSource(req.params("source").toLong())
				?: return@get """{"success":false,"error":"Could not find source with the specified id"}"""
		
		val reader = RssReader()
		val articles = reader.read(source.rss).filter {
			!articleHashExists(it.hash) && !articleLinkExists(it.link)
		}
		if (source.language != IsoAlpha2.EN)
		{
			articles.withIndex().groupBy { (i, v) -> i / 5 }.forEach { (i, v) ->
				val translated = v.map { (j, article) -> article.headline to article.teaser }.buildBatch().translate(source.language).parseBatch()
				v.map { it.value }.withIndex().map { (j, article) ->
					try
					{
						val (title, description) = translated[j]
						//article.enTitle = title
						//article.enDescription = description
					}
					catch (ex : Exception)
					{
						logger.error("Unable to find translation", ex)
					}
				}
			}
		}
		articles.store(source.id)
		"""{"success":true}"""
	}
	
	startDaemon()
}
