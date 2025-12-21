import SwiftUI

struct HabitTrackerView: View {
    @State private var habits: [String] = ["Drink water", "Walk 10 minutes"]
    @State private var newHabit: String = ""

    var body: some View {
        NavigationView {
            VStack(spacing: 12) {
                HStack {
                    TextField("What habit do you want to build?", text: $newHabit, onCommit: {
                        submitHabit()
                    })
                    .textFieldStyle(RoundedBorderTextFieldStyle())

                    Button(action: submitHabit) {
                        Image(systemName: "plus.circle.fill")
                            .font(.title2)
                            .foregroundColor(.accentColor)
                    }
                    .disabled(newHabit.trimmingCharacters(in: .whitespaces).isEmpty)
                }

                List {
                    ForEach(habits.indices, id: \.self) { idx in
                        Text(habits[idx])
                    }
                    .onDelete(perform: delete)
                }
            }
            .padding()
            .navigationTitle("Habits")
        }
    }

    private func submitHabit() {
        let trimmed = newHabit.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else { return }
        habits.append(trimmed)
        newHabit = ""
        hideKeyboard()
    }

    private func delete(at offsets: IndexSet) {
        habits.remove(atOffsets: offsets)
    }
}

#if DEBUG
struct HabitTrackerView_Previews: PreviewProvider {
    static var previews: some View {
        HabitTrackerView()
    }
}
#endif

fileprivate func hideKeyboard() {
    UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
}
