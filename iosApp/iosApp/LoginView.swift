import SwiftUI

struct LoginView: View {
    @Binding var isSignedIn: Bool
    @EnvironmentObject var settings: AppSettings
    @State private var email: String = ""
    @State private var password: String = ""
    @State private var showError: Bool = false

    var body: some View {
        VStack(spacing: 16) {
            Text("Sign in")
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

            if showError {
                Text("Invalid credentials")
                    .foregroundColor(.red)
            }

            Button(action: signIn) {
                Text("Sign in")
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.accentColor)
                    .foregroundColor(.white)
                    .cornerRadius(8)
            }

            Spacer()
        }
        .padding()
    }

    private func signIn() {
        // Replace with the real auth call. For now, local logic:
        if email.lowercased() == "fred@example.com" {
            // enable dev mode automatically for Fred
            settings.enableDevMode(true, message: "Dev mode enabled for fred@example.com")
            isSignedIn = true
        } else if !email.isEmpty {
            isSignedIn = true
        } else {
            showError = true
        }
    }
}

#if DEBUG
struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        LoginView(isSignedIn: .constant(false))
    }
}
#endif

// Small helper to dismiss keyboard
fileprivate func hideKeyboard() {
    UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
}
