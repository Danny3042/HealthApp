import platform.UIKit.UIApplication
import platform.darwin.sel_registerName


@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

actual fun hideKeyboard() {
    UIApplication.sharedApplication.sendAction(
        sel_registerName("resignFirstResponder"),
        to = null,
        from = null,
        forEvent = null
    )
}