package eu.newsapp.backend.rss

import com.rometools.rome.io.SyndFeedInput
import eu.newsapp.backend.toHex
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.security.MessageDigest
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.text.Charsets.UTF_8

private val logger : Logger = LoggerFactory.getLogger(RssReader::class.java)

val okhttpClient by lazy {
	OkHttpClient()
}

data class RssArticle(
		val title : String,
		val link : String,
		val description : String,
		val pubDate : ZonedDateTime,
		val enclosureUrl : String?
)
{
	var enTitle : String? = null
	var enDescription : String? = null
	
	val hash get() = run {
		val digest = MessageDigest.getInstance("SHA-256")
		digest.digest(title.toByteArray(UTF_8)).toHex()
	}
}

class RssReader(val hints : RssReaderHints = DefaultRssReaderHints)
{
	fun read(url : String) : List<RssArticle>
	{
		logger.info("Reading RSS from $url ...")
		val call = okhttpClient.newCall(Request.Builder().url(url).get().build())
		val resp = call.execute()
		if (resp.code() != 200)
			throw IOException("Got Response Code ${resp.code()} != 200")
		
		val input = SyndFeedInput()
		val feed = input.build(resp.body()?.charStream() ?: throw IOException("Failed to get response body"))
		
		logger.debug("RSS Parsing finished")
		return feed.entries.map {
			RssArticle(
					title = it.title,
					link = it.link,
					description = it.description?.value ?: "",
					pubDate = it.publishedDate.toInstant().atZone(ZoneId.systemDefault()),
					enclosureUrl = it.enclosures.firstOrNull()?.url
			)
		}
	}
}

interface RssReaderHints
{
	
}

object DefaultRssReaderHints : RssReaderHints
{
	
}
