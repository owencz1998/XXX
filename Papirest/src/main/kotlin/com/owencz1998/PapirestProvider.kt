package com.owencz1998

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
class PornTubeProvider: Plugin() {
    override fun load(context: Context) {
        registerMainAPI(Porntube())
    }
}