import SwiftUI
import FirebaseCore
import FirebaseAnalytics
#if canImport(GoogleSignIn)
import GoogleSignIn
#endif
import FirebaseMessaging
import UserNotifications
import AppTrackingTransparency
@main
struct iOSApp: App {
    
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    init() {
        FirebaseApp.configure()
        // Make hosting window backgrounds clear so Compose window underlay can show through
        UIWindow.appearance().backgroundColor = .clear
        
        // Configure navigation bar appearance for proper dark mode support
        let navigationBarAppearance = UINavigationBarAppearance()
        navigationBarAppearance.configureWithDefaultBackground()
        UINavigationBar.appearance().standardAppearance = navigationBarAppearance
        UINavigationBar.appearance().compactAppearance = navigationBarAppearance
        UINavigationBar.appearance().scrollEdgeAppearance = navigationBarAppearance
        
        // Configure tab bar appearance for proper dark mode support
        let tabBarAppearance = UITabBarAppearance()
        tabBarAppearance.configureWithDefaultBackground()
        UITabBar.appearance().standardAppearance = tabBarAppearance
        if #available(iOS 15.0, *) {
            UITabBar.appearance().scrollEdgeAppearance = tabBarAppearance
        }
        
        logInitialEvent()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    #if canImport(GoogleSignIn)
                    GIDSignIn.sharedInstance.handle(url)
                    #endif
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

            // Ensure existing windows are transparent
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                for scene in UIApplication.shared.connectedScenes {
                    if let ws = scene as? UIWindowScene {
                        for w in ws.windows {
                            w.backgroundColor = .clear
                        }
                    }
                }
            }

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
                        break
                    case .denied:
                        // Tracking denied
                        Analytics.setAnalyticsCollectionEnabled(false)
                    case .restricted:
                        // Tracking restricted
                        break
                    case .notDetermined:
                        // Tracking not determined
                        break
                    @unknown default:
                        // Handle unknown status
                        break
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
