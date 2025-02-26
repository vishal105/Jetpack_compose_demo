package com.example.jetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcompose.ui.theme.JetpackComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetpackComposeTheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                        PlayButton()
                    }
                }
            }
        }
    }
}

@Composable
fun PlayButton(modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = { /* TODO: Handle click */ },
        shape = CircleShape,
        modifier = modifier.size(56.dp)
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Play"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlayButtonPreview() {
    JetpackComposeTheme {
        Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
            PlayButton()
        }
    }
}