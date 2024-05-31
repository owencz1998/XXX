package com.owencz1998

import com.fasterxml.jackson.annotation.JsonProperty
import com.lagradost.api.Log
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.extractors.DoodLaExtractor
import com.lagradost.cloudstream3.extractors.Filesim
import com.lagradost.cloudstream3.utils.AppUtils.tryParseJson
import com.lagradost.cloudstream3.utils.ExtractorApi
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.INFER_TYPE
import com.lagradost.cloudstream3.utils.M3u8Helper
import com.lagradost.cloudstream3.utils.Qualities
import com.lagradost.cloudstream3.utils.getQualityFromName

class XvideosProvider : MainAPI() {
    private val globalTvType = TvType.NSFW
    private val Dev = "DevDebug"

    override var mainUrl = "https://www.xvideos.com"
    override var name = "Xvideos"
    override val hasMainPage = true
    override val hasChromecastSupport = true
    override val hasDownloadSupport = true
    override val supportedTypes = setOf(TvType.NSFW)

    fun getLinkAndExt(text: String) : Pair<String, String> {
        val validlink = text.trim().trim('"').trim('\'')
        val valindlinkext = validlink.substringAfterLast(".")
            .substringBeforeLast("?").trim().uppercase()
        return Pair(validlink, valindlinkext)
    }

