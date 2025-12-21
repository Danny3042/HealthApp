# Native iOS Navigation Bar Setup with Compose Multiplatform

## Overview
Your SwiftUI ContentView now has native iOS NavigationStack with support for back buttons. The navigation state is bridged between SwiftUI and Compose using NotificationCenter.

## What's Already Done (Swift Side) ✅

1. **NavigationState Class** - Tracks current title and back button visibility
2. **Native NavigationStack** - Each tab has its own NavigationStack with:
   - Dynamic title that updates based on Compose navigation
   - Back button that appears when `canGoBack` is true
   - Proper dark mode support
3. **Notification Listeners** - Listens for `ComposeNavigationChanged` from Compose

## What You Need to Do (Kotlin/Compose Side) 📝

### Step 1: Create the Navigation Bridge

Add the `ComposeNavigationBridge.kt` file to your `composeApp/src/iosMain/` directory. This file contains:
- `SetupIOSNavigationBridge()` composable function
- Listeners for back button presses from SwiftUI
- Notification sending when navigation state changes

### Step 2: Use the Bridge in Your App

In your main Compose app (where you have your NavHost), add:

```kotlin
@Composable
fun App() {
    val navController = rememberNavController()
    
    // 🔑 Add this line to bridge navigation
    SetupIOSNavigationBridge(navController)
    
    NavHost(
        navController = navController,
        startDestination = "HomePage"
    ) {
        composable("HomePage") { HomeScreen(navController) }
        composable("HabitDetail/{id}") { HabitDetailScreen(navController) }
        // ... other routes
    }
}
```

### Step 3: Hide Compose TopAppBar (Optional)

Since you're using the native iOS navigation bar, you should hide the Compose TopAppBar on iOS:

```kotlin
@Composable
fun MyScreen(navController: NavController) {
    Scaffold(
        // On iOS, don't show topBar since native navigation handles it
        topBar = if (getPlatform().name != "iOS") {
            { TopAppBar(title = { Text("My Screen") }) }
        } else {
            {}
        }
    ) { paddingValues ->
        // Your content
    }
}
```

Or create a platform-specific composable:

```kotlin
// commonMain
@Composable
expect fun PlatformScaffold(
    content: @Composable (PaddingValues) -> Unit
)

// iosMain
@Composable
actual fun PlatformScaffold(
    content: @Composable (PaddingValues) -> Unit
) {
    // No TopAppBar - use native iOS navigation
    Box(modifier = Modifier.fillMaxSize()) {
        content(PaddingValues())
    }
}

// androidMain
@Composable
actual fun PlatformScaffold(
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = { /* Your Android TopAppBar */ }
    ) { paddingValues ->
        content(paddingValues)
    }
}
```

### Step 4: Map Route Names to Titles

In `ComposeNavigationBridge.kt`, update the `when` block to match your actual route names:

```kotlin
val title = when {
    currentRoute.startsWith("HomePage") -> "Home"
    currentRoute.startsWith("HabitCoachingPage") -> "Habits"
    currentRoute.startsWith("HabitDetail") -> "Habit Details"
    currentRoute.startsWith("CreateHabit") -> "Create Habit"
    currentRoute.startsWith("Settings") -> "Settings"
    // Add all your routes here
    else -> "Back"
}
```

## How It Works

1. **Tab Navigation**: When user switches tabs, SwiftUI updates the title and resets `canGoBack` to false
2. **Deep Navigation**: When Compose navigates deeper:
   - Compose sends a notification with new title and `canGoBack: true`
   - SwiftUI updates the navigation bar title
   - SwiftUI shows the back button
3. **Back Button**: When user taps the native back button:
   - SwiftUI sends `ComposeBackPressed` notification
   - Compose's `navController.popBackStack()` is called
   - Navigation state updates again

## Testing

1. Run your app
2. Navigate to a detail screen in Compose
3. You should see:
   - ✅ Native iOS navigation bar at top
   - ✅ Updated title for the current screen
   - ✅ Back button appears on the left
   - ✅ Tapping back button returns to previous screen
   - ✅ Dark mode works correctly

## Troubleshooting

**Back button doesn't appear:**
- Check that your Compose code is calling `SetupIOSNavigationBridge()`
- Verify the notification name is exactly `"ComposeNavigationChanged"`
- Add print statements to see if notifications are being sent

**Title doesn't update:**
- Make sure your route names in the `when` block match your actual routes
- Check that the navigation is actually changing in Compose

**Back button doesn't work:**
- Verify `ComposeBackPressed` notification listener is set up
- Check that `navController.previousBackStackEntry` is not null

## Alternative: Pure Compose Navigation

If you prefer to handle all navigation in Compose without the native iOS bar, simply remove the NavigationStack wrappers in ContentView.swift and use Compose's TopAppBar with your own back button handling.
