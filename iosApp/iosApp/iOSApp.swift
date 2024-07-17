import SwiftUI
import FirebaseCore
import FirebaseAnalytics
import GoogleSignIn
@main
struct iOSApp: App {
    
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    init() {
        FirebaseApp.configure()
        logInitialEvent()
    }
	var body: some Scene {
		WindowGroup {
            ContentView()
                .onOpenURL { url in
                GIDSignIn.sharedInstance.handle(url)
                    
            }
		}
	}
    
    func logInitialEvent() {
        // logging app open event
        Analytics.logEvent(AnalyticsEventAppOpen, parameters: nil)
    }
}

class AppDelegate: NSObject, UIApplicationDelegate {
    
    func application(
        _ app: UIApplication,
        open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]
    ) -> Bool {
        var handled: Bool
        
    // let Google Sign in handle the URL if it is related to Google Sign in
        handled = GIDSignIn.sharedInstance.handle(url)
        if handled {
            return true
        }
        
        // Handle other custom URL types
        
        // if not handled by this app return false
        return false
    }
}
