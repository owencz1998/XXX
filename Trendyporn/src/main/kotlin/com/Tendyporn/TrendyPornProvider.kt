package com.Trendyporn

import android.content.Context
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin

@CloudstreamPlugin
class TrendyPornProvider : Plugin() {
    override fun load(context: Context) {
        registerMainAPI(TrendyPorn())
    }
}
