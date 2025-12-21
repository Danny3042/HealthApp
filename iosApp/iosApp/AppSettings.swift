import Foundation
import SwiftUI

final class AppSettings: ObservableObject {
    @Published var devMode: Bool = false
    @Published var showSnackbar: Bool = false
    @Published var snackbarMessage: String = ""

    func enableDevMode(_ enabled: Bool, message: String? = nil) {
        devMode = enabled
        if let msg = message {
            snackbarMessage = msg
            showSnackbar = true
            DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
                self.showSnackbar = false
            }
        }
    }
}

