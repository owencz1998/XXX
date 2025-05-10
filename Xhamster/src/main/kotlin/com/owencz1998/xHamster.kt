package owencz1998

import android.util.Log
import com.fasterxml.jackson.annotation.JsonProperty
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

// === DATA CLASSES FOR JSON PARSING ===
data class SearchResult(
    @JsonProperty("videoThumbProps") val videoThumbProps: List<VideoThumbProps>? = null
)

data class InitialsJson(
    val xplayerSettings: XPlayerSettings? = null,
    val videoEntity: VideoEntity? = null,
    val videoTagsComponent: VideoTagsComponent? = null,
    val relatedVideos: RelatedVideos? = null,
    val layoutPage: LayoutPage? = null,
    val searchResult: SearchResult? = null
)

data class XPlayerSettings(
    val sources: VideoSources? = null,
    val poster: Poster? = null,
    val subtitles: Subtitles? = null
)

data class VideoSources(
    val hls: HlsSources? = null,
    val standard: StandardSources? = null
)

data class HlsSources(val h264: HlsSource? = null)
data class StandardSources(val h264: List<StandardSourceQuality>? = null)
data class HlsSource(val url: String? = null)
data class StandardSourceQuality(val quality: String? = null, val url: String? = null)
data class Poster(val url: String? = null)

data class Subtitles(val tracks: List<SubtitleTrack>? = null)
data class SubtitleTrack(val label: String? = null, val lang: String? = null, val urls: SubtitleUrls? = null)
data class SubtitleUrls(val vtt: String? = null)

data class VideoEntity(val title: String? = null, val description: String? = null, val thumbBig: String? = null)
data class VideoTagsComponent(val tags: List<Tag>? = null)
data class Tag(val name: String? = null, val url: String? = null)

data class RelatedVideos(val videoTabInitialData: VideoTabInitialData? = null)
data class VideoTabInitialData(val videoListProps: VideoListProps? = null)
data class LayoutPage(@JsonProperty("videoListProps") val videoListProps: VideoListProps? = null)
data class VideoListProps(val videoThumbProps: List<VideoThumbProps>? = null)
data class VideoThumbProps(val title: String?, val pageURL: String?, @JsonProperty("thumbURL") val thumbUrl: String?)
// === END OF DATA CLASSES ===

class XhamsterProvider : MainAPI() {
    override var mainUrl = "https://vi.xhspot.com"
    override var name = "Xhamster"
    override val hasMainPage = true
    override var lang = "en"
    override val supportedTypes = setOf(TvType.Movie)

