package eu.newsapp.backend.db

import com.jcabi.jdbc.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import eu.newsapp.backend.Configuration
import eu.newsapp.backend.rss.RssArticle
import org.apache.commons.io.IOUtils
import org.postgresql.ds.PGSimpleDataSource
import sun.security.krb5.Config
import java.lang.invoke.MethodHandles
import java.sql.ResultSet
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.sql.DataSource
import kotlin.text.Charsets.UTF_8

internal val dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S z", Locale.CANADA)

val source : DataSource by lazy {
	val cfg = HikariConfig()
	cfg.dataSource = PGSimpleDataSource().apply {
		databaseName = Configuration.db.name
		serverName = Configuration.db.name
		portNumber = Configuration.db.port
		user = Configuration.db.username
		password = Configuration.db.password
	}
	cfg.maximumPoolSize = 2
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

abstract class ListOutcomeMapping<T> : ListOutcome.Mapping<T>
{
	abstract fun map(rs : ResultSet, prefix : String) : T
	
	override fun map(rs : ResultSet) : T
		= map(rs, prefix = "")
}
