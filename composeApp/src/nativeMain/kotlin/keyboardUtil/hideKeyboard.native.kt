package keyboardUtil

import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIApplication
import platform.UIKit.UIBarButtonItem
import platform.UIKit.UIBarButtonItemStyle
import platform.UIKit.UITextField
import platform.UIKit.UIToolbar
import platform.darwin.sel_registerName

@OptIn(ExperimentalForeignApi::class)
actual fun hideKeyboard() {
    UIApplication.sharedApplication.sendAction(
        sel_registerName("resignFirstResponder"),
        to = null,
        from = null,
        forEvent = null
    )
}

@OptIn(ExperimentalForeignApi::class)
fun createToolbarWithDoneButton(): UIToolbar {
    val toolbar = UIToolbar()
    toolbar.sizeToFit()

    val doneButton = UIBarButtonItem(
        title = "Done",
        style = UIBarButtonItemStyle.UIBarButtonItemStyleDone,
        target = null,
        action = sel_registerName("resignFirstResponder")
    )

    toolbar.setItems(listOf(doneButton), animated = false)
    return toolbar
}

class IOSCustomTextField(private val textField: UITextField) : CustomTextField {
    override fun becomeFirstResponder() {
        textField.becomeFirstResponder()
    }
}

actual fun createCustomTextField(): CustomTextField {
    val textField = UITextField()
    textField.inputAccessoryView = createToolbarWithDoneButton()
    return IOSCustomTextField(textField)
}