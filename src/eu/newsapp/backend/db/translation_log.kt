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

package eu.newsapp.backend.db

import com.jcabi.jdbc.JdbcSession
import com.jcabi.jdbc.SingleOutcome
import eu.newsapp.backend.IsoAlpha2
import java.time.*

data class TranslationLog(
		val timestamp : LocalDateTime,
		val languageFrom : IsoAlpha2,
		val languageTo : IsoAlpha2,
		val characterCount : Int
)

fun TranslationLog.insert() : Long
{
	val sql = "INSERT INTO translation_log (timestamp, language_from, language_to, character_count) VALUES (?::timestamp, ?, ?, ?) RETURNING id;"
	return JdbcSession(source).sql(sql)
			.set(timestamp.let { dtf.format(it) })
			.set(languageFrom).set(languageTo).set(characterCount)
			.select(SingleOutcome(String::class.java)).toLong()
}
