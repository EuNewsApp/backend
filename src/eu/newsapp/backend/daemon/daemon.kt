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

package eu.newsapp.backend.daemon

import eu.newsapp.backend.*
import eu.newsapp.backend.db.*
import eu.newsapp.backend.rss.*
import org.slf4j.*
import java.lang.Runtime.*
import java.time.*
import java.util.TimerTask
import kotlin.concurrent.fixedRateTimer

private val logger : Logger = LoggerFactory.getLogger("eu.newsapp.backend.daemon")

private fun TimerTask.execDaemon()
{
	// load sources
	val sources = loadSources()
	
	// scrape sources
	val articles = sources.filter { source ->
		source.autoScrape
	}.flatMap { source ->
		try
		{
			val reader = RssReader()
			reader.read(source.rss).map { source.id to it }
		}
		catch (ex : Exception)
		{
			logger.error("Failed to scrape $source", ex)
			emptyList<Pair<Long, RssArticle>>()
		}
	}
	
	// filter article must contain europe
	val filteredArticles = articles.filter { (_, article) ->
		(article.headline + " " + article.teaser).toLowerCase().let {
			it.contains("eu")
			// english & french
			|| it.contains("europe")
			// german & spain & portugese
			|| it.contains("europa")
		}
	}
	
	// filter articles to check that they are new
	val newArticles = filteredArticles.filterNot { (sourceId, article) ->
		articleExists(sourceId, article.hash) || articleLinkExists(article.link) // TODO update if link exist instead of ignore
	}
	
	// group articles by source
	val articlesBySource = newArticles.groupBy { (sourceId, _) ->
		sourceId
	}.mapValues { (_, articleList) ->
		articleList.map { (_, article) ->
			article
		}
	}
	
	// store new articles
	articlesBySource.forEach { (sourceId, articleList) ->
		articleList.store(sourceId)
	}
	
	// log
	println("Scraping successful, {'articles':${articles.size}, 'filteredArticles':${filteredArticles.size}, 'newArticles':${newArticles.size}}")
	
	// store scrape log
	val scrapeLog = ScrapeLog(
			timestamp = ZonedDateTime.now(),
			articlesTotal = articles.size,
			articlesIncluded = filteredArticles.size,
			articlesNew = newArticles.size
	)
	val scrapeLogId = insertScrapeLog(scrapeLog)
	
	// publish scrape log to elasticsearch
	scrapeLog.publishToElasticsearch(scrapeLogId)
	
	// group articles by language
	val articlesByLanguage = newArticles.mapNotNull { (sourceId, article) ->
		sources.find { it.id == sourceId }?.to(article)
	}.groupBy { (source, _) ->
		source.language
	}.mapValues { (_, articleList) ->
		articleList.map { (_, article) ->
			article
		}
	}
	
	// mark all english articles as translated
	articlesByLanguage.filter { (language, _) ->
		language == IsoAlpha2.EN
	}.forEach articleList@ { (_, articleList) ->
		articleList.forEach article@ { article ->
			setArticleTranslated(article.hash)
		}
	}
	
	// translate those articles
	articlesByLanguage.filter { (language, _) ->
		language != IsoAlpha2.EN
	}.forEach articleList@ { (language, articleList) ->
		val now = ZonedDateTime.now()
		val batch = articleList.map { article -> article.headline to article.teaser }.buildBatch()
		val translated = batch.translate(language).parseBatch()
		articleList.withIndex().forEach article@ { (i, article) ->
			val (headline, teaser) = translated.getOrNull(i) ?: return@article
			
			// get article id
			val articleId = loadArticle(article.hash)?.id
			if (articleId == null)
			{
				logger.error("Error loading article $article, hash not found: ${article.hash}")
				return@article
			}
			
			// store translation
			Translation(
					id = -1,
					articleId = articleId,
					language = IsoAlpha2.EN,
					headline = headline,
					teaser = teaser,
					translatedAt = now
			).store()
			
			// mark article as translated
			setArticleTranslated(article.hash)
			
			// store translation log
			TranslationLog(
					timestamp = now,
					languageFrom = language,
					languageTo = IsoAlpha2.EN,
					characterCount = batch.length
			).insert()
		}
	}
}

fun startDaemon()
{
	val fixedRateTimer = fixedRateTimer(name = "daemon", action = TimerTask::execDaemon, initialDelay = 1000L, period = 600_000L)
	
	getRuntime().addShutdownHook(Thread {
		fixedRateTimer.cancel()
	})
}
