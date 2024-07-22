package utils

import platform.UIKit.UIApplication

actual fun getPlatformContext(): PlatformContext {
    return IosPlatformContext(UIApplication.sharedApplication)
}

class IosPlatformContext(sharedApplication: UIApplication) : PlatformContext()
