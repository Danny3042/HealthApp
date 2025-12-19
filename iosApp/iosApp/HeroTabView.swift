import SwiftUI
import UIKit
import FirebaseAuth

struct HeroTabView: View {
    @Binding var selectedTab: Int

    // Map native tab index to Compose route names
    private let tabRoutes = [
        "HomePage",           // 0 -> Home
        "HabitCoachingPage",  // 1 -> Habits
        "ChatScreen",         // 2 -> Chat
        "meditation",         // 3 -> Meditation
        "profile"             // 4 -> Profile
    ]

    var body: some View {
        TabView(selection: $selectedTab) {
            HomeTabView()
                .onAppear { selectTab(0) }
                .tabItem { Label("Home", systemImage: "house") }
                .tag(0)

            HabitsTabView()
                .onAppear { selectTab(1) }
                .tabItem { Label("Habits", systemImage: "checkmark.circle") }
                .tag(1)

            ChatTabView()
                .onAppear { selectTab(2) }
                .tabItem { Label("Chat", systemImage: "message") }
                .tag(2)

            MeditateTabView()
                .onAppear { selectTab(3) }
                .tabItem { Label("Meditate", systemImage: "person.crop.circle") }
                .tag(3)

            ProfileTabView()
                .onAppear { selectTab(4) }
                .tabItem { Label("Profile", systemImage: "person") }
                .tag(4)
        }
        .onChange(of: selectedTab) { newIndex in
            selectTab(newIndex)
        }
    }

    private func selectTab(_ index: Int) {
        guard index >= 0 && index < tabRoutes.count else { return }
        let route = tabRoutes[index]
        DispatchQueue.main.async {
            // send at least once, Compose side will pick it up
            AuthManager.shared.requestNavigateTo(route: route)
        }
    }
}

// MARK: - Home
struct HomeTabView: View {
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    Text(greeting())
                        .font(.largeTitle)
                        .bold()

                    // Quick actions row
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 12) {
                            QuickCard(title: "Start Session", systemImage: "play.circle")
                            QuickCard(title: "Track Habit", systemImage: "checkmark.seal")
                            QuickCard(title: "Insights", systemImage: "chart.bar")
                        }
                        .padding(.horizontal)
                    }

                    // Example content list
                    VStack(alignment: .leading, spacing: 12) {
                        Text("Today")
                            .font(.headline)
                            .padding(.horizontal)

                        ForEach(0..<5) { i in
                            HStack {
                                RoundedRectangle(cornerRadius: 8)
                                    .fill(Color.blue.opacity(0.1))
                                    .frame(width: 6, height: 40)
                                VStack(alignment: .leading) {
                                    Text("Task #\(i + 1)")
                                        .font(.subheadline)
                                    Text("Short description for task")
                                        .font(.caption)
                                        .foregroundColor(.secondary)
                                }
                                .padding(.vertical, 8)
                                Spacer()
                            }
                            .padding(.horizontal)
                            .background(Color(UIColor.secondarySystemBackground))
                            .cornerRadius(8)
                            .padding(.horizontal)
                        }
                    }
                }
                .padding(.top)
            }
            .navigationTitle("Home")
        }
    }

    private func greeting() -> String {
        if let name = Auth.auth().currentUser?.displayName {
            return "Hello, \(name)"
        }
        return "Welcome"
    }
}

struct QuickCard: View {
    let title: String
    let systemImage: String
    var body: some View {
        VStack {
            Image(systemName: systemImage)
                .font(.system(size: 30))
                .padding()
                .background(Color.accentColor.opacity(0.15))
                .clipShape(RoundedRectangle(cornerRadius: 12))

            Text(title)
                .font(.caption)
        }
        .frame(width: 120, height: 120)
        .background(Color(UIColor.systemBackground))
        .cornerRadius(12)
        .shadow(color: Color.black.opacity(0.05), radius: 4, x: 0, y: 2)
    }
}

// MARK: - Habits
struct HabitsTabView: View {
    @State private var habits: [String] = ["Drink water", "Walk 10 minutes"]
    @State private var newHabit: String = ""

    var body: some View {
        NavigationView {
            VStack {
                HStack {
                    TextField("New habit", text: $newHabit)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                    Button(action: addHabit) {
                        Image(systemName: "plus.circle.fill")
                            .font(.title2)
                    }
                    .disabled(newHabit.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)
                }
                .padding()

                List {
                    ForEach(habits.indices, id: \ .self) { idx in
                        HStack {
                            Text(habits[idx])
                            Spacer()
                            Button(action: { removeHabit(at: idx) }) {
                                Image(systemName: "trash")
                                    .foregroundColor(.red)
                            }
                        }
                    }
                }
            }
            .navigationTitle("Habits")
        }
    }

    private func addHabit() {
        let trimmed = newHabit.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else { return }
        habits.append(trimmed)
        newHabit = ""
        // Optionally notify Compose or persistence here
    }

    private func removeHabit(at idx: Int) {
        habits.remove(at: idx)
    }
}

// MARK: - Chat
struct ChatTabView: View {
    @State private var messages: [String] = ["Welcome to chat!"]
    @State private var text: String = ""

    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                ScrollViewReader { proxy in
                    ScrollView {
                        VStack(alignment: .leading, spacing: 8) {
                            ForEach(Array(messages.enumerated()), id: \ .0) { idx, msg in
                                HStack {
                                    Text(msg)
                                        .padding(10)
                                        .background(Color.gray.opacity(0.15))
                                        .cornerRadius(8)
                                    Spacer()
                                }
                                .padding(.horizontal)
                            }
                        }
                        .padding(.top)
                    }
                    .onChange(of: messages.count) { _ in
                        if let last = messages.indices.last {
                            withAnimation { proxy.scrollTo(last, anchor: .bottom) }
                        }
                    }
                }

                HStack {
                    TextField("Message", text: $text)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                    Button(action: send) {
                        Text("Send")
                    }
                }
                .padding()
            }
            .navigationTitle("Chat")
        }
    }

    private func send() {
        let trimmed = text.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else { return }
        messages.append(trimmed)
        text = ""
    }
}

// MARK: - Meditate
struct MeditateTabView: View {
    let sessions = ["Breathing 5m", "Body Scan 10m", "Focused 15m"]

    var body: some View {
        NavigationView {
            List(sessions, id: \ .self) { s in
                HStack {
                    VStack(alignment: .leading) {
                        Text(s)
                            .font(.headline)
                        Text("Guided session")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                    Spacer()
                    Button(action: { /* start */ }) {
                        Image(systemName: "play.circle.fill")
                            .font(.title2)
                    }
                }
                .padding(.vertical, 6)
            }
            .navigationTitle("Meditate")
        }
    }
}

// MARK: - Profile
struct ProfileTabView: View {
    var body: some View {
        NavigationView {
            VStack(spacing: 16) {
                if let user = Auth.auth().currentUser {
                    Text(user.displayName ?? "User")
                        .font(.title)
                    Text(user.email ?? "")
                        .foregroundColor(.secondary)
                } else {
                    Text("Not signed in")
                }

                Divider()

                Button(action: { AuthManager.shared.requestShowLogin() }) {
                    Text("Sign in / Manage account")
                }

                Spacer()
            }
            .padding()
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
