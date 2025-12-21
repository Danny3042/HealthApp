import SwiftUI
import FirebaseAuth

struct ResetPasswordView: View {
    @Binding var isPresented: Bool
    @EnvironmentObject var settings: AppSettings
    @State private var email: String = ""
    @State private var message: String? = nil
    @State private var isSending: Bool = false

    var body: some View {
        NavigationView {
            VStack(spacing: 12) {
                Text("Reset Password")
                    .font(.title2)
                    .bold()
                TextField("Email address", text: $email)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .keyboardType(.emailAddress)
                    .autocapitalization(.none)
                    .submitLabel(.done)
                    .onSubmit { hideKeyboard() }
                Button(action: sendReset) {
                    if isSending { ProgressView() } else { Text("Send Reset Email") }
                }
                .frame(maxWidth: .infinity)
                .padding()
                .background(Color.accentColor)
                .foregroundColor(.white)
                .cornerRadius(8)

                if let msg = message {
                    Text(msg)
                        .foregroundColor(.secondary)
                        .multilineTextAlignment(.center)
                }

                Spacer()
            }
            .padding()
            .navigationTitle("Reset Password")
            .toolbar { ToolbarItem(placement: .cancellationAction) { Button("Close") { isPresented = false } } }
        }
    }

    private func sendReset() {
        isSending = true
        Auth.auth().sendPasswordReset(withEmail: email) { error in
            DispatchQueue.main.async {
                isSending = false
                if let err = error {
                    message = "Failed to send reset email: \(err.localizedDescription)"
                } else {
                    message = "Reset email sent successfully."
                    hideKeyboard()
                }
            }
        }
    }
}

#if DEBUG
struct ResetPasswordView_Previews: PreviewProvider {
    static var previews: some View {
        ResetPasswordView(isPresented: .constant(true)).environmentObject(AppSettings())
    }
}
#endif

// Small helper to dismiss keyboard
fileprivate func hideKeyboard() {
    UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
}
