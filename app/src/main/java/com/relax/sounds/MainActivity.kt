package com.relax.sounds

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var showSplash by remember { mutableStateOf(true) }
            LaunchedEffect(Unit) {
                delay(2000)
                showSplash = false
            }

            MaterialTheme {
                Crossfade(targetState = showSplash) { isSplash ->
                    if (isSplash) SplashScreen() else MainPlayerScreen()
                }
            }
        }
    }

    @Composable
    fun SplashScreen() {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF121212)), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Nature Relax", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(color = Color(0xFF4CAF50))
            }
            Text("Created by HsH © 2025", modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp), color = Color.Gray, fontSize = 12.sp)
        }
    }

    @Composable
    fun MainPlayerScreen() {
        var isPlaying by remember { mutableStateOf(false) }
        var currentPosition by remember { mutableStateOf(0f) }
        var totalDuration by remember { mutableStateOf(1f) }
        var sleepTimerMinutes by remember { mutableStateOf(0) } // تایمر خواب

        // انیمیشن ضربان (Pulse)
        val infiniteTransition = rememberInfiniteTransition()
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = if (isPlaying) 1.15f else 1f,
            animationSpec = infiniteRepeatable(
                animation = TweenSpec(durationMillis = 1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        // آپدیت ثانیه‌شمار و مدیریت تایمر خواب
        LaunchedEffect(isPlaying) {
            while (isPlaying) {
                mediaPlayer?.let {
                    currentPosition = it.currentPosition.toFloat()
                    totalDuration = it.duration.toFloat()
                }
                delay(1000)
            }
        }

        // منطق شمارش معکوس تایمر خواب
        LaunchedEffect(sleepTimerMinutes) {
            if (sleepTimerMinutes > 0 && isPlaying) {
                while (sleepTimerMinutes > 0 && isPlaying) {
                    delay(60000) // هر یک دقیقه
                    sleepTimerMinutes -= 1
                    if (sleepTimerMinutes == 0) {
                        mediaPlayer?.pause()
                        isPlaying = false
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFF1E272E)).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // کارت نمایش با انیمیشن ضربان
            Card(
                modifier = Modifier.size(220.dp).scale(scale),
                shape = RoundedCornerShape(110.dp), // دایره‌ای کامل
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2F3640))
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(if (isPlaying) "🌧️" else "💤", fontSize = 70.sp)
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            // نمایش تایمر و نوار پیشرفت
            Text(formatTime(currentPosition.toInt()), color = Color.White, fontSize = 16.sp)
            LinearProgressIndicator(
                progress = currentPosition / totalDuration,
                modifier = Modifier.fillMaxWidth().height(6.dp).padding(horizontal = 40.dp),
                color = Color(0xFF4CAF50),
                trackColor = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(30.dp))

            // دکمه کنترل صدا
            Button(
                onClick = {
                    if (isPlaying) mediaPlayer?.pause() else {
                        if (mediaPlayer == null) {
                            mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.rain)
                            mediaPlayer?.isLooping = true
                        }
                        mediaPlayer?.start()
                    }
                    isPlaying = !isPlaying
                },
                modifier = Modifier.fillMaxWidth(0.5f).height(55.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text(if (isPlaying) "Stop" else "Play Rain")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // دکمه تایمر خواب
            OutlinedButton(
                onClick = { if (sleepTimerMinutes < 60) sleepTimerMinutes += 10 else sleepTimerMinutes = 0 },
                modifier = Modifier.fillMaxWidth(0.5f),
                shape = RoundedCornerShape(25.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color.Gray))
            ) {
                Text(
                    if (sleepTimerMinutes > 0) "Timer: $sleepTimerMinutes min" else "Set Sleep Timer",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Text("HsH Copyright Reserved", color = Color(0xFF57606f), fontSize = 10.sp)
        }
    }

    private fun formatTime(milliseconds: Int): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / (1000 * 60)) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
