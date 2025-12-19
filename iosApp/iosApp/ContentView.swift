import UIKit
import SwiftUI
import ComposeApp

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

/// Root SwiftUI view. Use native SwiftUI by default. A developer toggle opens the Compose UI when required.
struct ContentView: View {
    @State private var showCompose: Bool = false
    @State private var isSignedIn: Bool = false
    @State private var selectedTab: Int = 0
    @StateObject private var settings = AppSettings()

    var body: some View {
        ZStack(alignment: .bottom) {
            Group {
                if showCompose {
                    ZStack(alignment: .topTrailing) {
                        ComposeViewController(onClose: { showCompose = false })
                            .ignoresSafeArea(edges: .bottom)
                        Button(action: { showCompose = false }) {
                            Image(systemName: "xmark.circle.fill")
                                .font(.title)
                                .foregroundColor(.white)
                                .padding()
                        }
                    }
                } else {
                    if isSignedIn {
                        HeroTabView(selectedTab: $selectedTab)
                            .environmentObject(settings)
                    } else {
                        NavigationView {
                            VStack {
                                LoginView(isSignedIn: $isSignedIn)
                                    .environmentObject(settings)

                                // small dev controls
                                HStack {
                                    Button(action: { showCompose = true }) {
                                        Text("Open Compose UI (dev)")
                                    }
                                    Spacer()
                                }
                                .padding(.horizontal)
                            }
                            .navigationTitle("HealthApp")
                        }
                    }
                }
            }

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
    }
}

// keep file ending
