package com.deflatedpickle.degen

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.Charsets
import io.ktor.client.plugins.CurlUserAgent
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.ktor.utils.io.charsets.Charsets

// https://github.com/4chan/4chan-API
object FourChan {
    const val REQUEST_DELAY = 60
    const val THREAD_DELAY = 10

    @Serializable
    data class BoardList(
        val boards: List<Board>,
    )

    // https://github.com/4chan/4chan-API/blob/d9a619833e1ef31ca9bdc353989dc0b1dd99970f/pages/Boards.md
    @Serializable
    data class Board(
        val board: String,
        val title: String,
        @SerialName("ws_board") val wsBoard: Int,
        @SerialName("per_page") val perPage: Int,
        val pages: Int,
        @SerialName("max_filesize") val maxFileSize: Int,
        @SerialName("max_webm_filesize") val maxWebmFileSize: Int,
        @SerialName("max_comment_chars") val maxCommentChars: Int,
        @SerialName("max_webm_duration") val maxWebmDuration: Int,
        @SerialName("bump_limit") val bumpLimit: Int,
        @SerialName("image_limit") val imageLimit: Int,
        val cooldowns: Cooldown,
        @SerialName("meta_description") val metaDescription: String,
        val spoilers: Int? = null,
        @SerialName("custom_spoilers") val customSpoilers: Int? = null,
        @SerialName("is_archived") val isArchived: Int? = null,
        @SerialName("board_flags") val boardFlags: Map<String, String>? = null,
        @SerialName("country_flags") val countryFlags: Int? = null,
        @SerialName("user_ids") val userIds: Int? = null,
        val oekaki: Int? = null,
        @SerialName("sjis_tags") val sjisTags: Int? = null,
        @SerialName("code_tags") val codeTags: Int? = null,
        @SerialName("math_tags") val mathTags: Int? = null,
        @SerialName("text_only") val textOnly: Int? = null,
        @SerialName("forced_anon") val forcedAnon: Int? = null,
        @SerialName("webm_audio") val webmAudio: Int? = null,
        @SerialName("require_subject") val requireSubject: Int? = null,
        @SerialName("min_image_width") val minImageWidth: Int? = null,
        @SerialName("min_image_height") val minImageHeight: Int? = null,
    )

    @Serializable
    data class Cooldown(
        val threads: Int,
        val replies: Int,
        val images: Int,
    )

    // https://github.com/4chan/4chan-API/blob/master/pages/Catalog.md
    @Serializable
    data class Catalog(
        val page: Int,
        val threads: List<Post>,
    )

    // todo: split into thread/reply subclasses
    @Serializable
    data class Post(
        val no: Int,
        val resto: Int,
        val sticky: Int? = null,
        val closed: Int? = null,
        val now: String,
        val time: Int,
        val name: String? = null,
        val trip: String? = null,
        val id: String? = null,
        val capcode: String? = null,
        val country: String? = null,
        @SerialName("country_name") val countryName: String? = null,
        val sub: String? = null,
        val com: String? = null,
        val tim: Long? = null,
        val filename: String? = null,
        val ext: String? = null,
        @SerialName("fsize") val fSize: Int? = null,
        val md5: String? = null,
        val w: Int? = null,
        val h: Int? = null,
        @SerialName("tn_w") val tnW: Int? = null,
        @SerialName("tn_h") val tn_h: Int? = null,
        @SerialName("filedeleted") val fileDeleted: Int? = null,
        val spoiler: Int? = null,
        @SerialName("custom_spoiler") val customSpoiler: Int? = null,
        @SerialName("omitted_posts") val omittedPosts: Int? = null,
        @SerialName("omitted_images") val omittedImages: Int? = null,
        val replies: Int? = null,
        val images: Int? = null,
        @SerialName("bumplimit") val bumpLimit: Int? = null,
        @SerialName("imagelimit") val imageLimit: Int? = null,
        @SerialName("last_modified") val lastModified: Int? = null,
        val tag: String? = null,
        @SerialName("semantic_url") val semanticUrl: String? = null,
        val since4pass: Int? = null,
        @SerialName("unique_ips") val uniqueIps: Int? = null,
        @SerialName("m_img") val mImg: Int? = null,
        @SerialName("last_replies") val lastReplies: List<Post>? = null,
    )

    private val client = HttpClient {
        install(Logging)
        install(HttpCookies)
        Charsets {
            register(Charsets.UTF_8)
            register(Charsets.ISO_8859_1, quality = 0.1f)
        }
        install(ContentNegotiation) {
            json()
        }
        CurlUserAgent()
        // todo: store boards and archives as persistent
        install(HttpCache)
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 5)
            exponentialDelay()
        }
    }

    private suspend fun query(url: String): HttpResponse {
        val response = client.get(url)
        println(response)

        println(response.status.description)
        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Status
        if (response.status.value in 200..299) {
            println("pog")
        }

        return response
    }

    suspend fun boards(): BoardList = query("https://a.4cdn.org/boards.json").body()

    // todo
    // suspend fun threadList(board: String) = client.get("https://a.4cdn.org/$board/thread.json")

    suspend fun catalog(board: String): List<Catalog> = query("https://a.4cdn.org/$board/catalog.json").body()

    // todo
    // suspend fun archive(board: String) = client.get("https://a.4cdn.org/$board/archive.json")

    // todo
    // suspend fun indexes(board: String, index: Int) = client.get("https://a.4cdn.org/$board/$index.json")

    // todo
    // suspend fun threads(board: String, thread: Int) = client.get("https://a.4cdn.org/$board/thread/$thread.json")
}