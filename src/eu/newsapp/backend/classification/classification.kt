package eu.newsapp.backend.classification

import com.google.cloud.language.v1.ClassifyTextRequest
import com.google.cloud.language.v1.ClassifyTextResponse
import com.google.cloud.language.v1.Document
import com.google.cloud.language.v1.Document.Type
import com.google.cloud.language.v1.LanguageServiceClient
import eu.newsapp.backend.db.Article
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger : Logger = LoggerFactory.getLogger("eu.newsapp.backend.classification")

val confidenceThreshold = 0.5

val language : LanguageServiceClient by lazy {
    LanguageServiceClient.create()
}

fun Article.classify() = classifyArticle(titleEn ?: title, headlineEn ?: headline)

fun classifyArticle(title: String, headline: String): String = try {
	logger.info("Classifying article '$title' ...")
    val doc : Document = Document.newBuilder().setContent(title + " " + headline).setType(Type.PLAIN_TEXT).build()
    val request : ClassifyTextRequest = ClassifyTextRequest.newBuilder().setDocument(doc).build()
    val response : ClassifyTextResponse = language.classifyText(request)

	logger.info("Classified: ${response.categoriesList.joinToString { "${it.name} (${it.confidence})" }}")
	
    response.categoriesList.filter { category ->
        category.confidence > confidenceThreshold
    }.joinToString("|") { category ->
        category.name
    }
} catch (ex : Exception) {
    logger.error("Error while classifying '$title'", ex)
    ""
}

fun String.categoriesToList() = if (isBlank()) emptyList() else split("|")
