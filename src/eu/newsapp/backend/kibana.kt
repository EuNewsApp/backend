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

import eu.newsapp.backend.db.ScrapeLog
import org.apache.http.HttpHost
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.xcontent.XContentFactory
import java.util.Date

private val client by lazy {
	RestHighLevelClient(RestClient.builder(HttpHost("176.9.25.9", 9200, "http")))
}

fun ScrapeLog.publishToKibana(id : Long) : Boolean
{
	val builder = XContentFactory.jsonBuilder()
	builder.startObject()
	builder.timeField("scrapeDate", timestamp.toInstant().let { Date.from(it) })
	builder.field("articleCount", articlesTotal)
	builder.field("includedArticleCount", articlesIncluded)
	builder.field("newArticles", articlesNew)
	builder.endObject()
	val indexRequest = IndexRequest("scrape", "scrape", "$id").source(builder)
	val indexResponse = client.index(indexRequest)
	return indexResponse.result.lowercase == "created"
}
