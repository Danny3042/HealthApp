import platform.UIKit.UIApplication
import platform.UIKit.UITextField
import platform.UIKit.UITextFieldDelegateProtocol
import platform.darwin.NSObject
import platform.darwin.sel_registerName


@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

actual fun hideKeyboard() {
    UIApplication.sharedApplication.sendAction(
        sel_registerName("resignFirstResponder"),
        to = null,
        from = null,
        forEvent = null
    )

    val delegate = object : NSObject(), UITextFieldDelegateProtocol {
        override fun textFieldShouldReturn(textField: UITextField): Boolean {
            hideKeyboard()
            return true
        }

    }
}