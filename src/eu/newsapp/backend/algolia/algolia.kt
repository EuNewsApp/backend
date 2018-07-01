/*
 backend
 Copyright (C) 2018 Dominic Meiser
 
 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/gpl-3.0>.
*/

package eu.newsapp.backend.algolia

import com.algolia.search.ApacheAPIClientBuilder
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import eu.newsapp.backend.Configuration
import eu.newsapp.backend.db.*
import java.time.*
import java.util.*

private val algoliaClient by lazy {
	ApacheAPIClientBuilder(Configuration.algolia.application, Configuration.algolia.key)
			.setObjectMapper(jacksonObjectMapper())
			.build()
}

private val algoliaIndex by lazy {
	algoliaClient.initIndex(Configuration.algolia.index, AlgoliaArticle::class.java)
}

data class AlgoliaSource(
		val name : String,
		val country : String,
		val language : String
)

data class AlgoliaTranslation(
		val headline : String,
		val teaser : String
)

data class AlgoliaArticle(
		val headline : String,
		val teaser : String,
		val source : AlgoliaSource,
		val link : String,
		val img : String?,
		val pubDate : Long,
		val translations : Map<String, AlgoliaTranslation>,
		val categories : List<String>
)

fun publishToAlgolia(article : Article, source : Source, translations : List<Translation>)
{
	algoliaIndex.addObject(AlgoliaArticle(
			headline = article.headline,
			teaser = article.teaser,
			source = AlgoliaSource(
					name = source.name,
					country = source.country.name,
					language = source.language.name
			),
			link = article.link,
			img = article.img,
			pubDate = article.pubDate.atZone(ZoneId.systemDefault()).toInstant().let { Date.from(it) }.time,
			translations = translations.map {
				it.language.name.toLowerCase() to AlgoliaTranslation(
						teaser = it.teaser,
						headline = it.headline
				)
			}.toMap(),
			categories = emptyList() // TODO
	))
}
