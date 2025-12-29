package com.relax.sounds

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // اعمال تم متریال 3 برای جلوگیری از خطاهای گرافیکی
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isPlaying by remember { mutableStateOf(false) }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Nature Relax Sounds",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (isPlaying) {
                                    mediaPlayer?.pause()
                                } else {
                                    if (mediaPlayer == null) {
                                        // اجرای فایل صوتی از پوشه res/raw/rain.mp3
                                        try {
                                            mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.rain)
                                            mediaPlayer?.isLooping = true
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                    mediaPlayer?.start()
                                }
                                isPlaying = !isPlaying
                            },
                            modifier = Modifier.fillMaxWidth(0.7f)
                        ) {
                            Text(if (isPlaying) "Stop Rain" else "Play Rain Sound")
                        }
                    }
                }
            }
        }
    }

    // پاکسازی حافظه هنگام بستن برنامه برای جلوگیری از نشت حافظه
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
