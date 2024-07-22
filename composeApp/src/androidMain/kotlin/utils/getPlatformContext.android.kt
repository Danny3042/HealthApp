package utils

import android.content.Context

lateinit var appContext: Context
actual fun getPlatformContext(): PlatformContext = appContext