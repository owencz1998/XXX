package com.owencz1998

import com.fasterxml.jackson.annotation.JsonProperty
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.network.WebViewResolver
import com.lagradost.cloudstream3.utils.*
import org.jsoup.nodes.Element

class papirest : MainAPI() { // all providers must be an instance of MainAPI
    override var mainUrl = "https://www.papi.rest/"
    override var name = "Papirest"
    override val hasMainPage = true
    override var lang = "en"
    override val hasDownloadSupport = true
    override val supportedTypes = setOf(
            TvType.NSFW
    )

        override val mainPage = mainPageOf(
        Pair(mainUrl, "Main Page"),
        Pair("$mainUrl/pornstar/cherie-deville-40/", "Ne"),
        Pair("$mainUrl/categories/squirting/",
"Squirt"),
    )


    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val curpage = page - 1
        val link = "$mainUrl/video/${request.data}?p=$curpage"
        val document = app.get(link).document
        val home = document.select("div.item").mapNotNull {
            it.toSearchResult()
        }
        return newHomePageResponse(request.name, home)
    }


    private fun Element.toSearchResult(): MovieSearchResponse? {

        val href = fixUrl(this.selectFirst("a")?.attr("href") ?: return null)
        val title = this.selectFirst("a div.i_info div.title")?.text() ?: return null
        val posterUrl = fixUrlNull(this.selectFirst("a div.i_img img")?.attr("data-src"))

        return newMovieSearchResponse(title, href, TvType.Movie) {

            this.posterUrl = posterUrl
        }
    }

    override suspend fun search(query: String): List<MovieSearchResponse> {
        val searchresult = mutableListOf<MovieSearchResponse>()

        (0..10).toList().apmap { page ->
            val doc = app.get("$mainUrl/video/$query?p=$page").document
            //return document.select("div.post-filter-image").mapNotNull {
            doc.select("div.item").apmap { res ->
                searchresult.add(res.toSearchResult()!!)
            }

        }

        return searchresult
    }

    override suspend fun load(url: String): LoadResponse {
        val document = app.get(url).document
        val title = document.selectFirst("div.l_info h1")?.text()?.trim() ?: "null"
        val poster =
                document.selectFirst("""meta[property="og:image"]""")?.attr("content") ?: "null"

        val recommendations = document.select("div.item").mapNotNull {
            it.toSearchResult()
        }

        return newMovieLoadResponse(title, url, TvType.NSFW, url) {
            this.posterUrl = poster
            this.recommendations = recommendations
        }

    }


    override suspend fun loadLinks(
            data: String,
            isCasting: Boolean,
            subtitleCallback: (SubtitleFile) -> Unit,
            callback: (ExtractorLink) -> Unit
    ): Boolean {
        val jason = app.get(
                data, interceptor = WebViewResolver(Regex("""/playlist/"""))
        ).parsed<SusJSON>()
        val extlinkList = mutableListOf<ExtractorLink>()
        jason.sources.map {
            extlinkList.add(
                    ExtractorLink(
                            source = name,
                            name = name,
                            url = it.streamlink!!,
                            referer = "$mainUrl/",
                            quality = getQualityFromName(it.qualityfile)
                    )
            )
        }
        extlinkList.forEach(callback)
        return true
    }

    data class SusJSON(
            @JsonProperty("image") val img: String? = null,
            @JsonProperty("sources") val sources: ArrayList<Streams> = arrayListOf()
    )

    data class Streams(
            @JsonProperty("file") val streamlink: String? = null,//the link
            @JsonProperty("label") val qualityfile: String? = null,//720 480 360 240
            @JsonProperty("type") val type: String? = null,//mp4
    )

}