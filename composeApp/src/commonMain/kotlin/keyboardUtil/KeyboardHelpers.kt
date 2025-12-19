package keyboardUtil

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

/**
 * Returns a KeyboardActions instance that hides the software keyboard (Compose controller),
 * calls the cross-platform hideKeyboard(), clears focus, then runs [onDone].
 * Use this in TextField/OutlinedTextField: keyboardActions = onDoneHideKeyboardAction { submit() }
 */
@Composable
fun onDoneHideKeyboardAction(onDone: () -> Unit = {}): KeyboardActions {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    return KeyboardActions(onDone = {
        keyboardController?.hide()
        focusManager.clearFocus()
        hideKeyboard()
        onDone()
    })
}

