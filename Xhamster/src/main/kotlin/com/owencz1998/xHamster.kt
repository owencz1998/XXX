package com.owencz1998

import org.jsoup.nodes.Element
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class xHamster : MainAPI() {
    override var mainUrl              = "https://xhamster.com"
    override var name                 = "xHamster"
    override val hasMainPage          = true
    override var lang                 = "en"
    override val hasQuickSearch       = false
    override val hasDownloadSupport   = true
    override val hasChromecastSupport = true
    override val supportedTypes       = setOf(TvType.NSFW)
    override val vpnStatus            = VPNStatus.MightBeNeeded

    override val mainPage = mainPageOf(
        "${mainUrl}/newest/"              to "Newest",
        "${mainUrl}/most-viewed/weekly/"  to "Most viewed weekly",
        "${mainUrl}/most-viewed/monthly/" to "Most viewed monthly",
        "${mainUrl}/most-viewed"          to "Most viewed all time",
        "${mainUrl}/most-viewed/weekly/"  to "Most viewed weekly",
        "${mainUrl}/categories/amateur/"  to "Amateur",
        "${mainUrl}/categories/anal-masturbation"  to "Anal Masterbation",
        "${mainUrl}/categories/bdsm/"  to "BDSM",
        "${mainUrl}/categories/babe/"  to "Babe",
        "${mainUrl}/categories/18-year-old/"  to "18",
        "${mainUrl}/categories/blowbang/"  to "Blowbang",
        "${mainUrl}/categories/blowjob/"  to "Blowjob",
        "${mainUrl}/categories/bukkake/"  to "Bukkake",
        "${mainUrl}/categories/cum-swallowing/"  to "Cum Swallow",
        "${mainUrl}/categories/cowgirl/"  to "Cowgirl",
        "${mainUrl}/categories/creampie/"  to "CreamPie",
        "${mainUrl}/categories/cum-on-tits/"  to "Cum On Tits",
        "${mainUrl}/categories/deep-throat/"  to "Deep Throat",
        "${mainUrl}/categories/doggy-style/"  to "Doggy Style",
        "${mainUrl}/categories/fingering/"  to "Fingering"
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val document = app.get(request.data + page + "?x_platform_switch=desktop").document
        val home     = document.select("div.thumb-list div.thumb-list__item").mapNotNull { it.toSearchResult() }

        return newHomePageResponse(
            list    = HomePageList(
                name               = request.name,
                list               = home,
                isHorizontalImages = true
            ),
            hasNext = true
        )
    }

    private fun Element.toSearchResult(): SearchResponse? {
        val title     = this.selectFirst("a.video-thumb-info__name")?.text() ?: return null
        val href      = fixUrl(this.selectFirst("a.video-thumb-info__name")!!.attr("href"))
        val posterUrl = fixUrlNull(this.select("img.thumb-image-container__image").attr("src"))

        return newMovieSearchResponse(title, href, TvType.Movie) { this.posterUrl = posterUrl }
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val searchResponse = mutableListOf<SearchResponse>()

        for (i in 0 until 15) {
            val document = app.get("${mainUrl}/search/${query.replace(" ", "+")}/?page=$i&x_platform_switch=desktop").document

            val results = document.select("div.thumb-list div.thumb-list__item").mapNotNull { it.toSearchResult() }

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

        val title           = document.selectFirst("div.with-player-container h1")?.text()?.trim().toString()
        val poster          = fixUrlNull(document.selectFirst("div.xp-preload-image")?.attr("style")?.substringAfter("https:")?.substringBefore("\');"))
        val tags            = document.select(" nav#video-tags-list-container ul.root-8199e.video-categories-tags.collapsed-8199e li.item-8199e a.video-tag").map { it.text() }
        val recommendations = document.select("div.related-container div.thumb-list div.thumb-list__item").mapNotNull { it.toSearchResult() }

        return newMovieLoadResponse(title, url, TvType.NSFW, url) {
            this.posterUrl       = poster
            this.tags            = tags
            this.recommendations = recommendations
        }
    }

    override suspend fun loadLinks(data: String, isCasting: Boolean, subtitleCallback: (SubtitleFile) -> Unit, callback: (ExtractorLink) -> Unit): Boolean {
        app.get(url = data).let { response ->
            callback(
                ExtractorLink(
                    source  = name,
                    name    = name,
                    url     = fixUrl(response.document.selectXpath("//link[contains(@href,'.m3u8')]")[0]?.attr("href").toString()),
                    referer = mainUrl,
                    quality = Qualities.Unknown.value,
                    isM3u8  = true
                )
            )
        }

        return true
    }
}