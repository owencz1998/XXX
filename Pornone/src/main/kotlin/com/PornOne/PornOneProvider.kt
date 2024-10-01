package com.PornOne

import com.google.gson.Gson
import com.lagradost.api.Log
import org.jsoup.nodes.Element
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.json.JSONObject
import org.jsoup.nodes.Document
import java.lang.System

class PornOneProvider : MainAPI() {
    override var mainUrl              = "https://pornone.com"
    override var name                 = "PornOne"
    override val hasMainPage          = true
    override var lang                 = "en"
    override val hasDownloadSupport   = true
    override val hasChromecastSupport = true
    override val supportedTypes       = setOf(TvType.NSFW)
    override val vpnStatus            = VPNStatus.MightBeNeeded

    override val mainPage = mainPageOf(
            "/" to "Latest Updates",
            "/porn-shorts/" to "Shorts",
            "/teen/" to "Teen",
            "/petite/" to "Petite",
            "/licking-pussy/" to "Licking Pussy",
            "/big-boobs/" to "Big Boobs",
            "/hardcore/" to "Hardcore",
            "/pussy/" to "Pussy",
            "/anal/" to "Anal",
            "/masterbation/" to "Masterbation",
            "/solo/" to "Solo",
            "/stroking/" to "Stroking",
            "/street/" to "Street",
            "/public/" to "public",
            "/nasty/" to "Nasty",
            "/school/" to "School",
            "/teacher/" to "Teacher",
            "/nerd/" to "Nerd",
            "/uniform/" to "Uniform",
            "/pump/" to "Pump",
            "/oil/" to "Oil",
            "/sister/" to "Sister",
            "/squirting/" to "Squirting",
            "/milking/" to "Milking",
            "/jerking/" to "Jerking",
            "/jizz/" to "Jizz",
            "/machine/" to "Machine",
            "/hogtied/" to "Hogtied",
            "/toys/" to "Toys",
            "/vibrator/" to "Vibrator",
            "/slut/" to "Slut",
            "/orgy/" to "Orgy",
            "/pregnant/" to "Pregnant",
            "/pawg/" to "Pawg",
            "/tight/" to "Tight",
            "/throat/" to "Throat",
            "/tease/" to "Tease",
            "/swallowing/" to "Swallowing",
            "/orgasm/" to "Orgasm",
            "/underwater/" to "underwater",
            "/spy/" to "Spy",
            "/hidden/" to "Hidden",
            "/old-man/" to "Old Man",
            "/old-and-young/" to "Old And Young",
            "/asian/" to "Asian",
            "/ass/" to "Ass",
            "/bbw/" to "BBW",
            "/big-dick/" to "Big Dick",
            "/monster/" to "Monster",
            "/blonde/" to "Blonde",
            "/brunette/" to "Brunette",
            "/busty/" to "Busty",
            "/creampie/" to "Creampie",
            "/daughter/" to "Daughter",
            "/double-anal/" to "Double Anal",
            "/foursome/" to "Foursome",
            "/gangbang/" to "Gangbang",
            "/indian/" to "Indian",
            "/lesbian/" to "Lesbian",
            "/milf/" to "Milf",
            "/mom/" to "Mom",
            "/natural-tits/" to "Natural Tits",
            "/stepmom/" to "Stepmom",
            "/threesome/" to "Threesome",
            "/wife/" to "Wife",
        )
    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
            var document = app.get("$mainUrl${request.data}$page", timeout = 30).document
            val responseList  = document.select(".popbop.vidLinkFX").mapNotNull { it.toSearchResult() }
            return newHomePageResponse(HomePageList(request.name, responseList, isHorizontalImages = true),hasNext = true)

    }

    private fun Element.toSearchResult(): SearchResponse {
        val title = this.select(".imgvideo.w-full").attr("alt")
        val href =  this.attr("href")
        var posterUrl = this.select(".imgvideo.w-full").attr("data-src")
        if(posterUrl.isNullOrBlank())
        {
            posterUrl = this.select(".imgvideo.w-full").attr("src")
        }
        return newMovieSearchResponse(title, href, TvType.Movie) {
            this.posterUrl = posterUrl
        }
    }

    override suspend fun search(query: String): List<SearchResponse> {

        val searchResponse = mutableListOf<SearchResponse>()

        for (i in 1..7) {
            var document = app.get("$mainUrl/search?q=$query&page=$i", timeout = 30).document

            //val document = app.get("${mainUrl}/page/$i/?s=$queassry").document

            val results = document.select(".popbop.vidLinkFX").mapNotNull { it.toSearchResult() }

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
        val script = document.select("script[data-react-helmet=\"true\"]").html()
        val jsonObj = JSONObject(script)
        val title = jsonObj.get("name")
        val poster = jsonObj.getJSONArray("thumbnailUrl")[0]
        val description = jsonObj.get("description")
    

        return newMovieLoadResponse(title.toString(), url, TvType.NSFW, url) {
            this.posterUrl = poster.toString()
            this.plot = description.toString()
        }
    }

    override suspend fun loadLinks(data: String, isCasting: Boolean, subtitleCallback: (SubtitleFile) -> Unit, callback: (ExtractorLink) -> Unit): Boolean {
        val doc = app.get(data).document
        val sources = doc.select("#pornone-video-player source")
        sources.forEach { item->
            val src = item.attr("src")
            val quality = item.attr("res")
            callback.invoke(ExtractorLink(name,name,src,"",quality.toInt()))
        }



        return true
    }
}
