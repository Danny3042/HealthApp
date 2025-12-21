import SwiftUI
import FirebaseAuth

struct SignUpView: View {
    @Binding var isPresented: Bool
    @Binding var isSignedIn: Bool
    @EnvironmentObject var settings: AppSettings

    @State private var email: String = ""
    @State private var password: String = ""
    @State private var errorMessage: String? = nil
    @State private var isLoading: Bool = false

    var body: some View {
        NavigationView {
            VStack(spacing: 12) {
                Text("Sign Up")
                    .font(.largeTitle)
                    .bold()

                TextField("Email", text: $email)
                    .textContentType(.emailAddress)
                    .keyboardType(.emailAddress)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .autocapitalization(.none)
                    .submitLabel(.done)
                    .onSubmit { hideKeyboard() }

                SecureField("Password", text: $password)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .submitLabel(.done)
                    .onSubmit { hideKeyboard() }

                if let msg = errorMessage {
                    Text(msg)
                        .foregroundColor(.red)
                        .multilineTextAlignment(.center)
                }

                Button(action: signUp) {
                    if isLoading { ProgressView() } else { Text("Sign up") }
                }
                .frame(maxWidth: .infinity)
                .padding()
                .background(Color.accentColor)
                .foregroundColor(.white)
                .cornerRadius(8)

                Spacer()
            }
            .padding()
            .navigationTitle("Sign Up")
            .toolbar { ToolbarItem(placement: .cancellationAction) { Button("Close") { isPresented = false } } }
        }
    }

    private func signUp() {
        isLoading = true
        Auth.auth().createUser(withEmail: email, password: password) { authResult, error in
            DispatchQueue.main.async {
                isLoading = false
                if let err = error {
                    errorMessage = err.localizedDescription
                    return
                }
                if let _ = authResult?.user {
                    if email.lowercased() == "fred@example.com" {
                        settings.enableDevMode(true, message: "Dev mode enabled for fred@example.com")
                    }
                    isSignedIn = true
                    isPresented = false
                    hideKeyboard()
                } else {
                    errorMessage = "Failed to create account"
                }
            }
        }
    }
}

#if DEBUG
struct SignUpView_Previews: PreviewProvider {
    static var previews: some View {
        SignUpView(isPresented: .constant(true), isSignedIn: .constant(false)).environmentObject(AppSettings())
    }
}
#endif

// Small helper to dismiss keyboard
fileprivate func hideKeyboard() {
    UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
}
