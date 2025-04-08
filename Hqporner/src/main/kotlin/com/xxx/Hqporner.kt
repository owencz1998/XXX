package com.XXX


import org.jsoup.nodes.Element
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import com.lagradost.cloudstream3.utils.AppUtils.toJson
import com.lagradost.cloudstream3.utils.AppUtils.tryParseJson

class Hqporner : MainAPI() {
    override var mainUrl              = "https://hqporner.com"
    override var name                 = "Hqporner"
    override val hasMainPage          = true
    override var lang                 = "en"
    override val hasDownloadSupport   = true
    override val supportedTypes       = setOf(TvType.NSFW)
    override val vpnStatus            = VPNStatus.MightBeNeeded

        override val mainPage = mainPageOf(
        "${mainUrl}/top/week" to "Top Week",
        "${mainUrl}/top/month" to "Top Month",
        "${mainUrl}/top" to "All Time Best Porn",
        "${mainUrl}/studio/free-brazzers-videos" to "Brazzers",
        "${mainUrl}/category/4k-porn" to "4k",
        "${mainUrl}/category/1080p-porn" to "1080p",
        "${mainUrl}/category/60fps-porn" to "60fps",
        "${mainUrl}/category/milf" to "Milf",
        "${mainUrl}/category/old-and-young" to "Old And Young",
        "${mainUrl}/category/teen-porn" to "Teen",
        "${mainUrl}/category/amateur" to "Amateur",
        "${mainUrl}/category/big-tits" to "Big Tits",
        "${mainUrl}/category/small-tits" to "Small Tits",
        "${mainUrl}/category/big-dick" to "Big Dick",
        "${mainUrl}/category/handjob" to "Handjob",
        "${mainUrl}/category/blowjob" to "Blowjob",
        "${mainUrl}/category/deepthroat" to "DeepThroat",
        "${mainUrl}/category/cumshot" to "Cumshot",
        "${mainUrl}/category/blonde" to "Blonde",
        "${mainUrl}/category/brunette" to "Brunette",
        "${mainUrl}/category/redhead" to "Redhead",
        "${mainUrl}/category/lesbian" to "Lesbian",
        "${mainUrl}/studio/free-brazzers-videos" to "Brazzers",
        "${mainUrl}/category/shemale" to "Shemale",
        "${mainUrl}/category/public" to "Public",
        "${mainUrl}/category/pickup" to "Pickup",
        "${mainUrl}/category/outdoor" to "Outdoor",
        "${mainUrl}/category/beach" to "Beach",
        "${mainUrl}/category/threesome" to "Threesome",
        "${mainUrl}/category/gangbang" to "Gangbang",
        "${mainUrl}/category/orgy" to "Orgy",
        "${mainUrl}/category/group-sex" to "Group Sex",
        "${mainUrl}/category/sex-party" to "Sex Party",
        "${mainUrl}/category/bdsm" to "Bdsm",
        "${mainUrl}/category/bondage" to "Bondage",
        "${mainUrl}/category/fetish" to "Fetish",
        "${mainUrl}/category/fisting" to "Fisting",
        "${mainUrl}/category/masterbation" to "Masterbation",
        "${mainUrl}/category/fingering" to "Fingering",
        "${mainUrl}/category/orgasm" to "Orgasm",
        "${mainUrl}/category/squirt" to "Squirt",
        "${mainUrl}/category/anal" to "Anal",
        "${mainUrl}/category/creampie" to "Creampie",
        "${mainUrl}/category/uniforms" to "Uniforms",
        "${mainUrl}/category/russian" to "Russian",
        "${mainUrl}/category/asian" to "Asian",
        "${mainUrl}/category/japanese-girls-porn" to "Japanese",

    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val document = app.get(request.data).document
        val home     = document.select("div.box.page-content div.row section").mapNotNull {
            it.toSearchResult()
        }

        return newHomePageResponse(
            list = HomePageList(
                name = request.name,
                list = home,
                isHorizontalImages = true
            ),
            hasNext = true
        )
    }

    private fun Element.toSearchResult(): SearchResponse {
        val capitalizedTitle     = this.selectFirst("h3 a")?.text() ?:"No Title"
        val title  = capitalizedTitle.split(" ")
            .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
        val href       = fixUrl(this.selectFirst("h3 a")!!.attr("href"))
        val posterUrl  = fixUrlNull(this.select("img").attr("src"))

        return newMovieSearchResponse(title, LoadUrl(href, posterUrl).toJson(),TvType.Movie) {
            this.posterUrl = posterUrl
        }

    }

    override suspend fun search(query: String): List<SearchResponse> {
        val searchResponse = mutableListOf<SearchResponse>()
        for (i in 1..2) {
            val document = app.get("${mainUrl}/?q=$query&p=$i").document
            val results = document.select("div.box.page-content div.row section").mapNotNull { it.toSearchResult() }
            searchResponse.addAll(results)
            if (results.isEmpty()) break
        }
        return searchResponse
    }

    override suspend fun load(url: String): LoadResponse? {
        val d = tryParseJson<LoadUrl>(url) ?: return null
        val document = app.get(d.href).document
        val capitalizedTitle= document.selectFirst("header > h1")?.text()?.trim().toString()
        val title  = capitalizedTitle.split(" ")
            .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
        val poster= d.posterUrl
        val plot="Hqporner"
        return newMovieLoadResponse(title, url, TvType.NSFW, d.href) {
            this.posterUrl       = poster
            this.plot=plot
        }
    }

    override suspend fun loadLinks(data: String, isCasting: Boolean, subtitleCallback: (SubtitleFile) -> Unit, callback: (ExtractorLink) -> Unit): Boolean {
        val document = app.get(data).document
        val doc=document.toString()
        val rawurl = Regex("""url: '/blocks/altplayer\.php\?i=//(.*?)',""").find(doc)?.groupValues?.get(1) ?:""
        val href= "https://$rawurl"
        loadExtractor(
            href,
            subtitleCallback,
            callback
        )
        return true
    }

    data class LoadUrl(
        val href: String,
        val posterUrl: String?
    )
}