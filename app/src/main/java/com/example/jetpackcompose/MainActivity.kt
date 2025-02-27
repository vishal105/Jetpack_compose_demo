package com.example.jetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import com.example.jetpackcompose.ui.theme.JetpackComposeTheme

@Composable
private fun AnimatedButtonVisibility(
    visible: Boolean,
    fromRight: Boolean = true,
    content: @Composable () -> Unit
) {
    val offsetMultiplier = if (fromRight) 1 else -1
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            initialOffsetX = { fullWidth -> (fullWidth / 2) * offsetMultiplier },
            animationSpec = tween(delayMillis = 300)
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { fullWidth -> (fullWidth / 2) * offsetMultiplier },
            animationSpec = tween(delayMillis = 300)
        )
    ) {
        content()
    }
}

@Composable
private fun CommonFloatingActionButton(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        shape = CircleShape,
        modifier = modifier.size(56.dp)
    ) {
        icon()
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetpackComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val isPlaying = PlayButton()
                            if (isPlaying) {
                                Spacer(modifier = Modifier.width(16.dp))
                                DialerButton(isVisible = true)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayButton(modifier: Modifier = Modifier): Boolean {
    var isPlaying by remember { mutableStateOf(false) }
    
    AnimatedButtonVisibility(visible = true) {
        CommonFloatingActionButton(
            onClick = { isPlaying = !isPlaying },
            modifier = modifier,
            icon = {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Warning else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }
        )
    }
    return isPlaying
}

@Composable
fun DialerButton(isVisible: Boolean = false, modifier: Modifier = Modifier) {
    AnimatedButtonVisibility(visible = isVisible, fromRight = false) {
        CommonFloatingActionButton(
            onClick = { /* TODO: Handle dialer click */ },
            modifier = modifier,
            icon = {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Dial"
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ButtonsPreview() {
    JetpackComposeTheme {
        Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val isPlaying = PlayButton()
                Spacer(modifier = Modifier.width(16.dp))
                DialerButton(isVisible = isPlaying)
            }
        }
    }
}