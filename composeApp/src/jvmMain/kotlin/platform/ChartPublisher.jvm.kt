package platform

// JVM implementation used for JVM targets (tests or desktop runners). Provide a no-op
// implementation that logs values so test harnesses can inspect behavior if needed.
actual object ChartPublisher {
    actual fun publishTotals(totals: List<Float>) {
        println("ChartPublisher.jvm: publishTotals = $totals")
    }
}

