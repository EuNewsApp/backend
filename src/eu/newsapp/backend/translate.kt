package eu.newsapp.backend

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.translate.AmazonTranslate
import com.amazonaws.services.translate.AmazonTranslateClient
import com.amazonaws.services.translate.model.TranslateTextRequest

val translateClient: AmazonTranslate by lazy {
    val awsCreds = ProfileCredentialsProvider().credentials

    AmazonTranslateClient.builder()
        .withCredentials(AWSStaticCredentialsProvider(awsCreds))
        .withRegion(Regions.US_EAST_1)
        .build()
}

fun String.translate(sourceLanguage : String): String {
    val request = TranslateTextRequest()
            .withText(this)
            .withSourceLanguageCode(sourceLanguage.toLowerCase())
            .withTargetLanguageCode("en")
    val result = translateClient.translateText(request)
    return result.translatedText
}

fun List<Pair<String, String>>.buildBatch() = joinToString("☰") { (title, headline) ->
    "$title☲$headline"
}

fun String.parseBatch(): List<Pair<String, String>> {
    return this.split("☰").map {
        println(it)
        it.split("☲").let {
            it[0] to it[1]
        }
    }
}
