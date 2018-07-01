/*
 * backend
 * Copyright (C) 2018 Dominic Meiser
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl-3.0>.
 */

package eu.newsapp.backend

import com.beust.klaxon.JsonObject
import eu.newsapp.backend.db.ScrapeLog
import okhttp3.*
import org.slf4j.LoggerFactory
import java.time.format.DateTimeFormatter

private val logger = LoggerFactory.getLogger("eu.newsapp.backend.Elasticsearch")

private lateinit var credentials : String

private val dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME

const val ES_PUSH_URL = "http://176.9.25.9:9200"
val JSON by lazy { MediaType.parse("application/json") }

fun setElasticsearchCredentials(username : String, password : String)
{
	credentials = Credentials.basic(username, password)
}

fun ScrapeLog.publishToElasticsearch(id : Long) = try {
	val data = JsonObject(mapOf(
			"totalArticles" to articlesTotal,
			"includedArticles" to articlesIncluded,
			"newArticles" to articlesNew,
			"scrapeTime" to timestamp.toEpochSecond().let { it * 1000 }
	))
	logger.info("publishing to elasticsearch: ${data.toJsonString()}")
	val req = okhttpClient.newCall(Request.Builder()
			.url("$ES_PUSH_URL/scrape/scrape/$id")
			.header("Authorization", credentials)
			.post(RequestBody.create(JSON, data.toJsonString()))
			.build())
	val res = req.execute()
	res.close()
} catch (ex : Exception) {
	logger.error("Error publishing to elasticsearch", ex)
}
