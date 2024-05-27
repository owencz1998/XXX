package com.owencz1998

import android.content.Context
import com.owencz1998.FullPorner
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin

@CloudstreamPlugin
class FullPornerProvider : Plugin() {
    override fun load(context: Context) {
        registerMainAPI(FullPorner())
    }
}