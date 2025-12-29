package com.relax.sounds

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
            LaunchedEffect(Unit) { delay(3000); showSplash = false }

            MaterialTheme {
                Crossfade(targetState = showSplash) { isSplash ->
                    if (isSplash) SplashScreen() else MainPlayerScreen(trackList)
                }
            }
        }
    }

    @Composable
    fun SplashScreen() {
        val infiniteTransition = rememberInfiniteTransition()
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = TweenSpec(durationMillis = 1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFF185A9D)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Nature Relax",
                    color = Color.White,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.alpha(alpha)
                )
                Spacer(modifier = Modifier.height(200.dp))
                Text(
                    "Developed by HsH. © Copyright",
                    color = Color.White.copy(0.5f),
                    fontSize = 12.sp
                )
            }
        }
    }

    @Composable
    fun MainPlayerScreen(tracks: List<Track>) {
        var currentIndex by remember { mutableStateOf(0) }
        var isPlaying by remember { mutableStateOf(false) }
        var currentPos by remember { mutableStateOf(0) }
        var duration by remember { mutableStateOf(1) }

        val bgGradient = Brush.verticalGradient(
            colors = listOf(Color(0xFF43CEA2), Color(0xFF185A9D), Color(0xFF6A11CB))
        )

        // تابع کمکی برای تعویض آهنگ
        fun playTrack(index: Int) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            currentIndex = (index + tracks.size) % tracks.size
            mediaPlayer = MediaPlayer.create(this@MainActivity, tracks[currentIndex].resId)
            mediaPlayer?.isLooping = true
            duration = mediaPlayer?.duration ?: 1
            mediaPlayer?.start()
            isPlaying = true
        }

        LaunchedEffect(isPlaying, currentIndex) {
            while (isPlaying) {
                currentPos = mediaPlayer?.currentPosition ?: 0
                delay(1000)
            }
        }

        Box(modifier = Modifier.fillMaxSize().background(bgGradient)) {
            // نوار زمان عمودی سمت راست
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 100.dp, end = 15.dp)
                    .width(6.dp)
                    .height(200.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(0.2f))
            ) {
                val progressHeight = (currentPos.toFloat() / duration.toFloat()) * 200f
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(progressHeight.dp)
                        .background(Color(0xFFFFB347))
                )
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(50.dp))
                
                Box(modifier = Modifier.size(70.dp).clip(CircleShape).background(Color.White.copy(0.2f)), contentAlignment = Alignment.Center) {
                    Text("🌿", fontSize = 35.sp)
                }
                Text("Logoor", color = Color.White, fontSize = 16.sp)

                Spacer(modifier = Modifier.height(30.dp))

                Column(modifier = Modifier.fillMaxWidth().padding(start = 20.dp)) {
                    Text("Relax.", color = Color(0xFFFFB347), fontSize = 42.sp, fontWeight = FontWeight.Bold)
                    Text("Breathe.", color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Bold)
                    Text("Listen.", color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(40.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    itemsIndexed(tracks) { index, track ->
                        Surface(
                            onClick = { playTrack(index) },
                            color = if (currentIndex == index) Color.White.copy(0.4f) else Color.White.copy(0.1f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(track.name, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))

                // کنترلر با افکت ضربه (Ripple)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .clip(RoundedCornerShape(45.dp))
                        .background(Color.White.copy(0.15f))
                        .border(1.dp, Color.White.copy(0.2f), RoundedCornerShape(45.dp)),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // دکمه عقب
                    Box(modifier = Modifier.size(60.dp).clip(CircleShape).clickable { playTrack(currentIndex - 1) }, contentAlignment = Alignment.Center) {
                        Text("⏮", color = Color.White, fontSize = 30.sp)
                    }

                    // دکمه پخش/توقف ساده
                    Box(modifier = Modifier.size(70.dp).clip(CircleShape).clickable {
                        if (isPlaying) mediaPlayer?.pause() else {
                            if (mediaPlayer == null) playTrack(currentIndex) else mediaPlayer?.start()
                        }
                        isPlaying = !isPlaying
                    }, contentAlignment = Alignment.Center) {
                        Text(if (isPlaying) "⏸" else "▶", color = Color.White, fontSize = 45.sp)
                    }

                    // دکمه جلو
                    Box(modifier = Modifier.size(60.dp).clip(CircleShape).clickable { playTrack(currentIndex + 1) }, contentAlignment = Alignment.Center) {
                        Text("⏭", color = Color.White, fontSize = 30.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text("Now Playing: ${tracks[currentIndex].name}", color = Color.White)
                
                Spacer(modifier = Modifier.weight(1f))
                Text("Developed by HsH. © Copyright", modifier = Modifier.padding(bottom = 20.dp), color = Color.White.copy(0.4f), fontSize = 12.sp)
            }
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