    override val mainPage = mainPageOf(
        Pair(mainUrl, "Main Page"),
        Pair("$mainUrl/new/", "New"),
        Pair("$mainUrl/c/squirting-56/",
"💦 Squirt"),
        Pair("$mainUrl/c/amateur-65/",
"👧 Amateur"),
        Pair("$mainUrl/c/teen-13/", 
"👱‍♀️ Teen"),
        Pair("$mainUrl/?k=handjob&top/",
"✊️ Handjob"),   
        Pair("$mainUrl/c/big_Tits-23/",
"🍈🍈 Big tits"),
        Pair("$mainUrl/c/lesbian-26/", 
"🫦 Lesbian"),
        Pair("$mainUrl/c/anal-12/",
"🙇‍♀️ Anal"),
        Pair("$mainUrl/c/blowjob-15/",
"💨 Blowjob"),
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
"Family"),
        Pair("$mainUrl/c/oiled-22/",
"Oiled"),
        Pair("$mainUrl/shemale?fmc=1/",
"Shemale"),
        Pair("$mainUrl/?k=british&top",
"British"),
        Pair("$mainUrl/c/Cam_Porn-58/",
"Cam"),
        Pair("$mainUrl/c/Gapes-167",
"Gape"),
        Pair("$mainUrl/?k=caught&top/",
"Caught"),
  Pair("$mainUrl/c/bbw-51/",
"BBW"),
        Pair("$mainUrl/?k=wife&top/",
"Wife"),
        Pair("$mainUrl/c/redhead-31/",
"Redhead"),
        Pair("$mainUrl/?k=rough&top/",
"Rough"),
        Pair("$mainUrl/c/stocking-28/",
"Stockings"),
        Pair("$mainUrl/?k=teacher&top/",
"Teacher"),
        Pair("$mainUrl/tags/anal-masturbation/",
"Anal Masterbation"),
        Pair("$mainUrl/tags/dildos/",
"Dildo"),
        Pair("$mainUrl/tags/fingering/",
"Fingering"),
       Pair("$mainUrl/tags/gay/",
"Gay"),
        Pair("$mainUrl/tags/ftvgirls/",
"Ftv"),
       Pair("$mainUrl/?k=vintage&top/",
"Vintage"),
        Pair("$mainUrl/?k=real&top/",
"Real"),   
        Pair("$mainUrl/?k=joi&top/",
"Joi"),
       Pair("$mainUrl/c/interracial-27/",
"Interracial"),
       Pair("$mainUrl/?k=hardcore&top/",
"Hardcore"),
       Pair("$mainUrl/pornstars/sweetie-fox1",
"Sweetie Fox"),
       Pair("$mainUrl/pornstars/henessy51",
"Henessy"),
       Pair("$mainUrl/pornstars/katerina-hartlova1",
"Katerina Hartlova"),
       Pair("$mainUrl/pornstars/luna-rival1",
"Luna Rival"),
      Pair("$mainUrl/pornstars/sexy-susi1",
"Sexy Susi"),
      Pair("$mainUrl/pornstars/mary-wet",
"Mary Wet"),
     )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val categoryData = request.data
        val categoryName = request.name
        val isPaged = categoryData.endsWith('/')
        val pagedLink = if (isPaged) categoryData + page else categoryData
        try {
            if (!isPaged && page < 2 || isPaged) {
                val soup = app.get(pagedLink).document
                val home = soup.select("div.thumb-block").mapNotNull {
                    if (it == null) { return@mapNotNull null }
                    val title = it.selectFirst("p.title a")?.text() ?: ""
                    val link = fixUrlNull(it.selectFirst("div.thumb a")?.attr("href")) ?: return@mapNotNull null
                    val image = it.selectFirst("div.thumb a img")?.attr("data-src")
                    newMovieSearchResponse(
                        name = title,
                        url = link,
                        type = globalTvType,
                    ) {
                        this.posterUrl = image
                    }
                }
                if (home.isNotEmpty()) {
                    return newHomePageResponse(
                        list = HomePageList(
                            name = categoryName,
                            list = home,
                            isHorizontalImages = true
                        ),
                        hasNext = true
                    )
                } else {
                    throw ErrorLoadingException("No homepage data found!")
                }
            }
        } catch (e: Exception) {
            //e.printStackTrace()
            logError(e)
        }
        throw ErrorLoadingException()
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val url = "$mainUrl?k=${query}"
        val document = app.get(url).document
        return document.select("div.thumb-block").mapNotNull {
            val title = it.selectFirst("p.title a")?.text()
                ?: it.selectFirst("p.profile-name a")?.text()
                ?: ""
            val href = fixUrlNull(it.selectFirst("div.thumb a")?.attr("href")) ?: return@mapNotNull null
            val image = if (href.contains("channels") || href.contains("pornstars")) null else it.selectFirst("div.thumb-inside a img")?.attr("data-src")
            val finaltitle = if (href.contains("channels") || href.contains("pornstars")) "" else title
            newMovieSearchResponse(
                name = finaltitle,
                url = href,
                type = globalTvType,
            ) {
                this.posterUrl = image
            }

        }.toList()
    }
    override suspend fun load(url: String): LoadResponse {
        val soup = app.get(url).document
        val title = if (url.contains("channels")||url.contains("pornstars")) soup.selectFirst("html.xv-responsive.is-desktop head title")?.text() else
            soup.selectFirst(".page-title")?.text()
        val poster: String? = if (url.contains("channels") || url.contains("pornstars")) soup.selectFirst(".profile-pic img")?.attr("data-src") else
            soup.selectFirst("head meta[property=og:image]")?.attr("content")
        val tags = soup.select(".video-tags-list li a")
            .map { it?.text()?.trim().toString().replace(", ","") }
        val episodes = soup.select("div.thumb-block").mapNotNull {
            val href = it?.selectFirst("a")?.attr("href") ?: return@mapNotNull null
            val name = it.selectFirst("p.title a")?.text() ?: ""
            val epthumb = it.selectFirst("div.thumb a img")?.attr("data-src")
            Episode(
                name = name,
                data = href,
                posterUrl = epthumb,
            )
        }
        val tvType = if (url.contains("channels") || url.contains("pornstars")) TvType.TvSeries else globalTvType
        return when (tvType) {
            TvType.TvSeries -> {
                TvSeriesLoadResponse(
                    name = title ?: "",
                    url = url,
                    apiName = this.name,
                    type = globalTvType,
                    episodes = episodes,
                    posterUrl = poster,
                    plot = title,
                    showStatus = ShowStatus.Ongoing,
                    tags = tags,
                )
            }
            else -> {
                newMovieLoadResponse(
                    name = title ?: "",
                    url = url,
                    type = globalTvType,
                    dataUrl = url,
                ) {
                    this.posterUrl = poster
                    this.plot = title
                    this.tags = tags
                    this.duration = getDurationFromString(title)
                }
            }
        }
    }
    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        app.get(data).document.select("script").apmap { script ->
            val scriptdata = script.data()
            if (scriptdata.isNullOrBlank()) {
                return@apmap
            }
            //Log.i(Dev, "scriptdata => $scriptdata")
            if (scriptdata.contains("HTML5Player")) {
                val extractedlink = script.data().substringAfter(".setVideoHLS('")
                    .substringBefore("');")
                if (extractedlink.isNotBlank()) {
                    M3u8Helper().m3u8Generation(
                        M3u8Helper.M3u8Stream(
                            extractedlink,
                            headers = app.get(data).headers.toMap()
                        ), true
                    ).map { stream ->
                        callback(
                            ExtractorLink(
                                source = this.name,
                                name = "${this.name} m3u8",
                                url = stream.streamUrl,
                                referer = data,
                                quality = getQualityFromName(stream.quality?.toString()),
                                isM3u8 = true
                            )
                        )
                    }
                }
                val mp4linkhigh = script.data().substringAfter("html5player.setVideoUrlHigh('").substringBefore("');")
                if (mp4linkhigh.isNotBlank()) {
                    callback(
                        ExtractorLink(
                            source = this.name,
                            name = "${this.name} MP4 High",
                            url = mp4linkhigh,
                            referer = data,
                            quality = Qualities.Unknown.value,
                        )
                    )
                }
                val mp4linklow = script.data().substringAfter("html5player.setVideoUrlLow('").substringBefore("');")
                if (mp4linklow.isNotBlank()) {
                    callback(
                        ExtractorLink(
                            source = this.name,
                            name = "${this.name} MP4 Low",
                            url = mp4linklow,
                            referer = data,
                            quality = Qualities.Unknown.value,
                        )
                    )
                }
            }

            val setOfRegexOption = setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)
            //Fetch default links
            if (scriptdata.contains("contentUrl")) {
                Log.i(Dev, "Fetching default link..")

                "(?<=contentUrl\\\":)(.*)(?=\\\",)".toRegex(setOfRegexOption)
                .findAll(scriptdata).forEach {
                    it.groupValues.forEach { link ->
                        val validLinkVal = getLinkAndExt(link)
                        val validlink = validLinkVal.first
                        val validlinkext = validLinkVal.second
                        Log.i(Dev, "Result Default => $validlink")
                        callback(
                            ExtractorLink(
                                source = this.name,
                                name = "${this.name} $validlinkext",
                                url = validlink,
                                referer = data,
                                quality = Qualities.Unknown.value,
                                isM3u8 = validlinkext.startsWith("M3")
                            )
                        )
                    }
                }
            }
            //Fetch HLS links
            Log.i(Dev, "Fetching HLS Low link..")
            Regex("(?<=setVideoUrlLow\\()(.*?)(?=\\);)", setOfRegexOption).findAll(scriptdata)
                .forEach {
                it.groupValues.forEach { link ->
                    val validLinkVal = getLinkAndExt(link)
                    val validlink = validLinkVal.first
                    val validlinkext = validLinkVal.second
                    Log.i(Dev, "Result HLS Low => $validlink")
                    callback(
                        ExtractorLink(
                            source = this.name,
                            name = "${this.name} $validlinkext Low",
                            url = validlink,
                            referer = data,
                            quality = Qualities.Unknown.value
                        )
                    )
                }
            }

            Log.i(Dev, "Fetching HLS High link..")
            Regex("(?<=setVideoUrlHigh\\()(.*?)(?=\\);)", setOfRegexOption).findAll(scriptdata)
                .forEach {
                it.groupValues.forEach { link ->
                    val validLinkVal = getLinkAndExt(link)
                    val validlink = validLinkVal.first
                    val validlinkext = validLinkVal.second
                    Log.i(Dev, "Result HLS High => $validlink")
                    callback(
                        ExtractorLink(
                            source = this.name,
                            name = "${this.name} $validlinkext High",
                            url = validlink,
                            referer = data,
                            quality = Qualities.Unknown.value
                        )
                    )
                }
            }

            Log.i(Dev, "Fetching HLS Default link..")
            Regex("(?<=setVideoHLS\\()(.*?)(?=\\);)", setOfRegexOption).findAll(scriptdata)
                .forEach {
                it.groupValues.forEach { link ->
                    val validLinkVal = getLinkAndExt(link)
                    val validlink = validLinkVal.first
                    val validlinkext = validLinkVal.second
                    Log.i(Dev, "Result HLS Default => $validlink")
                    callback(
                        ExtractorLink(
                            source = this.name,
                            name = "${this.name} $validlinkext Default",
                            url = validlink,
                            referer = data,
                            quality = Qualities.Unknown.value,
                            isM3u8 = true
                        )
                    )
                }
            }
        }
        return true
    }
}