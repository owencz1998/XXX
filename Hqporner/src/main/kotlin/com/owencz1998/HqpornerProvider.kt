package com.xxx

import android.content.Context
import com.xxx.newsensation
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin

@CloudstreamPlugin
class NewsensationProvider : Plugin() {
    override fun load(context: Context) {
        registerMainAPI(Hqporner())
    }
}