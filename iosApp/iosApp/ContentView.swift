import UIKit
import SwiftUI
import ComposeApp
import FirebaseAuth

/// Small wrapper to host the existing Compose MainViewController when needed.
struct ComposeViewController: UIViewControllerRepresentable {
    let onClose: (() -> Void)?

    func makeUIViewController(context: Context) -> UIViewController {
        let vc = MainViewControllerKt.MainViewController()
        // Make sure the Compose view doesn't draw an opaque background that hides native overlay
        vc.view.backgroundColor = UIColor.clear
        vc.view.isOpaque = false
        print("ComposeViewController created: \(vc)")
        return vc
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // keep the compose view visible and transparent
        uiViewController.view.backgroundColor = UIColor.clear
        uiViewController.view.isOpaque = false
    }
}

// Single shared Compose host used by native SwiftUI tab bar
struct SharedComposeHost: View {
    @Binding var selectedTab: Int
    private let tabRoutes = ["HomePage", "HabitCoachingPage", "ChatScreen", "meditation", "profile"]

    var body: some View {
        ComposeViewController(onClose: nil)
            .ignoresSafeArea(edges: .all)
            .onAppear {
                let route = tabRoutes.indices.contains(selectedTab) ? tabRoutes[selectedTab] : "HomePage"
                AuthManager.shared.requestNavigateTo(route: route)
            }
            .onChange(of: selectedTab) { newIndex in
                guard newIndex >= 0 && newIndex < tabRoutes.count else { return }
                AuthManager.shared.requestNavigateTo(route: tabRoutes[newIndex])
            }
    }
}

/// Root SwiftUI view that uses a native SwiftUI TabView and a single shared Compose host underneath.
struct ContentView: View {
    @State private var selectedTab: Int = 0
    @StateObject private var settings = AppSettings()
    @State private var authHandle: AuthStateDidChangeListenerHandle? = nil

    var body: some View {
        ZStack(alignment: .bottom) {
            // Compose content sits underneath and responds to native tab selections
            SharedComposeHost(selectedTab: $selectedTab)

            // Native SwiftUI TabView on top with transparent content
            HeroTabView(selectedTab: $selectedTab)

            // Snackbar
            if settings.showSnackbar {
                Text(settings.snackbarMessage)
                    .padding()
                    .background(Color.black.opacity(0.8))
                    .foregroundColor(.white)
                    .cornerRadius(8)
                    .padding()
                    .transition(.move(edge: .bottom).combined(with: .opacity))
            }
        }
        .onAppear {
            authHandle = Auth.auth().addStateDidChangeListener { _, user in
                DispatchQueue.main.async {
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
