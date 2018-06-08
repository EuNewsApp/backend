package eu.newsapp.backend.db

import com.jcabi.jdbc.JdbcSession
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
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
	val sql = "INSERT INTO article (title, headline, source, img, pub_date) VALUES (?, ?, ?, ?, ?::timestamp);"
	articles.forEach { article ->
		JdbcSession(source).sql(sql)
				.set(article.title)
				.set(article.description)
				.set(sourceId)
				.set(article.enclosureUrl)
				.set(article.pubDate.let { dtf.format(it) })
				.execute()
	}
}
