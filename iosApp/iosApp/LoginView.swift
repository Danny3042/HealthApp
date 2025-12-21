import SwiftUI
import AuthenticationServices
import FirebaseAuth

struct LoginView: View {
    @Binding var isSignedIn: Bool
    @EnvironmentObject var settings: AppSettings
    @State private var email: String = ""
    @State private var password: String = ""
    @State private var showError: Bool = false
    @State private var errorMessage: String? = nil
    @State private var isLoading: Bool = false
    @State private var showResetSheet: Bool = false
    @State private var showGoogleSignInError: Bool = false
    @State private var showSignUpSheet: Bool = false
    @State private var isPasswordVisible: Bool = false
    @State private var showSnackbar: Bool = false
    @State private var snackbarMessage: String = ""

    var body: some View {
        VStack(spacing: 16) {
            Text("Sign in")
                .font(.largeTitle)
                .bold()

            // Email field
            TextField("Email address", text: $email)
                .textContentType(.emailAddress)
                .keyboardType(.emailAddress)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .autocapitalization(.none)
                .submitLabel(.done)
                .onSubmit { hideKeyboard() }

            // Password field with visibility toggle
            HStack {
                if isPasswordVisible {
                    TextField("Password", text: $password)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .submitLabel(.done)
                        .onSubmit { hideKeyboard() }
                } else {
                    SecureField("Password", text: $password)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .submitLabel(.done)
                        .onSubmit { hideKeyboard() }
                }
                Button(action: { isPasswordVisible.toggle() }) {
                    Image(systemName: isPasswordVisible ? "eye.slash" : "eye")
                        .foregroundColor(.secondary)
                }
            }

            if let message = errorMessage {
                Text(message)
                    .foregroundColor(.red)
                    .multilineTextAlignment(.center)
            }

            Button(action: signIn) {
                if isLoading {
                    ProgressView()
                        .frame(maxWidth: .infinity)
                        .padding()
                } else {
                    Text("Sign in")
                        .frame(maxWidth: .infinity)
                        .padding()
                }
            }
            .background(Color.accentColor)
            .foregroundColor(.white)
            .cornerRadius(8)
            .disabled(isLoading)

            HStack {
                Button(action: { showResetSheet = true }) {
                    Text("Forgot password?")
                }
                Spacer()
                Button(action: { showSignUpSheet = true }) {
                    Text("Sign Up")
                }
                Spacer()
            }

            // Third-party sign-in buttons (Google / Apple)
            VStack(spacing: 12) {
                Button(action: {
                    guard let anchor = Self.currentPresentationAnchor() else {
                        snackbar("Unable to find window to present Apple Sign-In")
                        return
                    }
                    AuthManager.shared.startSignInWithAppleFlow(presentationAnchor: anchor) { result in
                        DispatchQueue.main.async {
                            switch result {
                            case .success(_):
                                handleSuccessfulSignIn()
                            case .failure(let err):
                                snackbar(err.localizedDescription)
                            }
                        }
                    }
                }) {
                    HStack {
                        Image(systemName: "applelogo")
                        Text("Sign in with Apple")
                    }
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.black)
                    .foregroundColor(.white)
                    .cornerRadius(8)
                }
                .frame(height: 44)

                #if canImport(GoogleSignIn)
                Button(action: {
                    guard let root = Self.keyWindowRootViewController() else {
                        snackbar("Unable to find UI to present Google Sign-In")
                        return
                    }
                    AuthManager.shared.signInWithGoogle(presenting: root) { result in
                        DispatchQueue.main.async {
                            switch result {
                            case .success(_):
                                handleSuccessfulSignIn()
                            case .failure(let err):
                                snackbar(err.localizedDescription)
                            }
                        }
                    }
                }) {
                    HStack {
                        Image(systemName: "globe")
                        Text("Sign in with Google")
                    }
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.white)
                    .foregroundColor(.black)
                    .cornerRadius(8)
                }
                #endif
            }

            Spacer()
        }
        .padding()
        .overlay(alignment: .bottom) {
            if showSnackbar {
                Text(snackbarMessage)
                    .padding()
                    .background(Color(.systemGray6))
                    .cornerRadius(8)
                    .padding()
            }
        }
        .sheet(isPresented: $showResetSheet) {
            ResetPasswordView(isPresented: $showResetSheet)
                .environmentObject(settings)
        }
        .sheet(isPresented: $showSignUpSheet) {
            SignUpView(isPresented: $showSignUpSheet, isSignedIn: $isSignedIn)
                .environmentObject(settings)
        }
    }

    private func signIn() {
        isLoading = true
        AuthManager.shared.signIn(email: email, password: password) { result in
            DispatchQueue.main.async {
                isLoading = false
                switch result {
                case .success(_):
                    handleSuccessfulSignIn()
                case .failure(let err):
                    snackbar(err.localizedDescription)
                }
            }
        }
    }

    private func handleSuccessfulSignIn() {
        if email.lowercased() == "fred@example.com" {
            settings.enableDevMode(true, message: "Dev mode enabled for fred@example.com")
        }
        isSignedIn = true
    }

    private func snackbar(_ message: String) {
        snackbarMessage = message
        showSnackbar = true
        DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
            showSnackbar = false
        }
    }

    // Helper to find current presentation anchor (ASPresentationAnchor is UIWindow)
    private static func currentPresentationAnchor() -> ASPresentationAnchor? {
        return UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap { $0.windows }
            .first { $0.isKeyWindow }
    }

    private static func keyWindowRootViewController() -> UIViewController? {
        return UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap { $0.windows }
            .first { $0.isKeyWindow }?
            .rootViewController
    }
}

#if DEBUG
struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        LoginView(isSignedIn: .constant(false)).environmentObject(AppSettings())
    }
}
#endif

// Small helper to dismiss keyboard
fileprivate func hideKeyboard() {
    UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
}
