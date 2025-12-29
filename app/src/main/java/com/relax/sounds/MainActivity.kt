package com.relax.sounds

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.relax.sounds.R

class MainActivity : ComponentActivity() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isPlaying by remember { mutableStateOf(false) }
            
            Column(modifier = Modifier.fillMaxSize().padding(32.dp)) {
                Text("Relaxing Sounds", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(20.dp))
                
                Button(onClick = {
                    if (isPlaying) {
                        mediaPlayer?.pause()
                    } else {
                        if (mediaPlayer == null) {
                            // فایل rain.mp3 باید در پوشه res/raw باشد
                            mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.rain)
                            mediaPlayer?.isLooping = true
                        }
                        mediaPlayer?.start()
                    }
                    isPlaying = !isPlaying
                }) {
                    Text(if (isPlaying) "Stop Rain" else "Play Rain Sound")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}
