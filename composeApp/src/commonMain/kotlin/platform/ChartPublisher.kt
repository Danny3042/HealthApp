package platform

// Bridge for publishing chart data from common code to platform (iOS will implement this)
expect object ChartPublisher {
    fun publishTotals(totals: List<Float>)
}

