package com.owencz1998

import org.jsoup.nodes.Element
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class porn4days : MainAPI() {
    override var mainUrl              = "https://porn4days.red/?ref=porndude"
    override var name                 = "porn4days"
    override val hasMainPage          = true
    override var lang                 = "en"
    override val hasQuickSearch       = false
    override val hasDownloadSupport   = true
    override val hasChromecastSupport = true
    override val supportedTypes       = setOf(TvType.NSFW)
    override val vpnStatus            = VPNStatus.MightBeNeeded

     override val mainPage = mainPageOf(
        Pair(mainUrl, "Main Page"),
        Pair("$mainUrl/new/", "New"),
        Pair("$mainUrl/c/squirting-56/",
"Squirt"),
        Pair("$mainUrl/c/amateur-65/",
"Amateur"),
        Pair("$mainUrl/c/teen-13/", 
"Teen"),
        Pair("$mainUrl/c/big_Tits-23/",
"Big tits"),
        Pair("$mainUrl/c/lesbian-26/", 
"Lesbian"),
        Pair("$mainUrl/c/anal-12/",
"Anal"),
        Pair("$mainUrl/c/blowjob-15/",
"Blowjob"),
        Pair("$mainUrl/c/solo_and_masturbation-33/",
"Solo"),
        Pair("$mainUrl/c/cumshot-18", 
"Cumshot"),
        Pair("$mainUrl/c/gangbang-69/", 
"Gangbang"),
        Pair("$mainUrl/c/big_cock-34/", 
"Big cock"),
        Pair("$mainUrl/c/fisting-165/", 
"Fisting"),
        Pair("$mainUrl/c/blonde-20/", 
"Blonde"),
        Pair("$mainUrl/c/brunette-25/", 
"Brunette"),
        Pair("$mainUrl/c/fucked_up_family-81/", 
"Family"),/
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val document = app.get(request.data + page).document
        val home     = document.select("div.col-12.col-md-4.col-lg-3.col-xl-3 > div.video-block").mapNotNull { it.toSearchResult() }

        return newHomePageResponse(
            list    = HomePageList(
                name               = request.name,
                list               = home,
                isHorizontalImages = true
            ),
            hasNext = true
        )
    }

    private fun Element.toSearchResult(): SearchResponse {
        val title     = fixTitle(this.select("a.infos").attr("title")).trim()
        val href      = fixUrl(this.select("a.infos").attr("href"))
        val posterUrl = fixUrlNull(this.select("a.thumb > img").attr("data-src"))

        return newMovieSearchResponse(title, href, TvType.Movie) {
            this.posterUrl = posterUrl
        }
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val searchResponse = mutableListOf<SearchResponse>()

        for (i in 1..10) {
            val document = app.get("${mainUrl}/page/$i?s=$query").document

            val results = document.select("div.col-12.col-md-4.col-lg-3.col-xl-3 > div.video-block").mapNotNull { it.toSearchResult() }

            if (!searchResponse.containsAll(results)) {
                searchResponse.addAll(results)
            } else {
                break
            }

            if (results.isEmpty()) break
        }

        return searchResponse
    }

    override suspend fun load(url: String): LoadResponse {
        val document = app.get(url).document

        val title       = document.selectFirst("meta[property=og:title]")?.attr("content")?.trim().toString()
        val poster      = fixUrlNull(document.selectFirst("[property='og:image']")?.attr("content"))
        val description = document.selectFirst("meta[property=og:description]")?.attr("content")?.trim()
    

        return newMovieLoadResponse(title, url, TvType.NSFW, url) {
            this.posterUrl = poster
            this.plot      = description
        }
    }

    override suspend fun loadLinks(data: String, isCasting: Boolean, subtitleCallback: (SubtitleFile) -> Unit, callback: (ExtractorLink) -> Unit): Boolean {
        val document = app.get(data).document

        document.select("div.video-player").map { res ->
            callback.invoke(
                    ExtractorLink(
                        source  = this.name,
                        name    = this.name,
                        url     = fixUrl(res.selectFirst("meta[itemprop=contentURL]")?.attr("content")?.trim().toString()),
                        referer = data,
                        quality = Qualities.Unknown.value
                    )
            )
        }

        return true
    }
}