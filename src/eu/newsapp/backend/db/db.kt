package eu.newsapp.backend.db

import com.jcabi.jdbc.JdbcSession
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.apache.commons.io.IOUtils
import org.postgresql.ds.PGSimpleDataSource
import java.lang.invoke.MethodHandles
import javax.sql.DataSource
import kotlin.text.Charsets.UTF_8

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
	val sql = IOUtils.toString(clazz.getResourceAsStream("create.sql"), UTF_8)
	JdbcSession(source).sql(sql).execute()
}
