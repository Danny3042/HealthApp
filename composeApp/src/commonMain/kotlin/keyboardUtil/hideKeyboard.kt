package keyboardUtil

expect fun hideKeyboard()

interface CustomTextField {
    fun becomeFirstResponder()
}

expect fun createCustomTextField(): CustomTextField