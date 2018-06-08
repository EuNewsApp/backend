package eu.newsapp.backend.db

import com.jcabi.jdbc.*
import eu.newsapp.backend.IsoAlpha2
import eu.newsapp.backend.rss.RssArticle

data class Article(
		val title : String,
		val headline : String,
		val country : IsoAlpha2,
		val source : String,
		val link : String,
		val img : String?,
		val title_en : String? = null,
		val headline_en : String? = null
)

fun store(articles : List<RssArticle>, sourceId : Long)
{
	val sql = "INSERT INTO article (hash, title, headline, source, link, img, pub_date, title_en, headline_en) VALUES (?, ?, ?, ?, ?, ?, ?::timestamp, ?, ?);"
	articles.forEach { article ->
		JdbcSession(source).sql(sql)
				.set(article.hash)
				.set(article.title)
				.set(article.description)
				.set(sourceId)
				.set(article.link)
				.set(article.enclosureUrl)
				.set(article.pubDate.let { dtf.format(it) })
				.set(article.enTitle)
				.set(article.enDescription)
				.execute()
	}
}

fun loadArticles(limit : Int = 25) : List<Article>
{
	val sql = "SELECT article.title, article.headline, source.country, source.name AS source, article.img, article.link, article.title_en, article.headline_en FROM article INNER JOIN source ON source.id = article.source ORDER BY pub_date DESC LIMIT ?;"
	return JdbcSession(source).sql(sql).set(limit).select(ListOutcome<Article>({ rs ->
		Article(
				title = rs.getString("title"),
				headline = rs.getString("headline"),
				country = rs.getString("country").let { enumValueOf<IsoAlpha2>(it) },
				source = rs.getString("source"),
				link = rs.getString("link"),
				img = rs.getString("img"),
				title_en = rs.getString("title_en"),
				headline_en = rs.getString("headline_en")
		)
	}))
}

fun articleHashExists(hash : String) : Boolean
{
	val sql = "SELECT COUNT(*) FROM article WHERE hash = ?;"
	val count = JdbcSession(source).sql(sql).set(hash).select(SingleOutcome<Int>(Integer.TYPE))
	return count == 0
}
