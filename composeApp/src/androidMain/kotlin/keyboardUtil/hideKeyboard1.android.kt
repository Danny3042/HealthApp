package keyboardUtil

actual fun hideKeyboard() {
    // no-op
    return
}

actual fun createCustomTextField(): CustomTextField {
    // no-op
    return object : CustomTextField {
        override fun becomeFirstResponder() {
            // no-op
            return
        }
    }
}