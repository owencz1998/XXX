package owencz1998

import android.content.Context
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
// This import might be necessary if registerMainAPI is an extension function
// import com.lagradost.cloudstream3.plugins.PluginManager.registerMainAPI

@CloudstreamPlugin // Marks this as a plugin
class XhamsterPlugin : Plugin() { // Inherits from Plugin
    override fun load(context: Context) {
        // All providers should be added in this way.
        // Register XhamsterProvider
        registerMainAPI(XhamsterProvider()) // Calls the provider registration here
    }
}