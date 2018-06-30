package eu.newsapp.backend

import devcsrj.okhttp3.logging.HttpLoggingInterceptor
import okhttp3.*
import okhttp3.CookieJar
import java.util.ArrayList
import java.util.HashMap

val okhttpClient by lazy {
	OkHttpClient.Builder()
			.cookieJar(OkHttpCookieJar)
			.addInterceptor(HttpLoggingInterceptor())
			.build()
}

private object OkHttpCookieJar : CookieJar
{
	private val cookieStore : MutableMap<String, List<Cookie>> = HashMap()
	
	override fun saveFromResponse(url : HttpUrl, cookies : List<Cookie>)
	{
		cookieStore[url.host()] = cookies
	}
	
	override fun loadForRequest(url : HttpUrl) : List<Cookie>
			= cookieStore[url.host()] ?: ArrayList<Cookie>()
}


private val HEX_CHARS = "0123456789ABCDEF".toCharArray()

fun ByteArray.toHex() : String{
	val result = StringBuffer()
	
	forEach {
		val octet = it.toInt()
		val firstIndex = (octet and 0xF0).ushr(4)
		val secondIndex = octet and 0x0F
		result.append(HEX_CHARS[firstIndex])
		result.append(HEX_CHARS[secondIndex])
	}
	
	return result.toString()
}
