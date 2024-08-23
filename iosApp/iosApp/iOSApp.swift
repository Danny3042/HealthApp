import SwiftUI
import FirebaseCore
import FirebaseAnalytics
import GoogleSignIn
import FirebaseMessaging
import UserNotifications
import AppTrackingTransparency

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
                    UIApplication.shared.endEditing()
                }
        }
    }
    
    func logInitialEvent() {
        // logging app open event
        Analytics.logEvent(AnalyticsEventAppOpen, parameters: nil)
    }
    
    
    
    class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate, MessagingDelegate {
        func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
            
            DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                self.requestTrackingPermission()
            }
            return true
        }
        
        
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
        
  
        
        private func requestTrackingPermission() {
                ATTrackingManager.requestTrackingAuthorization { status in
                    switch status {
                    case .authorized:
                        // Tracking authorized
                        print("Tracking authorized")
                    case .denied:
                        // Tracking denied
                        print("Tracking denied")
                        Analytics.setAnalyticsCollectionEnabled(false)
                    case .restricted:
                        // Tracking restricted
                        print("Tracking restricted")
                    case .notDetermined:
                        // Tracking not determined
                        print("Tracking not determined")
                    @unknown default:
                        // Handle unknown status
                        print("Unknown tracking status")
                    }
                }
            
        }
    }
}
    extension UIApplication {
        func endEditing() {
            sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
        }
    }
