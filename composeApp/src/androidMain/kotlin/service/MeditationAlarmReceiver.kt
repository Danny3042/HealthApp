package org.danielramzani.HealthCompose

import Widgets.COUNTDOWN_KEY
import Widgets.MeditationWidget
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MeditationAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        CoroutineScope(Dispatchers.IO).launch {
            val glanceManager = GlanceAppWidgetManager(context)
            val glanceIds = glanceManager.getGlanceIds(MeditationWidget::class.java)
            for (glanceId in glanceIds) {
                updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
                    val key = intPreferencesKey(COUNTDOWN_KEY)
                    val current = prefs[key] ?: 60
                    val mutablePrefs = prefs.toMutablePreferences()
                    if (current > 0) {
                        mutablePrefs[key] = current - 1
                    }
                    mutablePrefs // <-- return the updated preferences
                }
                MeditationWidget().update(context, glanceId)
            }
        }
    }
}