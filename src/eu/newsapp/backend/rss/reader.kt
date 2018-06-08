package eu.newsapp.backend.rss

import com.rometools.rome.io.SyndFeedInput
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

val okhttpClient by lazy {
	OkHttpClient()
}

class RssReader(val hints : RssReaderHints = DefaultRssReaderHints)
{
	fun read(url : String)
	{
		val call = okhttpClient.newCall(Request.Builder().url(url).get().build())
		val resp = call.execute()
		if (resp.code() != 200)
			throw IOException("Got Response Code ${resp.code()} != 200")
		
		val input = SyndFeedInput()
		val feed = input.build(resp.body()?.charStream() ?: throw IOException("Failed to get response body"))
		println(feed)
	}
}

interface RssReaderHints
{
	
}

object DefaultRssReaderHints : RssReaderHints
{
	
}
