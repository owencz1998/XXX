package PornHat

import com.fasterxml.jackson.annotation.JsonProperty
import com.lagradost.cloudstream3.HomePageList
import com.lagradost.cloudstream3.HomePageResponse
import com.lagradost.cloudstream3.LoadResponse
import com.lagradost.cloudstream3.LoadResponse.Companion.addActors
import com.lagradost.cloudstream3.TvType
import com.lagradost.cloudstream3.MainAPI
import com.lagradost.cloudstream3.MainPageRequest
import com.lagradost.cloudstream3.MovieSearchResponse
import com.lagradost.cloudstream3.SearchResponse
import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.mainPageOf
import com.lagradost.cloudstream3.newHomePageResponse
import com.lagradost.cloudstream3.newMovieLoadResponse
import com.lagradost.cloudstream3.newMovieSearchResponse
import com.lagradost.cloudstream3.utils.AppUtils.parseJson
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.Qualities
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.net.URLEncoder

class PornHat : MainAPI() {
    override var mainUrl = "https://www.pornhat.com"
    override var name = "PornHat"
    override val supportedTypes = setOf(TvType.NSFW)
    override var lang = "en"
    override val hasMainPage = true

    override val mainPage = mainPageOf(
        mainUrl to "New Videos",
        "$mainUrl/trending/" to "Trending",
        "$mainUrl/popular/" to "Popular",
    )

    private fun getVideos(document: Document): List<MovieSearchResponse> {
        val videoList = document.select(".list_video_wrapper").select(".item")
        val searchResponses = videoList.map { it.toSearchResponse() }
        return searchResponses
    }

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val response = app.get(request.data)
        val searchResponses = getVideos(response.document)
        return newHomePageResponse(
            HomePageList(request.name, searchResponses, true), false
        )
    }

    private fun Element.toSearchResponse(): MovieSearchResponse {
        val a = this.select("a").first()!!
        val title = a.attr("title")
        val url = mainUrl + a.attr("href")
        val poster = a.select("img").attr("data-original")
        return newMovieSearchResponse(title, url, TvType.NSFW) {
            this.posterUrl = poster
        }
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val safeQuery = URLEncoder.encode(query, "UTF-8")
        val response = app.get("$mainUrl/search/$safeQuery/")

        return getVideos(response.document)
    }

    override suspend fun load(url: String): LoadResponse {
        val response = app.get(url)
        val document = response.document
        val script = document.select("script[type=\"application/ld+json\"]").first()!!.data()
        val data = parseJson<VideoData>(script)
        val videoElement = document.select("#my-video").toString()
        return newMovieLoadResponse(data.name, url, TvType.NSFW, videoElement) {
            this.plot = data.description
            this.posterUrl = data.thumbnailUrl
            this.tags = data.keywords
            this.duration = parseDuration(data.duration)
            addActors(data.actor)
        }
    }

    private fun parseDuration(timeStr: String): Int {
        // Remove the 'PT' prefix
        var remainingStr = timeStr.substring(2)

        // Initialize hours, minutes, and seconds
        var hours = 0
        var minutes = 0
        var seconds = 0

        // Extract hours
        if (remainingStr.contains("H")) {
            val parts = remainingStr.split("H")
            hours = parts[0].toInt()
            remainingStr = parts[1]
        }

        // Extract minutes
        if (remainingStr.contains("M")) {
            val parts = remainingStr.split("M")
            minutes = parts[0].toInt()
            remainingStr = parts[1]
        }

        // Extract seconds
        if (remainingStr.contains("S")) {
            val parts = remainingStr.split("S")
            seconds = parts[0].toInt()
        }

        // Calculate total seconds
        var result = hours * 60 + minutes
        if (seconds > 30) {
            result++
        }
        return result
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit,
    ): Boolean {
        val videoPlayer = Jsoup.parse(data)

        val url = videoPlayer.select("source[label=\"Auto\"]").attr("src")
        if (url.isEmpty()) {
            return false
        }

        callback(
            ExtractorLink(
                this.name,
                this.name,
                url,
                "",
                Qualities.Unknown.value,
                isM3u8 = true
            )
        )
        return true
    }
}

data class VideoData(
    @JsonProperty("actor")
    val actor: List<String>?,
    @JsonProperty("description")
    val description: String?,
    @JsonProperty("duration")
    val duration: String,
    @JsonProperty("keywords")
    val keywords: List<String>?,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("thumbnailUrl")
    val thumbnailUrl: String?,
    @JsonProperty("uploadDate")
    val uploadDate: String?
)


