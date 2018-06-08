package eu.newsapp.backend.db

import com.jcabi.jdbc.JdbcSession
import com.jcabi.jdbc.ListOutcome
import eu.newsapp.backend.IsoAlpha2

data class Source(
		val id : Long,
		val name : String,
		val country : IsoAlpha2,
		val rss : String
)

fun loadSources() : List<Source>
{
	val sql = "SELECT id, name, country, rss FROM source ORDER BY source.country;"
	return JdbcSession(source).sql(sql).select(ListOutcome<Source>({ rs ->
		Source(
				id = rs.getLong("id"),
				name = rs.getString("name"),
				country = rs.getString("country").let { enumValueOf<IsoAlpha2>(it) },
				rss = rs.getString("rss")
		)
	}))
}
