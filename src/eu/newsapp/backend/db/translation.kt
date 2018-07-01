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

package eu.newsapp.backend.db

import com.jcabi.jdbc.*
import eu.newsapp.backend.IsoAlpha2
import eu.newsapp.backend.db.TranslationOutcomeMapping.SELECT
import java.sql.ResultSet
import java.time.LocalDateTime

data class Translation(
		val id : Long,
		val articleId : Long,
		val language : IsoAlpha2,
		val headline : String,
		val teaser : String,
		val translatedAt : LocalDateTime
)

private object TranslationOutcomeMapping : ListOutcomeMapping<Translation>()
{
	const val SELECT = "SELECT id, article, language, headline, teaser, translated_at FROM translation"
	
	override fun map(rs : ResultSet, prefix : String) = Translation(
			id = rs.getLong("id"),
			articleId = rs.getLong("article"),
			language = rs.getString("language").let { enumValueOf<IsoAlpha2>(it) },
			headline = rs.getString("headline"),
			teaser = rs.getString("teaser"),
			translatedAt = rs.getTimestamp("translated_at").toLocalDateTime()
	)
}

fun loadTranslation(articleId : Long) : List<Translation>
{
	val sql = "$SELECT WHERE article = ?;"
	return JdbcSession(source).sql(sql).set(articleId).select(ListOutcome(TranslationOutcomeMapping))
}

fun Translation.store()
{
	val sql = "INSERT INTO translation (article, language, headline, teaser, translated_at) VALUES (?, ?, ?, ?, ?::timestamp);"
	JdbcSession(source).sql(sql)
			.set(articleId)
			.set(language)
			.set(headline)
			.set(teaser)
			.set(translatedAt.let { dtf.format(it) })
			.execute()
}
