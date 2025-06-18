package org.danielramzani.HealthCompose

import Widgets.MeditationWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class MeditationWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = MeditationWidget()
}