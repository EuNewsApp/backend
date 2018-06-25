package eu.newsapp.backend.db

import com.jcabi.jdbc.*
import eu.newsapp.backend.IsoAlpha2
import java.sql.ResultSet

data class Source(
		val id : Long,
		val name : String,
		val autoScrape : Boolean,
		val country : IsoAlpha2,
		val language : IsoAlpha2,
		val rss : String
)

private object SourceOutcomeMapping : ListOutcomeMapping<Source>()
{
	const val SELECT = "SELECT source.id, source.name, source.auto_scrape, source.country, source.language, source.rss FROM source"
	
	override fun map(rs : ResultSet, prefix : String) = Source(
			id = rs.getLong("${prefix}id"),
			name = rs.getString("${prefix}name"),
			autoScrape = rs.getBoolean("${prefix}auto_scrape"),
			country = rs.getString("${prefix}country").let { enumValueOf<IsoAlpha2>(it) },
			language = rs.getString("${prefix}language").let { enumValueOf<IsoAlpha2>(it) },
			rss = rs.getString("${prefix}rss")
	)
}

fun loadSources() : List<Source>
{
	val sql = "${SourceOutcomeMapping.SELECT} ORDER BY source.country;"
	return JdbcSession(source).sql(sql).select(ListOutcome<Source>(SourceOutcomeMapping))
}

fun loadSource(id : Long) : Source?
{
	val sql = "${SourceOutcomeMapping.SELECT} WHERE id = ? LIMIT 1;"
	return JdbcSession(source).sql(sql).set(id).select(ListOutcome<Source>(SourceOutcomeMapping)).firstOrNull()
}
