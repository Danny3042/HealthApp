import UIKit
import SwiftUI
import ComposeApp
import FirebaseAuth

// Helper to apply native interface style and update native bar appearances
func applyNativeInterfaceStyle(dark: Bool?, useSystem: Bool) {
    DispatchQueue.main.async {
        let style: UIUserInterfaceStyle = useSystem ? .unspecified : ((dark ?? false) ? .dark : .light)
        if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene {
            for window in windowScene.windows {
                window.overrideUserInterfaceStyle = style
            }
        }

        // Update navigation bar appearance to use system background (adapts to dark/light)
        let navAppearance = UINavigationBarAppearance()
        navAppearance.configureWithDefaultBackground()
        UINavigationBar.appearance().standardAppearance = navAppearance
        UINavigationBar.appearance().scrollEdgeAppearance = navAppearance
        UINavigationBar.appearance().tintColor = UIColor.label

        // Update tab bar appearance
        let tabAppearance = UITabBarAppearance()
        tabAppearance.configureWithDefaultBackground()
        UITabBar.appearance().standardAppearance = tabAppearance
        if #available(iOS 15.0, *) {
            UITabBar.appearance().scrollEdgeAppearance = tabAppearance
        }
        UITabBar.appearance().tintColor = UIColor.systemBlue
        UITabBar.appearance().unselectedItemTintColor = UIColor.secondaryLabel
    }
}

// Shared Compose host that uses a single view across all tabs with safe area handling
struct SharedComposeHost: View {
    @Binding var selectedTab: Int
    private let tabRoutes = ["HomePage", "HabitCoachingPage", "ChatScreen", "meditation", "profile"]

    @State private var composeReady: Bool = false
    @State private var lastRequestedRoute: String? = nil
    @State private var observerAdded: Bool = false
    @State private var showBackButton: Bool = false
    @State private var currentRoute: String = ""
    private let composeReadyNotification = Notification.Name("ComposeReady")

    var body: some View {
        GeometryReader { geometry in
            ComposeViewController(onClose: nil)
                .onAppear {
                    print("=== SharedComposeHost.onAppear ===")
                    print("  - selectedTab: \(selectedTab)")
                    print("  - geometry size: \(geometry.size)")
                    
                    // Only register observer once globally
                    if !observerAdded {
                        observerAdded = true
                        
                        // Listen for ComposeReady
                        NotificationCenter.default.addObserver(
                            forName: composeReadyNotification,
                            object: nil,
                            queue: .main
                        ) { _ in
                            print("SharedComposeHost: ComposeReady notification received")
                            composeReady = true
                            if let queued = lastRequestedRoute {
                                sendRouteWithRetries(route: queued)
                                lastRequestedRoute = nil
                            }
                        }
                        
                        // Listen for route changes from Compose
                        NotificationCenter.default.addObserver(
                            forName: Notification.Name("ComposeRouteChanged"),
                            object: nil,
                            queue: .main
                        ) { notification in
                            if let route = notification.userInfo?["route"] as? String {
                                print("SharedComposeHost: Route changed to: \(route)")
                                currentRoute = route
                                
                                // Show back button for sub-pages
                                let mainRoutes = ["HomePage", "HabitCoachingPage", "ChatScreen", "meditation", "profile", "HeroScreen", "Login", "SignUp", "ResetPassword"]
                                
                                withAnimation(.spring(response: 0.3, dampingFraction: 0.7)) {
                                    showBackButton = !mainRoutes.contains(route)
                                }
                                print("SharedComposeHost: Show back button: \(showBackButton)")
                            }
                        }
                    }

                    let route = tabRoutes.indices.contains(selectedTab) ? tabRoutes[selectedTab] : "HomePage"
                    print("SharedComposeHost: requesting initial route: \(route)")
                    requestRoute(route)
                }
                .onChange(of: selectedTab) { newIndex in
                    guard newIndex >= 0 && newIndex < tabRoutes.count else { return }
                    let route = tabRoutes[newIndex]
                    print("SharedComposeHost: *** TAB CHANGED to \(newIndex), navigating to route: \(route) ***")
                    requestRoute(route)
                }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading) {
                if showBackButton {
                    Button(action: handleBackButton) {
                        HStack(spacing: 6) {
                            Image(systemName: "chevron.left")
                                .font(.system(size: 17, weight: .semibold))
                            Text("Back")
                                .font(.system(size: 17))
                        }
                    }
                    .transition(.opacity.combined(with: .scale))
                }
            }
        }
    }
    
    private func handleBackButton() {
        print("SharedComposeHost: Back button tapped")
        // Add haptic feedback
        let impactFeedback = UIImpactFeedbackGenerator(style: .light)
        impactFeedback.impactOccurred()
        
        // Post notification to Compose to handle back navigation
        NotificationCenter.default.post(
            name: Notification.Name("ComposeBackPressed"),
            object: nil
        )
    }

    private func requestRoute(_ route: String) {
        sendRouteWithRetries(route: route)
        if !composeReady {
            lastRequestedRoute = route
        } else {
            lastRequestedRoute = nil
        }
    }

    private func sendRouteWithRetries(route: String) {
        print("SharedComposeHost: sendRouteWithRetries for route: \(route)")
        AuthManager.shared.requestNavigateTo(route: route)
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.05) { 
            AuthManager.shared.requestNavigateTo(route: route) 
        }
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) { 
            AuthManager.shared.requestNavigateTo(route: route) 
        }
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) { 
            AuthManager.shared.requestNavigateTo(route: route) 
        }
    }
}

