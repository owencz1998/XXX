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
        "${mainUrl}/?mode=async&function=get_block&block_id=list_videos_most_recent_videos&sort_by=post_date&from="                            to "New Videos",
        "${mainUrl}/?mode=async&function=get_block&block_id=list_videos_most_recent_videos&sort_by=video_viewed&from="                         to "Most Viewed Videos",
        "${mainUrl}/?mode=async&function=get_block&block_id=list_videos_most_recent_videos&sort_by=rating&from="                               to "Top Rated Videos",
        "${mainUrl}/?mode=async&function=get_block&block_id=list_videos_most_recent_videos&sort_by=most_commented&from="                       to "Most Commented Videos",
        "${mainUrl}/?mode=async&function=get_block&block_id=list_videos_most_recent_videos&sort_by=duration&from="                             to "Longest Videos",
        "${mainUrl}/channels/brazzers/?mode=async&function=get_block&block_id=list_videos_common_videos_list&sort_by=post_date&from="          to "Brazzers",
        "${mainUrl}/channels/digitalplayground/?mode=async&function=get_block&block_id=list_videos_common_videos_list&sort_by=post_date&from=" to "Digital Playground",
        "${mainUrl}/channels/realitykings/?mode=async&function=get_block&block_id=list_videos_common_videos_list&sort_by=post_date&from="      to "Realitykings",
        "${mainUrl}/channels/babes-network/?mode=async&function=get_block&block_id=list_videos_common_videos_list&sort_by=post_date&from="     to "Babes Network",
        "${mainUrl}/categories/amateur/?mode=async&function=get_block&block_id=list_videos_common_videos_list&sort_by=post_date&from="         to "Amateur"
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