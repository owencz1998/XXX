package com.Onlyjerk

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
class OnlyjerkProvider: Plugin() {
    override fun load(context: Context) {
        registerMainAPI(Onlyjerk())
        registerExtractorAPI(Dooodster())
        registerExtractorAPI(Bigwarp())
        registerExtractorAPI(Listeamed())
        registerExtractorAPI(Beamed())
        registerExtractorAPI(Bgwp())
    }
}