/// Wrapper to host the Compose MainViewController with singleton pattern and safe area support
struct ComposeViewController: UIViewControllerRepresentable {
    let onClose: (() -> Void)?
    
    // Singleton Compose ViewController shared across all tabs
    private static var sharedComposeVC: UIViewController?
    private static var isCreating = false
    
    class Coordinator {
        var hasSetupObserver = false
    }
    
    func makeCoordinator() -> Coordinator {
        Coordinator()
    }
    
    func makeUIViewController(context: Context) -> UIViewController {
        print("ComposeViewController.makeUIViewController called")
        
        // Prevent multiple simultaneous creations
        if ComposeViewController.isCreating {
            print("ComposeViewController: Already creating, waiting...")
            // Return a placeholder
            let placeholder = UIViewController()
            placeholder.view.backgroundColor = .clear
            return placeholder
        }
        
        // Return the shared instance if it exists
        if let existing = ComposeViewController.sharedComposeVC {
            print("ComposeViewController: Reusing existing Compose VC")
            return existing
        }
        
        // Create new instance only once
        ComposeViewController.isCreating = true
        let composeVC = MainViewControllerKt.MainViewController()
        composeVC.view.backgroundColor = .clear
        print("ComposeViewController: Created new Compose VC")
        
        // Store as singleton
        ComposeViewController.sharedComposeVC = composeVC
        ComposeViewController.isCreating = false
        
        return composeVC
    }
    
    // Ensure the shared compose VC is visible and in front of other UI layers
    static func ensureSharedVisible() {
        DispatchQueue.main.async {
            guard let vc = ComposeViewController.sharedComposeVC else { return }
            vc.view.isHidden = false
            if let sup = vc.view.superview {
                print("ComposeViewController: bringing existing sharedComposeVC.view to front of its superview")
                sup.bringSubviewToFront(vc.view)
            } else {
                print("ComposeViewController: sharedComposeVC.view has no superview yet — nothing to bring to front")
            }
        }
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // Only send safe area insets once per update cycle
        if !context.coordinator.hasSetupObserver {
            context.coordinator.hasSetupObserver = true
        }
        
        // Send safe area insets to Compose
        let safeAreaInsets = uiViewController.view.safeAreaInsets
        let insetsInfo: [String: CGFloat] = [
            "top": safeAreaInsets.top,
            "bottom": safeAreaInsets.bottom,
            "leading": safeAreaInsets.left,
            "trailing": safeAreaInsets.right
        ]
        
        NotificationCenter.default.post(
            name: Notification.Name("SafeAreaInsetsChanged"),
            object: nil,
            userInfo: insetsInfo
        )
        // Also attempt typed call into Kotlin-generated bridge if available.
        let bridge = PlatformBridge.shared
        // Swift binding should expose setSafeAreaInsets(top:bottom:leading:trailing:)
        bridge.setSafeAreaInsets(
            top: Double(insetsInfo["top"] ?? 0.0),
            bottom: Double(insetsInfo["bottom"] ?? 0.0),
            leading: Double(insetsInfo["leading"] ?? 0.0),
            trailing: Double(insetsInfo["trailing"] ?? 0.0)
        )
    }
}

/// Root SwiftUI view that uses native TabView with Compose content
struct ContentView: View {
    @State private var selectedTab: Int = 0
    @StateObject private var settings = AppSettings()
    @State private var authHandle: AuthStateDidChangeListenerHandle? = nil

