package eu.newsapp.backend.db

import com.jcabi.jdbc.*
import eu.newsapp.backend.IsoAlpha2
import eu.newsapp.backend.db.ArticleOutcomeMapping.SELECT
import eu.newsapp.backend.rss.RssArticle
import java.sql.ResultSet
import java.time.LocalDateTime

data class Article(
		val id : Long,
		val hash : String,
		val headline : String,
		val teaser : String,
		val sourceName : String,
		val sourceId : Long,
		val country : IsoAlpha2,
		val language : IsoAlpha2,
		val link : String,
		val img : String?,
		val pubDate : LocalDateTime,
		val isTranslated : Boolean,
		val isClassified : Boolean,
		val isPublished : Boolean
)

private object ArticleOutcomeMapping : ListOutcomeMapping<Article>()
{
	const val SELECT = "SELECT article.id, article.hash, article.headline, article.teaser, source.name AS s_name, " +
			"source.id AS s_id, source.country AS s_country, source.language AS s_language, article.link, article.img, " +
			"article.pub_date, article.is_translated, article.is_classified, article.is_published " +
			"FROM article INNER JOIN source ON source.id = article.source"
	
	override fun map(rs : ResultSet, prefix : String) = Article(
			id = rs.getLong("${prefix}id"),
			hash = rs.getString("${prefix}hash"),
			headline = rs.getString("${prefix}headline"),
			teaser = rs.getString("${prefix}teaser"),
			sourceName = rs.getString("${prefix}s_name"),
			sourceId = rs.getLong("${prefix}s_id"),
			country = rs.getString("${prefix}s_country").let { enumValueOf<IsoAlpha2>(it) },
			language = rs.getString("${prefix}s_language").let { enumValueOf<IsoAlpha2>(it) },
			link = rs.getString("${prefix}link"),
			img = rs.getString("${prefix}img"),
			pubDate = rs.getTimestamp("${prefix}pub_date").toLocalDateTime(),
			isTranslated = rs.getBoolean("is_translated"),
			isClassified = rs.getBoolean("is_classified"),
			isPublished = rs.getBoolean("is_published")
	)
}

fun List<RssArticle>.store(sourceId : Long)
{
	val sql = "INSERT INTO article (hash, headline, teaser, source, link, img, pub_date) VALUES (?, ?, ?, ?, ?, ?, ?::timestamp);"
	forEach { article ->
		JdbcSession(source).sql(sql)
				.set(article.hash)
				.set(article.headline)
				.set(article.teaser)
				.set(sourceId)
				.set(article.link)
				.set(article.enclosureUrl)
				.set(article.pubDate.let { dtf.format(it) })
				.execute()
	}
}

fun loadArticles(limit : Int = 25) : List<Article>
{
	val sql = "$SELECT ORDER BY pub_date DESC LIMIT ?;"
	return JdbcSession(source).sql(sql).set(limit).select(ListOutcome(ArticleOutcomeMapping))
}

fun loadArticle(hash : String) : Article?
{
	val sql = "$SELECT WHERE hash = ?;"
	return JdbcSession(source).sql(sql).set(hash).select(ListOutcome(ArticleOutcomeMapping)).firstOrNull()
}

fun articleHashExists(hash : String) : Boolean
{
	val sql = "SELECT COUNT(*) FROM article WHERE hash = ?;"
	val count = JdbcSession(source).sql(sql).set(hash).select(SingleOutcome<String>(String::class.java)).toInt()
	return count != 0
}

fun articleExists(sourceId : Long, hash : String) : Boolean
{
	val sql = "SELECT COUNT(*) FROM article WHERE source = ? AND hash = ?;"
	val count = JdbcSession(source).sql(sql).set(sourceId).set(hash).select(SingleOutcome<String>(String::class.java)).toInt()
	return count != 0
}

fun articleLinkExists(link : String) : Boolean
{
	val sql = "SELECT COUNT(*) FROM article WHERE link = ?;"
	val count = JdbcSession(source).sql(sql).set(link).select(SingleOutcome<String>(String::class.java)).toInt()
	return count != 0
}

fun setArticleTranslated(hash : String)
{
	val sql = "UPDATE article SET is_translated = TRUE WHERE hash = ?;"
	JdbcSession(source).sql(sql).set(hash).execute()
}

fun setArticleClassified(id : Long)
{
	val sql = "UPDATE article SET is_classified = TRUE WHERE id = ?;"
	JdbcSession(source).sql(sql).set(id).execute()
}

fun setArticlePublished(id : Long)
{
	val sql = "UPDATE article SET is_published = TRUE WHERE id = ?;"
	JdbcSession(source).sql(sql).set(id).execute()
}
