import SwiftUI

struct MeditationView: View {
    var body: some View {
        NavigationView {
            VStack(spacing: 16) {
                Text("Meditation")
                    .font(.title)
                Text("Simple breathing and guided meditations will be here.")
                Spacer()
            }
            .padding()
            .navigationTitle("Meditate")
        }
    }
}

#if DEBUG
struct MeditationView_Previews: PreviewProvider {
    static var previews: some View {
        MeditationView()
    }
}
#endif

