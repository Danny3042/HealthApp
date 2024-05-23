import SwiftUI
import FirebaseCore
import FirebaseAnalytics

@main
struct iOSApp: App {
    
    init() {
        FirebaseApp.configure()
        logInitialEvent()
    }
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
    
    func logInitialEvent() {
        // logging app open event
        Analytics.logEvent(AnalyticsEventAppOpen, parameters: nil)
    }
}

