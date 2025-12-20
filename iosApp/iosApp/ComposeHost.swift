import UIKit
import ComposeApp

// Central holder for the single shared Compose UIViewController instance.
final class SharedComposeHolder {
    static let sharedVC: UIViewController = {
        // Create once on main thread
        if !Thread.isMainThread {
            var vc: UIViewController! = nil
            DispatchQueue.main.sync {
                vc = MainViewControllerKt.MainViewController()
                vc.view.backgroundColor = .clear
                vc.view.isOpaque = false
                print("SharedComposeHolder: created shared VC on main: \(vc)")
            }
            return vc
        } else {
            let vc = MainViewControllerKt.MainViewController()
            vc.view.backgroundColor = .clear
            vc.view.isOpaque = false
            print("SharedComposeHolder: created shared VC: \(vc)")
            return vc
        }
    }()
}

// Attacher that creates a dedicated window and hosts the shared Compose VC so it persists.
final class ComposeHostAttacher {
    static let shared = ComposeHostAttacher()
    private(set) var attached: Bool = false
    private weak var hostContainer: UIViewController? = nil

    func attachIfNeeded() {
        DispatchQueue.main.async {
            guard !self.attached else { return }
            guard let scene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
                  let window = scene.windows.first,
                  let root = window.rootViewController else {
                print("ComposeHostAttacher: cannot find root view controller to attach")
                return
            }

            let shared = SharedComposeHolder.sharedVC

            // Detach from any previous parent
            if let prev = shared.parent {
                shared.willMove(toParent: nil)
                shared.view.removeFromSuperview()
                shared.removeFromParent()
            }

            // Attach to the root view controller and insert at back so SwiftUI content overlays it
            root.addChild(shared)
            shared.view.translatesAutoresizingMaskIntoConstraints = false
            root.view.insertSubview(shared.view, at: 0)
            NSLayoutConstraint.activate([
                shared.view.topAnchor.constraint(equalTo: root.view.topAnchor),
                shared.view.bottomAnchor.constraint(equalTo: root.view.safeAreaLayoutGuide.bottomAnchor),
                shared.view.leadingAnchor.constraint(equalTo: root.view.leadingAnchor),
                shared.view.trailingAnchor.constraint(equalTo: root.view.trailingAnchor)
            ])
            shared.didMove(toParent: root)

            self.hostContainer = root
            self.attached = true
            print("ComposeHostAttacher: attached shared Compose VC to root view (index 0)")

            // DEBUG: add a small, removable overlay label so we can visually confirm attachment
            #if DEBUG
            let debugLabel = UILabel()
            debugLabel.text = "ComposeHost attached"
            debugLabel.font = UIFont.systemFont(ofSize: 10)
            debugLabel.textColor = UIColor.white
            debugLabel.backgroundColor = UIColor.black.withAlphaComponent(0.35)
            debugLabel.translatesAutoresizingMaskIntoConstraints = false
            root.view.addSubview(debugLabel)
            NSLayoutConstraint.activate([
                debugLabel.leadingAnchor.constraint(equalTo: root.view.leadingAnchor, constant: 8),
                debugLabel.topAnchor.constraint(equalTo: root.view.topAnchor, constant: 44)
            ])
            root.view.bringSubviewToFront(debugLabel)
            #endif

             // Monitor and reattach if detached
             DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                 self.monitorAndReattach(times: 30, interval: 0.15)
             }
         }
     }

    private func reattachIfNeeded() {
        DispatchQueue.main.async {
            guard let root = self.hostContainer ?? UIApplication.shared.connectedScenes.compactMap({ $0 as? UIWindowScene }).first?.windows.first?.rootViewController else { return }
            let shared = SharedComposeHolder.sharedVC
            if shared.view.superview == nil || shared.parent == nil {
                print("ComposeHostAttacher: reattaching shared VC to root view")
                if let prev = shared.parent {
                    shared.willMove(toParent: nil)
                    shared.view.removeFromSuperview()
                    shared.removeFromParent()
                }
                root.addChild(shared)
                shared.view.translatesAutoresizingMaskIntoConstraints = false
                root.view.insertSubview(shared.view, at: 0)
                NSLayoutConstraint.activate([
                    shared.view.topAnchor.constraint(equalTo: root.view.topAnchor),
                    shared.view.bottomAnchor.constraint(equalTo: root.view.safeAreaLayoutGuide.bottomAnchor),
                    shared.view.leadingAnchor.constraint(equalTo: root.view.leadingAnchor),
                    shared.view.trailingAnchor.constraint(equalTo: root.view.trailingAnchor)
                ])
                shared.didMove(toParent: root)
                print("ComposeHostAttacher: reattached shared VC to root view")
            }
        }
    }

    private func monitorAndReattach(times: Int, interval: TimeInterval) {
        guard times > 0 else { return }
        DispatchQueue.main.asyncAfter(deadline: .now() + interval) {
            let shared = SharedComposeHolder.sharedVC
            let hasSuperview = shared.view.superview != nil
            print("ComposeHostAttacher.monitor -> hasSuperview=\(hasSuperview) parent=\(String(describing: shared.parent))")
            if !hasSuperview {
                self.reattachIfNeeded()
            }
            self.monitorAndReattach(times: times - 1, interval: interval)
        }
    }
}
