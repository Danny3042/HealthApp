package keyboardUtil

import android.app.Activity
import android.app.Application
import android.os.Bundle

class AppActivityTracker : Application() {
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {
                CurrentActivityHolder.currentActivity = activity
            }

            override fun onActivityPaused(activity: Activity) {
                if (CurrentActivityHolder.currentActivity === activity) {
                    CurrentActivityHolder.currentActivity = null
                }
            }

            override fun onActivityStopped(activity: Activity) {
                if (CurrentActivityHolder.currentActivity === activity) {
                    CurrentActivityHolder.currentActivity = null
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {
                if (CurrentActivityHolder.currentActivity === activity) {
                    CurrentActivityHolder.currentActivity = null
                }
            }
        })
    }
}

