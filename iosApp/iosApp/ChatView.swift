import SwiftUI

struct ChatView: View {
    @State private var messages: [String] = ["Welcome to Health Chat"]
    @State private var input: String = ""

    var body: some View {
        NavigationView {
            VStack {
                List(messages, id: \.self) { msg in
                    Text(msg)
                }
                HStack {
                    TextField("Talk to AI...", text: $input)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                    Button(action: send) {
                        Image(systemName: "paperplane.fill")
                            .foregroundColor(.accentColor)
                    }
                }
                .padding()
            }
            .navigationTitle("Chat")
        }
    }

    private func send() {
        let trimmed = input.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else { return }
        messages.append(trimmed)
        input = ""
        hideKeyboard()
    }
}

#if DEBUG
struct ChatView_Previews: PreviewProvider {
    static var previews: some View {
        ChatView()
    }
}
#endif

fileprivate func hideKeyboard() {
    UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
}
