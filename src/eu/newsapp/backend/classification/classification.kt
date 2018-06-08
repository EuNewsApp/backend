package eu.newsapp.backend.classification

import com.google.cloud.language.v1.ClassifyTextRequest
import com.google.cloud.language.v1.ClassifyTextResponse
import com.google.cloud.language.v1.Document
import com.google.cloud.language.v1.Document.Type
import com.google.cloud.language.v1.LanguageServiceClient

val confidenceThreshold = 0.5;

val language : LanguageServiceClient by lazy {
    LanguageServiceClient.create()
}

fun classifyArticle(title: String, headline: String): String{
    val doc : Document = Document.newBuilder().setContent(title + " " + headline).setType(Type.PLAIN_TEXT).build()
    val request : ClassifyTextRequest = ClassifyTextRequest.newBuilder().setDocument(doc).build()
    val response : ClassifyTextResponse = language.classifyText(request)

    return response.categoriesList.filter { category ->
        category.confidence > confidenceThreshold
    }.joinToString("|") { category ->
        category.name
    }
}

fun categoriesToList(categories: String): List<String>{
    return categories.split("|")
}