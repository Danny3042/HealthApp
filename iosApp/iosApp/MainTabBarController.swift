import UIKit

// Native UITabBarController that hosts a single shared Compose view controller and uses the tab bar
// for navigation events. Each tab is a placeholder view controller; the shared Compose controller
// is added as a child and fills the area above the tab bar.
class MainTabBarController: UITabBarController, UITabBarControllerDelegate {
    private let composeViewController: UIViewController = MainViewControllerKt.MainViewController()
    private let routes = ["HomePage", "HabitCoachingPage", "ChatScreen", "meditation", "profile"]

    override func viewDidLoad() {
        super.viewDidLoad()
        delegate = self

        // Embed the shared Compose view controller
        addChild(composeViewController)
        view.addSubview(composeViewController.view)
        composeViewController.view.translatesAutoresizingMaskIntoConstraints = false

        // Constrain the compose view to the container, leaving space for the tab bar
        NSLayoutConstraint.activate([
            composeViewController.view.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            composeViewController.view.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            composeViewController.view.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            composeViewController.view.bottomAnchor.constraint(equalTo: tabBar.topAnchor)
        ])

        composeViewController.didMove(toParent: self)

        // Ensure the compose view is behind the tab bar but above other content
        view.bringSubviewToFront(composeViewController.view)
        view.bringSubviewToFront(tabBar)

        // Create placeholder view controllers for the tab bar items.
        let items: [(String, String)] = [
            ("Home", "house"),
            ("Habits", "checkmark.circle"),
            ("Chat", "message"),
            ("Meditate", "person.crop.circle"),
            ("Profile", "person")
        ]

        var vcs: [UIViewController] = []
        for (index, item) in items.enumerated() {
            let vc = UIViewController()
            vc.view.backgroundColor = .systemBackground
            vc.tabBarItem = UITabBarItem(title: item.0, image: UIImage(systemName: item.1), tag: index)
            vcs.append(vc)
        }
        viewControllers = vcs

        // Start on the first tab
        selectedIndex = 0
        // Delay briefly to ensure Compose main VC is ready before requesting navigation
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.05) {
            self.requestNavigationForSelectedIndex()
        }
    }

    // UITabBarControllerDelegate
    func tabBarController(_ tabBarController: UITabBarController, didSelect viewController: UIViewController) {
        requestNavigationForSelectedIndex()
    }

    private func requestNavigationForSelectedIndex() {
        let tag = selectedIndex
        guard tag >= 0 && tag < routes.count else { return }
        let route = routes[tag]
        // Ask the native AuthManager shim to request navigation in Compose on the main thread
        // Send the navigation request immediately and retry shortly after to ensure Compose receives it
        func sendRequest() {
            AuthManager.shared.requestNavigateTo(route: route)
        }

        DispatchQueue.main.async {
            sendRequest()
            // Retry couple of times in case Compose isn't fully ready yet
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.05) { sendRequest() }
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) { sendRequest() }
        }
    }
}
