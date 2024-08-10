package com.owencz1998

import android.content.Context
import com.owencz1998.Hqporner
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin

@CloudstreamPlugin
class HqpornerProvider : Plugin() {
    override fun load(context: Context) {
        registerMainAPI(Hqporner())
    }
}