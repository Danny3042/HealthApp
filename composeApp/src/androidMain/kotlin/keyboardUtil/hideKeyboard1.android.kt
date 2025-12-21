package keyboardUtil

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import java.lang.ref.WeakReference

actual fun hideKeyboard() {
    val activity: Activity? = CurrentActivityHolder.currentActivity
    if (activity == null) return
    val view = activity.currentFocus ?: activity.window?.decorView
    // clear focus so Compose text fields also release focus
    view?.clearFocus()
    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(view?.windowToken, 0)
}

actual fun createCustomTextField(): CustomTextField {
    // provide a no-op fallback for Android if needed
    return object : CustomTextField {
        override fun becomeFirstResponder() {
            // no-op
            return
        }
    }
}

object CurrentActivityHolder {
    // Use a weak reference to avoid leaking the Activity
    private var _currentActivityRef: WeakReference<Activity>? = null

    var currentActivity: Activity?
        get() = _currentActivityRef?.get()
        set(value) {
            _currentActivityRef = if (value == null) null else WeakReference(value)
        }
}
