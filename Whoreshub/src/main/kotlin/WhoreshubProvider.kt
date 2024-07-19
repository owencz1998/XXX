package com.owencz1998

import android.content.Context
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin

@CloudstreamPlugin
class WhoreshubProvider : Plugin() {
    override fun load(context: Context) {
        registerMainAPI(Whoreshub())
    }
}
