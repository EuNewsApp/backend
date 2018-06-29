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
import java.time.ZonedDateTime

data class ScrapeLog(
		val timestamp : ZonedDateTime,
		val articlesTotal : Int,
		val articlesIncluded : Int,
		val articlesNew : Int
)

fun insertScrapeLog(log : ScrapeLog) : Long
{
	val sql = "INSERT INTO scrape_log (timestamp, articles_total, articles_included, articles_new) VALUES (?::timestamp, ?, ?, ?) RETURNING id;"
	return JdbcSession(source).sql(sql)
			.set(log.timestamp.let { dtf.format(it) })
			.set(log.articlesTotal).set(log.articlesIncluded).set(log.articlesNew)
			.select(SingleOutcome(String::class.java)).toLong()
}
