import SwiftUI

struct ProfileView: View {
    var body: some View {
        NavigationView {
            VStack(spacing: 12) {
                Text("Profile")
                    .font(.title)
                Text("User profile details would be shown here.")
                Spacer()
            }
            .padding()
            .navigationTitle("Profile")
        }
    }
}

#if DEBUG
struct ProfileView_Previews: PreviewProvider {
    static var previews: some View {
        ProfileView()
    }
}
#endif

