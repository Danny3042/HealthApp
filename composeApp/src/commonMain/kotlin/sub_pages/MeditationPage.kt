package sub_pages

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mmk.kmpnotifier.notification.NotifierManager
import components.TimeEditDialog
import kotlinx.coroutines.delay

const val MEDITATION_PAGE_ROUTE = "meditation"

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MeditationPage(onBack: (() -> Unit)? = null) {
    var totalTime by remember { mutableStateOf(5 * 60) }
    var timeLeft by remember { mutableStateOf(totalTime) }
    val progress = timeLeft / totalTime.toFloat()
    var isRunning by remember { mutableStateOf(false) }
    val notifier = remember { NotifierManager.getLocalNotifier() }
    var showEditDialog by remember { mutableStateOf(false) }

    val primary = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
    val secondary = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)

    LaunchedEffect(isRunning) {
        while (isRunning && timeLeft > 0) {
            delay(1000)
            timeLeft -= 1
        }
        if (timeLeft == 0) {
            isRunning = false
            notifier.notify(
                title = "Meditation Timer Finished",
                body = "Your meditation session has ended. Take a moment to reflect.",
            )
        }
    }

    if (showEditDialog) {
        TimeEditDialog(
            initialMinutes = totalTime / 60,
            initialSeconds = totalTime % 60,
            onDismiss = { showEditDialog = false },
            onConfirm = { minutes, seconds ->
                totalTime = minutes * 60 + seconds
                timeLeft = totalTime
                showEditDialog = false
                isRunning = false
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Blurred gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(primary, secondary)
                        ),
                        size = size
                    )
                }
                .blur(40.dp)
        )
        // Foreground content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Back button row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onBack?.invoke() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Meditation Timer", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(180.dp),
                    strokeWidth = 8.dp
                )
                AnimatedContent(
                    targetState = timeLeft,
                    transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) }
                ) { remainingTime ->
                    Text(
                        text = "${(remainingTime / 60).toString().padStart(2, '0')}:${(remainingTime % 60).toString().padStart(2, '0')}",
                        style = MaterialTheme.typography.displayLarge
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedContent(
                    targetState = isRunning,
                    transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) }
                ) { running ->
                    FloatingActionButton(
                        onClick = {
                            if (running) {
                                isRunning = false
                            } else if (timeLeft > 0) {
                                isRunning = true
                            }
                        },
                        containerColor = if (running) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                        modifier = Modifier
                            .size(96.dp)
                            .indication(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = true, color = Color.White)
                            )
                    ) {
                        Icon(
                            imageVector = if (running) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = if (running) "Stop" else "Start",
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }
                FloatingActionButton(
                    onClick = {
                        isRunning = false
                        timeLeft = totalTime
                    },
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(96.dp)
                        .indication(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = true, color = MaterialTheme.colorScheme.primary)
                        ),
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        modifier = Modifier.size(56.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { showEditDialog = true }
            ) { Text("Edit") }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Take a deep breath and relax.\nFocus on your breathing.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
