package com.relax.sounds

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

data class Track(val name: String, val resId: Int)

class MainActivity : ComponentActivity() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // لیست کامل ۱۰ آهنگ شما بر اساس پیام آخر
        val trackList = listOf(
            Track("Dark Heart", R.raw.dark_heart),
            Track("Sentimental", R.raw.sentimental),
            Track("Harmony", R.raw.harmony),
            Track("Careful", R.raw.careful),
            Track("Worlds", R.raw.worlds),
            Track("Pure Dream", R.raw.pure_dream),
            Track("For You", R.raw.for_you),
            Track("Thoughtful", R.raw.thoughtful),
            Track("Bread", R.raw.bread),
            Track("Enlivening", R.raw.enlivening)
        )

        setContent {
            var showSplash by remember { mutableStateOf(true) }
            LaunchedEffect(Unit) { delay(2000); showSplash = false }

            MaterialTheme {
                Crossfade(targetState = showSplash) { isSplash ->
                    if (isSplash) SplashScreen() else MainPlayerScreen(trackList)
                }
            }
        }
    }

    @Composable
    fun SplashScreen() {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF185A9D)), contentAlignment = Alignment.Center) {
            Text("Nature Relax", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        }
    }

    @Composable
    fun MainPlayerScreen(tracks: List<Track>) {
        var currentTrack by remember { mutableStateOf(tracks[0]) }
        var isPlaying by remember { mutableStateOf(false) }
        var currentPos by remember { mutableStateOf(0) }
        var sleepTimer by remember { mutableStateOf(0) }

        val bgGradient = Brush.verticalGradient(
            colors = listOf(Color(0xFF43CEA2), Color(0xFF185A9D), Color(0xFF6A11CB))
        )

        LaunchedEffect(isPlaying, currentTrack) {
            while (isPlaying) {
                currentPos = mediaPlayer?.currentPosition ?: 0
                delay(1000)
            }
        }

        // منطق تایمر خواب
        LaunchedEffect(sleepTimer) {
            if (sleepTimer > 0) {
                delay(sleepTimer * 60000L)
                if (isPlaying) {
                    mediaPlayer?.pause()
                    isPlaying = false
                    sleepTimer = 0
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().background(bgGradient).padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            
            // بخش لوگو (Logoor)
            Box(modifier = Modifier.size(70.dp).clip(CircleShape).background(Color.White.copy(0.2f)), contentAlignment = Alignment.Center) {
                Text("🌿", fontSize = 35.sp)
            }
            Text("Logoor", color = Color.White, fontSize = 16.sp)

            Spacer(modifier = Modifier.height(30.dp))

            // متن‌های استایل عکس
            Column(modifier = Modifier.fillMaxWidth().padding(start = 20.dp)) {
                Text("Relax.", color = Color(0xFFFFB347), fontSize = 42.sp, fontWeight = FontWeight.Bold)
                Text("Breathe.", color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Bold)
                Text("Listen.", color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(30.dp))

            // لیست افقی آهنگ‌ها (LazyRow)
            Text("Select Sound:", color = Color.White.copy(0.7f), fontSize = 14.sp, modifier = Modifier.align(Alignment.Start).padding(start = 20.dp))
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(horizontal = 10.dp)) {
                items(tracks) { track ->
                    Surface(
                        onClick = {
                            mediaPlayer?.stop()
                            mediaPlayer?.release()
                            mediaPlayer = MediaPlayer.create(this@MainActivity, track.resId)
                            mediaPlayer?.isLooping = true
                            currentTrack = track
                            mediaPlayer?.start()
                            isPlaying = true
                        },
                        color = if (currentTrack == track) Color.White.copy(0.4f) else Color.White.copy(0.1f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(track.name, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = Color.White, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // کنترلر شیشه‌ای اصلی (Play/Pause)
            Row(
                modifier = Modifier.fillMaxWidth().height(90.dp).clip(RoundedCornerShape(45.dp))
                    .background(Color.White.copy(0.15f)).border(1.dp, Color.White.copy(0.2f), RoundedCornerShape(45.dp)),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("⏮", color = Color.White, fontSize = 30.sp)
                IconButton(onClick = {
                    if (isPlaying) {
                        mediaPlayer?.pause()
                    } else {
                        if (mediaPlayer == null) {
                            mediaPlayer = MediaPlayer.create(this@MainActivity, currentTrack.resId)
                            mediaPlayer?.isLooping = true
                        }
                        mediaPlayer?.start()
                    }
                    isPlaying = !isPlaying
                }) {
                    Text(if (isPlaying) "⏸" else "▶️", color = Color.White, fontSize = 45.sp)
                }
                Text("⏭", color = Color.White, fontSize = 30.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))
            
            // وضعیت پخش و تایمر
            Text("Playing: ${currentTrack.name}", color = Color.White, fontWeight = FontWeight.Medium)
            Text("${formatTime(currentPos)} / Sleep Timer: ${if(sleepTimer > 0) "$sleepTimer min" else "Off"}", color = Color.White.copy(0.6f))

            // دکمه تنظیم تایمر خواب
            TextButton(onClick = { if (sleepTimer < 60) sleepTimer += 10 else sleepTimer = 0 }) {
                Text("Set Sleep Timer (+10m)", color = Color(0xFFFFB347))
            }

            Spacer(modifier = Modifier.weight(1f))
            Text("Developed by HsH. © Copyright", modifier = Modifier.padding(bottom = 20.dp), color = Color.White.copy(0.5f), fontSize = 12.sp)
        }
    }

    private fun formatTime(ms: Int): String {
        val sec = (ms / 1000) % 60
        val min = (ms / (1000 * 60)) % 60
        return String.format("%02d:%02d", min, sec)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}
