package com.TrendyPorn

import org.jsoup.nodes.Element
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class TrendyPorn : MainAPI() {
    override var mainUrl              = "https://www.trendyporn.com"
    override var name                 = "TrendyPorn"
    override val hasMainPage          = true
    override var lang                 = "en"
    override val hasQuickSearch       = false
    override val hasDownloadSupport   = true
    override val supportedTypes       = setOf(TvType.NSFW)
    override val vpnStatus            = VPNStatus.MightBeNeeded

    override val mainPage = mainPageOf(
        "${mainUrl}/" to "Home",
        "${mainUrl}/most-recent/" to "Most Recent",
        "${mainUrl}/most-viewed/" to "Most Viewed",
        "${mainUrl}/random/" to "Random",
        "${mainUrl}/channels/26/hd/" to "HD",
        "${mainUrl}/channels/2/amateur/" to "Amateur",
        "${mainUrl}/channels/3/anal/" to "Anal",
        "${mainUrl}/channels/1/asian/" to "Asian",
        "${mainUrl}/channels/4/ass/" to "Ass",
        "${mainUrl}/channels/5/bbw/" to "BBW",
        "${mainUrl}/channels/6/bdsm/" to "BDSM",
        "${mainUrl}/channels/7/blonde/" to "Blonde",
        "${mainUrl}/channels/8/blowjobs/" to "BlowJobs", 
        "${mainUrl}/channels/9/british/" to "British",
        "${mainUrl}/channels/10/bukkake/" to "Bukkake",
        "${mainUrl}/channels/11/cartoons/" to "Cartoons",
        "${mainUrl}/channels/12/celebrity/" to "Celebrity", 
        "${mainUrl}/channels/14/close-ups/" to "Close Ups",
        "${mainUrl}/channels/15/couples/" to "Couples",
        "${mainUrl}/channels/16/deepthroat/" to "DeepThroat",
        "${mainUrl}/channels/53/desi/" to "Desi",
        "${mainUrl}/channels/52/dildos/" to "Dildos",
        "${mainUrl}/channels/17/ebony/" to "Ebony",
        "${mainUrl}/channels/18/femdom/" to "Femdom", 
        "${mainUrl}/channels/19/flashing/" to "Flashing",
        "${mainUrl}/channels/20/gangbang/" to "Gangbang",
        "${mainUrl}/channels/21/gay/" to "Gay",
        "${mainUrl}/channels/22/girlfriend/" to "Girlfriend",
        "${mainUrl}/channels/23/group-sex/" to "Group Sex",
        "${mainUrl}/channels/51/hairy/" to "Hairy",
        "${mainUrl}/channels/24/handjobs/" to "Handjobs",
        "${mainUrl}/channels/25/hardcore/" to "Hardcore",
        "${mainUrl}/channels/27/hentai/" to "Hentai",
        "${mainUrl}/channels/50/hidden-cams/" to "Hidden Cams",
        "${mainUrl}/channels/28/home-made/" to "Home Made",
        "${mainUrl}/channels/49/indonesian/" to "Indonesian", 
        "${mainUrl}/channels/31/japanese/" to "Japanese", 
        "${mainUrl}/channels/32/kissing/" to "Kissing",
        "${mainUrl}/channels/48/korean/" to "Korean", 
        "${mainUrl}/channels/33/lesbians/" to "Lesbians",
        "${mainUrl}/channels/34/massage/" to "Massage",
        "${mainUrl}/channels/35/milf/" to "Milfs",
        "${mainUrl}/channels/36/nipples/" to "Nipples",
        "${mainUrl}/channels/54/pornstars/" to "Pornstars", 
        "${mainUrl}/channels/55/pov/" to "POV",
        "${mainUrl}/channels/37/pubkic/" to "Public",
        "${mainUrl}/channels/47/pussy/" to "Pussy",
        "${mainUrl}/channels/46/russian/" to "Russian",
        "${mainUrl}/channels/38/shemale/" to "Shemale",
        "${mainUrl}/channels/39/showers/" to "Showers",
        "${mainUrl}/channels/40/teen/" to "Teen",
        "${mainUrl}/channels/41/tits/" to "Tits",
        "${mainUrl}/channels/42/toys/" to "Toys",
        "${mainUrl}/channels/43/vintage/" to "Vintage",
        "${mainUrl}/channels/43/webcams/" to "Webcams",
        "${mainUrl}/channels/45/wife/" to "Wife"
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val document = app.get(request.data + "page" + page + ".html").document
        val home = document.select("div.well-sm").mapNotNull { it.toSearchResult() }

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
        val title = this.select("a").attr("title")
        val href = this.select("a").attr("href")
        val posterUrl = this.select("img").attr("data-original")
        return newMovieSearchResponse(title, href, TvType.Movie) {
            this.posterUrl = posterUrl
        }
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val searchResponse = mutableListOf<SearchResponse>()

        for (i in 1..5) {
            val document = app.get("${mainUrl}/search/${query}/page${i}.html").document

            val results = document.select("div.well-sm").mapNotNull { it.toSearchResult() }

            searchResponse.addAll(results)

            if (results.isEmpty()) break
        }

        return searchResponse
    }

    override suspend fun load(url: String): LoadResponse {
        val document = app.get(url).document
        val title = document.selectFirst("meta[property=og:title]")?.attr("content") ?:""
        val posterUrl = fixUrlNull(document.selectFirst("meta[property=og:image]")?.attr("content")) ?:""

        return newMovieLoadResponse(title, url, TvType.NSFW, url) {
            this.posterUrl = posterUrl
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
        ): Boolean {

        val document = app.get(data).document
        val link = document.selectFirst("source")?.attr("src") ?:""

        callback.invoke(
            newExtractorLink(
                source = this.name,
                name = this.name,
                url = link
            ) {
                this.referer = ""
                this.quality = Qualities.Unknown.value
            }
        )
        return true
    }
}