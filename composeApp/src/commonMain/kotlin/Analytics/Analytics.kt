package Analytics
interface Analytics {
    fun logEvent(name: String, params: Map<String, String>)
}