    var body: some View {
        ZStack {
            // Native TabView with shared Compose host in each tab
            TabView(selection: $selectedTab) {
                NavigationView {
                    SharedComposeHost(selectedTab: $selectedTab)
                        .ignoresSafeArea(.all, edges: [.top, .bottom])
                        .navigationTitle("Home")
                        .navigationBarTitleDisplayMode(.large)
                }
                .tabItem {
                    Label("Home", systemImage: "house")
                }
                .tag(0)
                
                NavigationView {
                    SharedComposeHost(selectedTab: $selectedTab)
                        .ignoresSafeArea(.all, edges: [.top, .bottom])
                        .navigationTitle("Habits")
                        .navigationBarTitleDisplayMode(.large)
                }
                .tabItem {
                    Label("Habits", systemImage: "checkmark.circle")
                }
                .tag(1)
                
                NavigationView {
                    SharedComposeHost(selectedTab: $selectedTab)
                        .ignoresSafeArea(.all, edges: [.top, .bottom])
                        .navigationTitle("Chat")
                        .navigationBarTitleDisplayMode(.large)
                }
                .tabItem {
                    Label("Chat", systemImage: "message")
                }
                .tag(2)
                
                NavigationView {
                    SharedComposeHost(selectedTab: $selectedTab)
                        .ignoresSafeArea(.all, edges: [.top, .bottom])
                        .navigationTitle("Meditate")
                        .navigationBarTitleDisplayMode(.large)
                }
                .tabItem {
                    Label("Meditate", systemImage: "apple.meditate.circle.fill")
                }
                .tag(3)
                
                NavigationView {
                    SharedComposeHost(selectedTab: $selectedTab)
                        .ignoresSafeArea(.all, edges: [.top, .bottom])
                        .navigationTitle("Profile")
                        .navigationBarTitleDisplayMode(.large)
                }
                .tabItem {
                    Label("Profile", systemImage: "person")
                }
                .tag(4)
            }

            // Snackbar overlay
            if settings.showSnackbar {
                VStack {
                    Spacer()
                    Text(settings.snackbarMessage)
                        .padding()
                        .background(Color.black.opacity(0.8))
                        .foregroundColor(.white)
                        .cornerRadius(8)
                        .padding()
                        .padding(.bottom, 60)
                }
                .transition(.move(edge: .bottom).combined(with: .opacity))
            }
        }
        .ignoresSafeArea(.all)
        .onAppear {
            print("=== ContentView.onAppear ===")
            
            // Force update window interface style
            if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene {
                windowScene.windows.forEach { window in
                    window.overrideUserInterfaceStyle = .unspecified
                }
            }

            // Ensure Compose shared VC is visible when Compose reports navigation changes or is ready
            NotificationCenter.default.addObserver(forName: Notification.Name("ComposeNavigationChanged"), object: nil, queue: .main) { note in
                print("ContentView: ComposeNavigationChanged received - ensuring Compose VC visible")
                ComposeViewController.ensureSharedVisible()
            }

            NotificationCenter.default.addObserver(forName: Notification.Name("ComposeReady"), object: nil, queue: .main) { _ in
                print("ContentView: ComposeReady received - ensuring Compose VC visible")
                ComposeViewController.ensureSharedVisible()
            }

            // Listen for dark mode updates from Compose and apply native style & appearances
            NotificationCenter.default.addObserver(forName: Notification.Name("ComposeDarkModeChanged"), object: nil, queue: .main) { note in
                print("ContentView: ComposeDarkModeChanged received: \(String(describing: note.userInfo))")
                guard let userInfo = note.userInfo as? [String: Any] else { return }
                let dark = userInfo["dark"] as? Bool
                let useSystem = userInfo["useSystem"] as? Bool ?? true
                applyNativeInterfaceStyle(dark: dark, useSystem: useSystem)
            }
        }
        .onDisappear {
            if let h = authHandle {
                Auth.auth().removeStateDidChangeListener(h)
                authHandle = nil
            }
        }
    }
}

// VisualEffectBlur helper to get native blur background (uses UIVisualEffectView)
struct VisualEffectBlur: UIViewRepresentable {
    var blurStyle: UIBlurEffect.Style

    func makeUIView(context: Context) -> UIVisualEffectView {
        return UIVisualEffectView(effect: UIBlurEffect(style: blurStyle))
    }

    func updateUIView(_ uiView: UIVisualEffectView, context: Context) {}
}

// keep file ending
