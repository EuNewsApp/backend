package eu.newsapp.backend.db

import com.jcabi.jdbc.*
import eu.newsapp.backend.IsoAlpha2
import java.sql.ResultSet

data class Source(
		val id : Long,
		val name : String,
		val country : IsoAlpha2,
		val language : IsoAlpha2,
		val rss : String
)

private object SourceOutcomeMapping : ListOutcome.Mapping<Source>
{
	override fun map(rs : ResultSet) = Source(
			id = rs.getLong("id"),
			name = rs.getString("name"),
			country = rs.getString("country").let { enumValueOf<IsoAlpha2>(it) },
			language = rs.getString("language").let { enumValueOf<IsoAlpha2>(it) },
			rss = rs.getString("rss")
	)
}

fun loadSources() : List<Source>
{
	val sql = "SELECT id, name, country, language, rss FROM source ORDER BY source.country;"
	return JdbcSession(source).sql(sql).select(ListOutcome<Source>(SourceOutcomeMapping))
}

fun loadSource(id : Long) : Source?
{
	val sql = "SELECT id, name, country, language, rss FROM source WHERE id = ? LIMIT 1;"
	return JdbcSession(source).sql(sql).set(id).select(ListOutcome<Source>(SourceOutcomeMapping)).firstOrNull()
}
