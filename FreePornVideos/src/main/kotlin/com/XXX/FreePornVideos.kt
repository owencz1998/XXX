package com.XXX

//import android.util.Log
import org.jsoup.nodes.Element
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import com.lagradost.cloudstream3.LoadResponse.Companion.addActors

class FreePornVideos : MainAPI() {
    override var mainUrl              = "https://www.freepornvideos.xxx"
    override var name                 = "Free Porn Videos"
    override val hasMainPage          = true
    override var lang                 = "en"
    override val hasQuickSearch       = false
    override val supportedTypes       = setOf(TvType.NSFW)
    override val vpnStatus            = VPNStatus.MightBeNeeded

    override val mainPage = mainPageOf(
        "most-popular/week" to "Most Popular",
        "networks/brazzers-com" to "Brazzers",
        "networks/mylf-com" to "MYLF",
        "networks/brazzers-com" to "Brazzers",
        "sites/evil-angel" to "Evil Angel",
        "networks/bangbros" to "BangBros",
        "networks/adult-time" to "Adult Time",
        "networks/rk-com" to "Reality Kings",
        "networks/naughtyamerica-com" to "Naughty America",
        "sites/bang-bus" to "Bang Bus",
        "sites/blacked" to "Blacked",
        "networks/teamskeet-com" to "Team Skeet",
        "sites/teamskeetallstars" to "Team Skeet All Stars",
        "sites/teamskeetlabs" to "Team Skeet Labs",
        "networks/mofos-com" to "Mofos",
        "sites/fake-taxi" to "Fake Taxi",
        "sites/female-fake-taxi" to "Female Fake Taxi",
        "sites/fakehub-originals" to "FakeHub Originals",
        "sites/devils-film" to "Devils Films",
        "sites/family-strokes" to "Family Strokes",
        "sites/this-girl-sucks" to "This Girl Sucks",
        "sites/teens-love-huge-cocks" to "Teens Love huge Cocks",
        "sites/cum-fiesta" to "Cum Fiesta",
        "sites/deeper" to "Deeper",
        "sites/ass-parade" to "Ass Parade",
        "sites/ass-masterpiece" to "Ass Masterpiece",
        "sites/got-mylf" to "Got Mylf",
        "networks/mom-lover" to "Mom Lover",
        "sites/hotguysfuck" to "Hot Guys Fuck",
        "sites/caught-fapping" to "Caught Fapping",
        "sites/public-bang" to "Public Bang",
        "sites/public-agent" to "Mom Lover",
        "sites/got-mylf" to "Got Mylf",
        "sites/dare-dorm" to "Dare Dorm",
        "sites/exxxtra-small" to "Exxxtra Small",
        "sites/princesscum.com" to "Princess Cum",
        "categories/jav-uncensored" to "Jav",
        "sites/monsters-of-cock" to "Monsters Of Cock",
        "networks/nubiles-porn-com" to "Nubiles",
        "sites/mature-nl" to "Mature",
        "sites/sexart" to "Sex Art",
        "sites/fake-hostel" to "Fake Hostel", 
        "sites/rk-prime" to "Rk Prime",
        "sites/dfxtra-originals" to "Dfxtra Orginals",
        "sites/dad-crush" to "Dad Crush",
        "sites/titty-attack" to "Titty Attack",
        "sites/big-tits-round-asses" to "Big Tits Round Asses",
        "sites/the-real-workout" to "The Real Work Out",
        "sites/fitness-rooms" to "Fitness Rooms",
        "sites/sexmex" to "Sexmex",
        "sites/netvideogirls" to "Net Video Girls",
        "sites/dont-break-me" to "Dont Break Me",
        "sites/we-live-together" to "We Live Together",
        "sites/bffs" to "BFFS",
        "sites/neighbor-affair" to "Neighbor Affair",
        "sites/i-know-that-girl" to "I Know That Girl",
        "sites/perfect-fucking-strangers" to "Perfect Fucking Strangers",
        "sites/my-babysitters-club" to "My Babysitters Club",
        "sites/tushy" to "Tusy",
        "sites/tushyraw" to "TushyRaw",
        "sites/blacked-raw" to "BlackedRaw",
        "sites/brown-bunnies" to "Brown Bunnies",
        "sites/bbcparadise" to "BBC Paradise",
        "sites/device-bondage" to "Device Bondage",
        "sites/kink-classics" to "Kink Classics",
        "sites/pure-taboo" to "Pure Taboo",
        "sites/pervnana" to "Perv Nana",
        "sites/my-pervy-family" to "My Pervy Family",
        "sites/my-family-pies" to "My Family Pies",
        "sites/sis-loves-me" to "Sis Loves Me",
        "sites/step-siblings-caught" to "Step Siblings Caught",
        "sites/perv-mom" to "Perv Mom",
        "sites/analmom2" to "Anal Mom",
        "sites/momwantscreampie" to "Mom Wants Creampie",
        "sites/my-friends-hot-mom" to "My Friends Hot Mom",
        "sites/momwantstobreed" to "Mom Wants To Breed",
        "sites/mylf-classics" to "Mylf Classics",
        "sites/bad-milfs" to "Bad Milfs",
        "networks/mom-lover" to "Mom Lover",
        "sites/got-mylf" to "Got Mylf", 
        "sites/21-sextury" to "21 Sextury",
        "sites/momswap" to "Mom Swap",
        "sites/daughter-swap" to "Daughter Swap",
        "networks/mom-lover" to "Mom Lover",
        "sites/brat-tamer" to "Brat Tamer",
        "sites/wake-up-n-fuck2" to "Wakeup N Fuck",
        "sites/girlsway" to "GirlsWay",
        "sites/lesbea" to "Lesbea",
        "sites/shoplyfter" to "ShopLyfter",
        "sites/american-daydreams" to "American Daydreams",
        "sites/my-first-sex-teacher" to "My First Sex Teacher",
        "sites/private" to "Private",
        "sites/moderndaysins" to "Mondern Day Sins",
        "sites/use-pov" to "Use Pov",
        "sites/thundercock" to "Thunder Cock",
        "sites/cuckold-sessions" to "Cuckold Sessions",
        "sites/dane-jones" to "Dane Jones",
        "sites/vixen" to "Vixen",
        "sites/trans-angels" to "Trans Angels",
        "sites/transfixed" to "TransFixed"
        



    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val document = app.get("$mainUrl/${request.data}/${page+1}/").document
        val home     = document.select("#list_videos_common_videos_list_items > div.item").mapNotNull {
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
        val title      = this.select("strong.title").text()
        val href       = this.selectFirst("a")!!.attr("href")
        val posterUrl  = this.select("a img").attr("data-src")
        return newMovieSearchResponse(title, href, TvType.Movie) {
            this.posterUrl = posterUrl
        }

    }

    override suspend fun search(query: String): List<SearchResponse> {
        val searchResponse = mutableListOf<SearchResponse>()

        for (i in 1..5) {
            val document = app.get(
                "${mainUrl}/search/$query/")
            .document
            val results = document.select("#custom_list_videos_videos_list_search_result_items > div.item").mapNotNull { it.toSearchResult() }
            searchResponse.addAll(results)

            if (results.isEmpty()) break
        }

        return searchResponse
    }

    override suspend fun load(url: String): LoadResponse {
        val document = app.get(url).document

        val full_title      = document.selectFirst("div.headline > h1")?.text()?.trim().toString()
        val last_index      = full_title.lastIndexOf(" - ")
        val raw_title       = if (last_index != -1) full_title.substring(0, last_index) else full_title
        val title           = raw_title.removePrefix("- ").trim().removeSuffix("-").trim()

        val poster          = fixUrlNull(document.selectFirst("[property='og:image']")?.attr("content"))
        val tags            = document.selectXpath("//div[contains(text(), 'Categories:')]/a").map { it.text() }
        val description     = document.selectXpath("//div[contains(text(), 'Description:')]/em").text().trim()
        val actors          = document.selectXpath("//div[contains(text(), 'Models:')]/a").map { it.text() }
        val recommendations = document.select("div#list_videos_related_videos_items div.item").mapNotNull { it.toSearchResult() }

        val year            = full_title.substring(full_title.length - 4).toIntOrNull()
        val rating          = document.selectFirst("div.rating span")?.text()?.substringBefore("%")?.trim()?.toFloatOrNull()?.div(10)?.toString()?.toRatingInt()

        val raw_duration    = document.selectXpath("//span[contains(text(), 'Duration')]/em").text().trim()
        val duration_parts  = raw_duration.split(":")
        val duration        = when (duration_parts.size) {
            3 -> {
                val hours   = duration_parts[0].toIntOrNull() ?: 0
                val minutes = duration_parts[1].toIntOrNull() ?: 0

                hours * 60 + minutes
            }
            else -> {
                duration_parts[0].toIntOrNull() ?: 0
            }
        }

        return newMovieLoadResponse(title.removePrefix("- ").removeSuffix("-").trim(), url, TvType.NSFW, url) {
            this.posterUrl       = poster
            this.year            = year
            this.plot            = description
            this.tags            = tags
            this.recommendations = recommendations
            this.rating          = rating
            this.duration        = duration
            addActors(actors)
        }
    }

    override suspend fun loadLinks(data: String, isCasting: Boolean, subtitleCallback: (SubtitleFile) -> Unit, callback: (ExtractorLink) -> Unit): Boolean {
        val document = app.get(data).document
        document.select("video source").map { res ->
            callback.invoke(
                ExtractorLink(
                    "FPV",
                    "FPV",
                    res.attr("src"),
                    referer = data,
                    quality = getQualityFromName(res.attr("label")),
                )
            )
        }

        return true
    }
}
