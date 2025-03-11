package com.example.jetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.jetpackcompose.ui.theme.JetpackComposeTheme

@Composable
private fun AnimatedButtonVisibility(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandHorizontally(animationSpec = tween(300, easing = EaseInOut)) + fadeIn(animationSpec = tween(300)),
        exit = shrinkHorizontally(animationSpec = tween(300, easing = EaseInOut)) + fadeOut(animationSpec = tween(300))
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
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        var isDialingEnabled by remember { mutableStateOf(false) }
                        
                        // Center-aligned box to contain our animated buttons
                        Box(contentAlignment = Alignment.Center) {
                            // Play button with offset animation
                            var isPlaying by remember { mutableStateOf(false) }
                            
                            // Animate the offset of the play button
                            val playButtonOffset by animateDpAsState(
                                targetValue = if (isPlaying) (-36).dp else 0.dp,
                                animationSpec = tween(500, easing = EaseInOut),
                                label = "playButtonOffset"
                            )
                            
                            // Play button with animated offset
                            Box(modifier = Modifier.offset(x = playButtonOffset, y = 0.dp)) {
                                PlayButton(
                                    onPlayStateChanged = { 
                                        isDialingEnabled = it
                                        isPlaying = it
                                    }
                                )
                            }
                            
                            // Dialer button with animated visibility and offset
                            AnimatedVisibility(
                                visible = isPlaying,
                                enter = expandHorizontally(animationSpec = tween(500, easing = EaseInOut)) +
                                        fadeIn(animationSpec = tween(300)),
                                exit = shrinkHorizontally(animationSpec = tween(500, easing = EaseInOut)) +
                                        fadeOut(animationSpec = tween(300))
                            ) {
                                Box(modifier = Modifier.padding(start = 36.dp)) {
                                    DialerButton(isVisible = isDialingEnabled)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayButton(modifier: Modifier = Modifier, onPlayStateChanged: (Boolean) -> Unit = {}): Boolean {
    var isPlaying by remember { mutableStateOf(false) }
    
    CommonFloatingActionButton(
        onClick = {
            isPlaying = !isPlaying
            onPlayStateChanged(isPlaying)
        },
        icon = {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Warning else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play"
            )
        },
        modifier = modifier
    )
    return isPlaying
}

@Composable
fun DialerButton(isVisible: Boolean = false, modifier: Modifier = Modifier) {
    // Use AnimatedVisibility with custom enter/exit transitions for a smooth slide-in effect
    AnimatedVisibility(
        visible = isVisible,
        enter = expandHorizontally(animationSpec = tween(300, easing = EaseInOut)) + 
                fadeIn(animationSpec = tween(300)),
        exit = shrinkHorizontally(animationSpec = tween(300, easing = EaseInOut)) + 
               fadeOut(animationSpec = tween(300))
    ) {
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
            // Center-aligned box to contain our animated buttons
            Box(contentAlignment = Alignment.Center) {
                // Play button with offset animation
                var isPlaying by remember { mutableStateOf(false) }
                
                // Animate the offset of the play button
                val playButtonOffset by animateDpAsState(
                    targetValue = if (isPlaying) (-36).dp else 0.dp,
                    animationSpec = tween(500, easing = EaseInOut),
                    label = "playButtonOffset"
                )
                
                // Play button with animated offset
                Box(modifier = Modifier.offset(x = playButtonOffset, y = 0.dp)) {
                    PlayButton(
                        onPlayStateChanged = { 
                            isPlaying = it
                        }
                    )
                }
                
                // Dialer button with animated visibility and offset
                AnimatedVisibility(
                    visible = isPlaying,
                    enter = expandHorizontally(animationSpec = tween(500, easing = EaseInOut)) +
                            fadeIn(animationSpec = tween(300)),
                    exit = shrinkHorizontally(animationSpec = tween(500, easing = EaseInOut)) +
                            fadeOut(animationSpec = tween(300))
                ) {
                    Box(modifier = Modifier.padding(start = 36.dp)) {
                        DialerButton(isVisible = true)
                    }
                }
            }
        }
    }
}