    private fun getInitialsJson(html: String): InitialsJson? {
        return try {
            val script = Jsoup.parse(html).selectFirst("script#initials-script")?.html() ?: return null
            val jsonString = script.removePrefix("window.initials=").removeSuffix(";")
            AppUtils.parseJson<InitialsJson>(jsonString)
        } catch (e: Exception) {
            Log.e(name, "getInitialsJson failed: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    private fun parseVideoItem(element: Element): SearchResponse? {
        val titleElement = element.selectFirst("a.mobile-video-thumb__name")
        val imageElement = element.selectFirst("a.thumb-image-container img")

        val title = titleElement?.text() ?: imageElement?.attr("alt") ?: return null
        val href = titleElement?.attr("href") ?: element.selectFirst("a.thumb-image-container")?.attr("href") ?: return null
        val fixedHref = fixUrl(href)

        var posterUrl = imageElement?.attr("srcset") ?: imageElement?.attr("src")
        val fixedPoster = posterUrl?.trim()?.let { fixUrl(it) }

        return newMovieSearchResponse(title, fixedHref, TvType.NSFW) {
            this.posterUrl = fixedPoster
        }
    }

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse? {
        if (page > 1) return null
        val document = try { app.get("$mainUrl/").document } catch (e: Exception) { return null }
        val initialData = getInitialsJson(document.html())

        var items = initialData?.layoutPage?.videoListProps?.videoThumbProps?.mapNotNull {
            val title = it.title ?: return@mapNotNull null
            val href = fixUrlNull(it.pageURL) ?: return@mapNotNull null
            val poster = fixUrlNull(it.thumbUrl)
            newMovieSearchResponse(title, href, TvType.NSFW) {
                this.posterUrl = poster
            }
        }

        if (items.isNullOrEmpty()) {
            items = document.select("div.mobile-video-thumb").mapNotNull { parseVideoItem(it) }
        }

        return if (!items.isNullOrEmpty()) {
            newHomePageResponse("Video Trang Chủ", items)
        } else null
    }

    override suspend fun search(query: String): List<SearchResponse>? {
        val searchUrl = "$mainUrl/search/$query"
        val document = try { app.get(searchUrl).document } catch (e: Exception) { return null }
        val initialData = getInitialsJson(document.html())

        var results = initialData?.searchResult?.videoThumbProps?.mapNotNull {
            val title = it.title ?: return@mapNotNull null
            val href = fixUrlNull(it.pageURL) ?: return@mapNotNull null
            val poster = fixUrlNull(it.thumbUrl)
            newMovieSearchResponse(title, href, TvType.NSFW) {
                this.posterUrl = poster
            }
        }

        if (results.isNullOrEmpty()) {
            results = document.select("div.mobile-video-thumb").mapNotNull { parseVideoItem(it) }
        }

        return results
    }

    override suspend fun load(url: String): LoadResponse? {
        val document = try { app.get(url).document } catch (e: Exception) { return null }
        val htmlContent = document.html()
        val initialData = getInitialsJson(htmlContent)

        val title = initialData?.videoEntity?.title
            ?: document.selectFirst("meta[property=og:title]")?.attr("content")
            ?: document.selectFirst("title")?.text()?.substringBefore("|")?.trim()
            ?: return null

        val plot = initialData?.videoEntity?.description
            ?: document.selectFirst("div.video-description p[itemprop=description]")?.text()?.trim()

        val poster = initialData?.xplayerSettings?.poster?.url
            ?: initialData?.videoEntity?.thumbBig
            ?: document.selectFirst("meta[property=og:image]")?.attr("content")
        val fixedPoster = fixUrlNull(poster)

        val tags = initialData?.videoTagsComponent?.tags?.mapNotNull { it.name }
            ?: document.select("section[data-role=video-tags] a.item").mapNotNull { it.text() }

        val recommendations = document.select("div[data-role=video-related] div.mobile-video-thumb").mapNotNull {
            parseVideoItem(it)
        }

        return newMovieLoadResponse(title, url, TvType.NSFW, url) {
            this.plot = plot
            this.posterUrl = fixedPoster
            this.tags = tags
            this.recommendations = recommendations
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val document = try { app.get(data).document } catch (e: Exception) { return false }
        val initialData = getInitialsJson(document.html()) ?: return false

        var foundLinks = false
        val sources = initialData.xplayerSettings?.sources
        val sourceName = this.name

        sources?.hls?.h264?.url?.let { m3u8Url ->
            val fixedM3u8Url = fixUrl(m3u8Url)
            try {
                M3u8Helper.generateM3u8(sourceName, fixedM3u8Url, data).forEach {
                    callback(it)
                    foundLinks = true
                }
            } catch (e: Exception) {
                callback(newExtractorLink(sourceName, "$sourceName HLS", fixedM3u8Url) {
                    this.referer = data
                    this.quality = Qualities.Unknown.value
                    this.type = ExtractorLinkType.M3U8
                })
                foundLinks = true
            }
        }

        sources?.standard?.h264?.forEach { qualitySource ->
            val quality = qualitySource.quality?.replace("p", "")?.toIntOrNull() ?: Qualities.Unknown.value
            val videoUrl = fixUrl(qualitySource.url ?: return@forEach)
            callback(newExtractorLink(sourceName, "$sourceName MP4 ${qualitySource.quality}", videoUrl) {
                this.referer = data
                this.quality = quality
                this.type = ExtractorLinkType.VIDEO
            })
            foundLinks = true
        }

        initialData.xplayerSettings?.subtitles?.tracks?.forEach { track ->
            val subUrl = track.urls?.vtt?.let { fixUrl(it) } ?: return@forEach
            val lang = track.lang ?: track.label ?: "Unknown"
            subtitleCallback(SubtitleFile(lang, subUrl))
        }

        return foundLinks
    }
}