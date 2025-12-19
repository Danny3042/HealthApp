import UIKit
import SwiftUI
import ComposeApp
import FirebaseAuth

/// Small wrapper to host the existing Compose MainViewController when needed.
struct ComposeViewController: UIViewControllerRepresentable {
    let onClose: (() -> Void)?

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // no-op
    }
}

/// Root SwiftUI view that shows tabs with a shared Compose host.
struct ContentView: View {
    @StateObject private var settings = AppSettings()
    @State private var isSignedIn: Bool = Auth.auth().currentUser != nil
    @State private var authHandle: AuthStateDidChangeListenerHandle? = nil
    @State private var selectedTab: Int = 0

    var body: some View {
        ZStack(alignment: .bottom) {
            if isSignedIn {
                HeroTabView(selectedTab: $selectedTab)
                    .ignoresSafeArea(edges: .all)
            } else {
                // Show login when not signed in
                ComposeViewController(onClose: nil)
                    .ignoresSafeArea(edges: .all)
            }

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
                    self.isSignedIn = (user != nil)
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
