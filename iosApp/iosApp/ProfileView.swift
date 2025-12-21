import SwiftUI

struct ProfileView: View {
    @EnvironmentObject var settings: AppSettings

    var body: some View {
        NavigationView {
            VStack(spacing: 12) {
                Text("Profile")
                    .font(.title)
                Text("User profile details would be shown here.")

                Toggle(isOn: $settings.devMode) {
                    Text("Dev Mode")
                }
                .onChange(of: settings.devMode) { enabled in
                    settings.enableDevMode(enabled, message: enabled ? "Dev mode enabled" : "Dev mode disabled")
                }

                if settings.devMode {
                    NavigationLink("Open Debug Screen", destination: DebugScreen())
                }

                Spacer()
            }
            .padding()
            .navigationTitle("Profile")
        }
    }
}

struct DebugScreen: View {
    var body: some View {
        VStack {
            Text("Debug Screen")
            Text("Place debug features here (logs, mock data, AI debug, etc).")
        }
        .padding()
        .navigationTitle("Debug")
    }
}

#if DEBUG
struct ProfileView_Previews: PreviewProvider {
    static var previews: some View {
        ProfileView()
    }
}
#endif
