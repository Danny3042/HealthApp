package Widgets

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import org.danielramzani.HealthCompose.MeditationAlarmReceiver
const val COUNTDOWN_KEY = "countdown_seconds"
private val Context.dataStore by preferencesDataStore(name = "glance_prefs")




class MeditationWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<androidx.datastore.preferences.core.Preferences>()
            val seconds = prefs[intPreferencesKey(COUNTDOWN_KEY)] ?: 60
            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        ColorProvider(day = Color(0xFFB3E5FC), night = Color(0xFF263238)),
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Meditation",
                        style = TextStyle(
                            color = ColorProvider(day = Color(0xFF01579B), night = Color(0xFFB3E5FC)),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        "Time left: $seconds s",
                        style = TextStyle(
                            color = ColorProvider(day = Color(0xFF0277BD), night = Color(0xFFB3E5FC)),
                            fontSize = 18.sp
                        )
                    )
                    Button(
                        text = "Start",
                        onClick = actionRunCallback<StartMeditationAction>(),
                        modifier = GlanceModifier
                            .padding(top = 12.dp)
                            .background(ColorProvider(day = Color(0xFF0288D1), night = Color(0xFF37474F))
                            )
                    )
                }
            }
        }
    }
}



class StartMeditationAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Update Glance state directly
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            prefs.toMutablePreferences().apply {
                this[intPreferencesKey(COUNTDOWN_KEY)] = 60
            }
        }
        MeditationWidget().update(context, glanceId)

        // Schedule alarm as before
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MeditationAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            1000L,
            pendingIntent
        )
    }
}


