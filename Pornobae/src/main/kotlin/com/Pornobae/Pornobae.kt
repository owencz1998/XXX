package com.Pornobae

import org.jsoup.nodes.Element
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.extractors.StreamWishExtractor
import com.lagradost.cloudstream3.utils.*

class Pornobae : MainAPI() {
    override var mainUrl              = "https://pornobae.com"
    override var name                 = "Pornobae"
    override val hasMainPage          = true
    override var lang                 = "en"
    override val hasQuickSearch       = true
    override val hasDownloadSupport   = true
    override val hasChromecastSupport = true
    override val supportedTypes       = setOf(TvType.NSFW)
    override val vpnStatus            = VPNStatus.MightBeNeeded

    override val mainPage = mainPageOf(
            "category/brazzers" to "Brazzers",
            "category/adult-time" to "Adult Time",
            "category/bangbros" to "Bangbros",
            "category/ddf-network" to "DDF Network",
            "category/family-therapy" to "Family Therapy",
            "category/babes-network" to "Babes Network",
            "category/teamskeet/family-strokes" to "Family Strokes",
            "category/fakehub/fake-taxi" to "Fake Taxi",
            "category/tushy/blacked-raw" to "Blacked Raw",
            "category/black-patrol" to "Black Patrol",
            "category/adult-empire/my-pervy-family" to "My Pervy Family",
            "category/nubiles-porn/nfbusty" to "NF Busty",
            "category/adult-time/pure-taboo" to "Pure Taboo",
            "category/18eighteen" to "18eighteen",
            "category/21sextury-network" to "21sextury",
            "category/abuse-me" to "Abuse Me",
            "category/adult-empire" to "Adult Empire",
            "category/allinternal" to "Allinternal",
            "category/amateur-allure" to "Amateur Allure",
            "category/mylf/anal-mom" to "Anal Mom",
            "category/family-therapy/anal-therapy" to "Anal Therapy",
            "category/arabs-exposed" to "Arabs Exposed",
            "category/asstraffic" to "Asstraffic",
            "category/backroom-casting-couch" to "Backroom Casting",
            "category/teamskeet/bffs" to "Bffs",
            "category/black-loads" to "black loads",
            "category/blue-pill-men" to "Blue Pill Men",
            "category/college-rules" to "College Rules",
            "category/busty-buffy/" to "Bust Buffy",
            "category/nubiles-porn/mom-lover" to "Mom Lover",
            "category/nubiles-porn/brattysis" to "Bratty Sis",
            "category/culioneros" to "Culioneros",
            "category/pornpros/cum4k" to "Cum 4k",
            "category/cumlouder" to "Cumlouder",
            "category/teamskeet/dad-crush" to "Dad Crush",
            "category/dancing-bear" to "Dancing Bear",
            "category/dare-dorm" to "Dare Dorm",
            "category/date-slam" to "Date Slam",
            "category/teamskeet/daughter-swap" to "Daughter Swap",
            "category/defloration" to "Defloration",
            "category/deviante" to "Deviante",
            "category/digital-playground" to "Digital Playground",
            "category/dirty-flix" to "Dirty Flix",
            "category/dogfart-network" to "Dogfart Network",
            "category/dont-fuck-my-daughter" to "Dont Fuck My Daughter",
            "category/adult-empire/elegant-angel" to "Elegant Angel",
            "category/exploitedcollegegirls" to "Exploited College Girls",
            "category/fakehub/fake-agent" to "Fake Agent",
            "category/fakehub/fake-agent-uk" to "Fake Agent Uk",
            "category/fakehub/fake-driving-school" to "Fake Driving School",
            "category/fakehub/fake-hospital" to "Fake Hospital",
            "category/fakehub/fake-hostel" to "Fake Hostel",
            "category/family-hookups" to "Family Hookups",
            "category/adult-empire/filthy-kings" to "Filthy Kings",
            "category/first-anal-quest" to "First Anal",
            "category/foster-tapes" to "Foster Tapes",
            "category/teamskeet/freaky-fembots" to "Freaky",
            "category/teamskeet/freeuse-fantasy" to "Freeuse",
            "category/mylf/freeuse-milf" to "Freeuse Milf",
            "category/fullpornnetwork" to "Full Porn Network",
            "category/hotcrazymess" to "Hot Crazy Mess",
                    
   

    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val url:String
        if (page==1)
        {
            url = "$mainUrl/${request.data}"
        }
        else
        {
            url ="$mainUrl/${request.data}/page/$page"
        }
        val document = app.get(url).document
        val home     = document.select("div.videos-list article").mapNotNull {
             it.toSearchResult() }

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
        val title     = fixTitle(this.select("header span").text().substringAfter(" – ").trim())
        val href      = fixUrl(this.select("a").attr("href"))
        val posterUrl = fixUrlNull(this.select("img").attr("data-src"))
        return newMovieSearchResponse(title, href, TvType.Movie) {
            this.posterUrl = posterUrl
        }
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val searchResponse = mutableListOf<SearchResponse>()

        for (i in 1..5) {
            val document = app.get("${mainUrl}/search/$query/page/$i").document

            val results = document.select("#primary article").mapNotNull { it.toSearchResult() }

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

        val title       = document.selectFirst("meta[property=og:title]")?.attr("content")
            ?.substringAfter("–") ?:""
        val poster      = fixUrlNull(document.selectFirst("[property='og:image']")?.attr("content"))
        val description = document.selectFirst("meta[property=og:description]")?.attr("content")?.trim()

        val recommendations = document.select("article").map {
            val rectitle     = fixTitle(it.select("header span").text().substringAfter(" – ").trim())
            val rechref      = fixUrl(it.select("a").attr("href"))
            val recposterUrl = fixUrlNull(it.select("img").attr("data-src"))
            newTvSeriesSearchResponse(rectitle, rechref, TvType.TvSeries) {
                this.posterUrl = recposterUrl
            }
        }
        return newMovieLoadResponse(title, url, TvType.NSFW, url) {
            this.posterUrl = poster
            this.plot      = description
            this.recommendations=recommendations
        }
    }

    override suspend fun loadLinks(data: String, isCasting: Boolean, subtitleCallback: (SubtitleFile) -> Unit, callback: (ExtractorLink) -> Unit): Boolean {
        val document = app.get(data).document
        document.select("div.responsive-player").map { res ->
            val href=res.select("iframe").attr("src")
            loadExtractor(href,subtitleCallback, callback)
        }

        return true
    }
}


class PornobaeExtractor : StreamWishExtractor() {
    override var mainUrl = "https://tubexplayer.com"
 }