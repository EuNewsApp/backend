package eu.newsapp.backend.rss

import com.rometools.rome.io.SyndFeedInput
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.time.ZoneId
import java.time.ZonedDateTime

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
	
}

class RssReader(val hints : RssReaderHints = DefaultRssReaderHints)
{
	fun read(url : String) : List<RssArticle>
	{
		val call = okhttpClient.newCall(Request.Builder().url(url).get().build())
		val resp = call.execute()
		if (resp.code() != 200)
			throw IOException("Got Response Code ${resp.code()} != 200")
		
		val input = SyndFeedInput()
		val feed = input.build(resp.body()?.charStream() ?: throw IOException("Failed to get response body"))
		
		return feed.entries.map {
			RssArticle(
					title = it.title,
					link = it.link,
					description = it.description.value,
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
