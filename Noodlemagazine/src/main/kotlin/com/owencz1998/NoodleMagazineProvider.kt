package com.owencz1998

import com.fasterxml.jackson.annotation.JsonProperty
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.network.WebViewResolver
import com.lagradost.cloudstream3.utils.*
import org.jsoup.nodes.Element

class NoodleMagazineProvider : MainAPI() { 
    override var mainUrl = "https://noodlemagazine.com"
    override var name = "Noodle Magazine"
    override val hasMainPage = true
    override var lang = "en"
    override val hasDownloadSupport = true
    override val supportedTypes = setOf(
            TvType.NSFW
    )

    override val mainPage = mainPageOf(
            "latest" to "Latest",
            "onlyfans" to "Onlyfans",
            // ... (other categories)
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        // ... (main page logic unchanged)
    }

    private fun Element.toSearchResult(): MovieSearchResponse? {
        // ... (search result parsing unchanged)
    }

    override suspend fun search(query: String): List<MovieSearchResponse> {
        // ... (search logic unchanged)
    }

    override suspend fun load(url: String): LoadResponse {
        // ... (load response logic unchanged)
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val webViewResult = app.get(data, interceptor = WebViewResolver(Regex("""/playlist/""")))

        if (!webViewResult.isSuccessful) {
            // Log or handle the error (e.g., return false)
            return false
        }

        val jason = webViewResult.parsed<SusJSON>()

        if (jason.sources.isEmpty()) {
            // Log or handle the lack of sources (e.g., return false)
            return false
        }

        jason.sources.forEach {
            callback(
                ExtractorLink(
                    source = name,
                    name = name,
                    url = it.streamlink!!,
                    referer = "$mainUrl/",
                    quality = getQualityFromName(it.qualityfile)
                )
            )
        }

        return true
    }

    data class SusJSON(
        @JsonProperty("image") val img: String? = null,
        @JsonProperty("sources") val sources: ArrayList<Streams> = arrayListOf()
    )

    data class Streams(
        @JsonProperty("file") val streamlink: String? = null,
        @JsonProperty("label") val qualityfile: String? = null,
        @JsonProperty("type") val type: String? = null,
    )
}
