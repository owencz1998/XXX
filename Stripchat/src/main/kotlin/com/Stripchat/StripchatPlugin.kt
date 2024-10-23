package com.Stripchat

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
class StripchatPlugin: Plugin() {
    override fun load(context: Context) {
        registerMainAPI(StripchatProvider())
    }
}