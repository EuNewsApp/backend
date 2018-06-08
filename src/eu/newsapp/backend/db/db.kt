package eu.newsapp.backend.db

import com.jcabi.jdbc.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import eu.newsapp.backend.Article
import eu.newsapp.backend.IsoAlpha2
import eu.newsapp.backend.rss.RssArticle
import org.apache.commons.io.IOUtils
import org.postgresql.ds.PGSimpleDataSource
import java.lang.invoke.MethodHandles
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.sql.DataSource
import kotlin.text.Charsets.UTF_8

private val dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S z", Locale.CANADA)

val source : DataSource by lazy {
	val cfg = HikariConfig()
	cfg.dataSource = PGSimpleDataSource().apply {
		databaseName = "eunewsapp"
		serverName = "localhost"
		user = "postgres"
	}
	HikariDataSource(cfg)
}

fun initDB()
{
	val clazz = MethodHandles.lookup().lookupClass()
	
	// create the database
	val createSql = IOUtils.toString(clazz.getResourceAsStream("create.sql"), UTF_8)
	JdbcSession(source).sql(createSql).execute()
	
	// seed the database with some sources
	val sourcesSql = IOUtils.toString(clazz.getResourceAsStream("sources.sql"), UTF_8)
	JdbcSession(source).sql(sourcesSql).execute()
}

fun store(articles : List<RssArticle>, sourceId : Long)
{
	val sql = """INSERT INTO article (title, headline, source, link, img, pub_date, title_en, headline_en) VALUES (?, ?, ?, ?, ?, ?::timestamp, ?, ?);"""
	articles.forEach { article ->
		JdbcSession(source).sql(sql)
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
	val sql = """SELECT article.title, article.headline, source.country, source.name AS source, article.img, article.link, article.title_en, article.headline_en FROM article INNER JOIN source ON source.id = article.source ORDER BY pub_date DESC LIMIT ?;"""
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
