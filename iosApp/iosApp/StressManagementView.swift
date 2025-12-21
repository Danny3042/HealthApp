import SwiftUI

struct StressManagementView: View {
    @State private var activity: String = ""
    @State private var showTip: String? = nil

    var body: some View {
        NavigationView {
            VStack(alignment: .leading, spacing: 12) {
                Text("Stress Management")
                    .font(.title)
                    .bold()

                TextField("What stress relief activity do you want to try?", text: $activity, onCommit: {
                    hideKeyboard()
                })
                .textFieldStyle(RoundedBorderTextFieldStyle())

                Button(action: {
                    hideKeyboard()
                    if !activity.isEmpty {
                        showTip = "Try short breathing exercises for 2 minutes"
                        activity = ""
                    }
                }) {
                    Text("Get AI Tip & Add Activity")
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.accentColor)
                        .foregroundColor(.white)
                        .cornerRadius(8)
                }

                if let tip = showTip {
                    Text("AI Coach")
                        .font(.headline)
                    Text(tip)
                        .padding()
                        .background(Color(.systemGray6))
                        .cornerRadius(8)
                }

                Spacer()
            }
            .padding()
            .navigationTitle("Stress")
        }
    }
}

#if DEBUG
struct StressManagementView_Previews: PreviewProvider {
    static var previews: some View {
        StressManagementView()
    }
}
#endif

fileprivate func hideKeyboard() {
    UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
}

