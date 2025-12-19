import SwiftUI

struct HeroTabView: View {
    @Binding var selectedTab: Int

    var body: some View {
        TabView(selection: $selectedTab) {
            HomeTabView()
                .tabItem {
                    Label("Home", systemImage: "house")
                }
                .tag(0)

            HabitTrackerView()
                .tabItem {
                    Label("Habits", systemImage: "checkmark.circle")
                }
                .tag(1)

            ChatView()
                .tabItem {
                    Label("Chat", systemImage: "message")
                }
                .tag(2)

            MeditationView()
                .tabItem {
                    Label("Meditate", systemImage: "person.crop.circle")
                }
                .tag(3)

            ProfileView()
                .tabItem {
                    Label("Profile", systemImage: "person")
                }
                .tag(4)
        }
    }
}

struct HomeTabView: View {
    var body: some View {
        NavigationView {
            VStack(spacing: 12) {
                Text("Home content (SwiftUI)")
                    .font(.title2)
                Spacer()
            }
            .padding()
            .navigationTitle("Home")
        }
    }
}

struct HabitsTabView: View {
    var body: some View {
        NavigationView {
            Text("Habits (SwiftUI)")
                .navigationTitle("Habits")
        }
    }
}

struct ChatTabView: View {
    var body: some View {
        NavigationView {
            Text("Chat (SwiftUI)")
                .navigationTitle("Chat")
        }
    }
}

struct MeditateTabView: View {
    var body: some View {
        NavigationView {
            Text("Meditate (SwiftUI)")
                .navigationTitle("Meditate")
        }
    }
}

struct ProfileTabView: View {
    var body: some View {
        NavigationView {
            Text("Profile (SwiftUI)")
                .navigationTitle("Profile")
        }
    }
}

#if DEBUG
struct HeroTabView_Previews: PreviewProvider {
    static var previews: some View {
        HeroTabView(selectedTab: .constant(0))
    }
}
#endif
