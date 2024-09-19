package com.xxx

import android.content.Context
import com.xxx.Newsensations
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin

@CloudstreamPlugin
class NewsensationsProvider : Plugin() {
    override fun load(context: Context) {
        registerMainAPI(Newsensations())
    }
}