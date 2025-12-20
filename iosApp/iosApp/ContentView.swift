import UIKit
import SwiftUI
import ComposeApp
import FirebaseAuth

// Shared Compose host that uses a single view across all tabs with safe area handling
struct SharedComposeHost: View {
    @Binding var selectedTab: Int
    private let tabRoutes = ["HomePage", "HabitCoachingPage", "ChatScreen", "meditation", "profile"]

    @State private var composeReady: Bool = false
    @State private var lastRequestedRoute: String? = nil
    @State private var observerAdded: Bool = false
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
                        .navigationTitle("Home")
                        .navigationBarTitleDisplayMode(.large)
                }
                .tabItem {
                    Label("Home", systemImage: "house")
                }
                .tag(0)
                
                NavigationView {
                    SharedComposeHost(selectedTab: $selectedTab)
                        .navigationTitle("Habits")
                        .navigationBarTitleDisplayMode(.large)
                }
                .tabItem {
                    Label("Habits", systemImage: "checkmark.circle")
                }
                .tag(1)
                
                NavigationView {
                    SharedComposeHost(selectedTab: $selectedTab)
                        .navigationTitle("Chat")
                        .navigationBarTitleDisplayMode(.large)
                }
                .tabItem {
                    Label("Chat", systemImage: "message")
                }
                .tag(2)
                
                NavigationView {
                    SharedComposeHost(selectedTab: $selectedTab)
                        .navigationTitle("Meditate")
                        .navigationBarTitleDisplayMode(.large)
                }
                .tabItem {
                    Label("Meditate", systemImage: "person.crop.circle")
                }
                .tag(3)
                
                NavigationView {
                    SharedComposeHost(selectedTab: $selectedTab)
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
            
            authHandle = Auth.auth().addStateDidChangeListener { _, user in
                DispatchQueue.main.async {
                    print("ContentView: Auth state changed, user: \(user?.email ?? "nil")")
                    if user != nil {
                        AuthManager.shared.requestNavigateTo(route: "HeroScreen")
                    } else {
                        AuthManager.shared.requestNavigateTo(route: "Login")
                    }
                }
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
