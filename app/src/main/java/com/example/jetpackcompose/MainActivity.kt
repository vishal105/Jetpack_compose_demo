package com.example.jetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.offset
import com.example.jetpackcompose.ui.theme.JetpackComposeTheme

@Composable
private fun AnimatedButtonVisibility(
    visible: Boolean,
    fromRight: Boolean = true,
    offsetFraction: Float = 1/3f,
    durationMillis: Int = 300,
    enterAnimationSpec: androidx.compose.animation.core.FiniteAnimationSpec<androidx.compose.ui.unit.IntOffset> = tween(durationMillis = durationMillis),
    exitAnimationSpec: androidx.compose.animation.core.FiniteAnimationSpec<androidx.compose.ui.unit.IntOffset> = tween(durationMillis = durationMillis),
    content: @Composable () -> Unit
) {
    val offsetMultiplier = if (fromRight) 1 else -1
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            initialOffsetX = { fullWidth -> (fullWidth * offsetFraction).toInt() * offsetMultiplier },
            animationSpec = enterAnimationSpec
        ) + fadeIn(animationSpec = tween(durationMillis = durationMillis)),
        exit = slideOutHorizontally(
            targetOffsetX = { fullWidth -> (fullWidth * offsetFraction).toInt() * offsetMultiplier },
            animationSpec = exitAnimationSpec
        ) + fadeOut(animationSpec = tween(durationMillis = durationMillis))
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
                        var isExpanded by remember { mutableStateOf(false) }
                        
                        // Play button with animation
                        val playButtonOffset by animateDpAsState(
                            targetValue = if (isExpanded) (-40).dp else 0.dp,
                            animationSpec = tween(durationMillis = 300)
                        )
                        
                        // Call button with animation - only create when expanded
                        val callButtonOffset by animateDpAsState(
                            targetValue = if (isExpanded) 40.dp else 0.dp,
                            animationSpec = tween(durationMillis = 300)
                        )
                        
                        // Play button
                        PlayButton(
                            onPlayStateChanged = { isExpanded = it },
                            modifier = Modifier.offset(x = playButtonOffset)
                        )
                        
                        // Call button with animation
                        AnimatedButtonVisibility(
                            visible = isExpanded,
                            fromRight = true,
                            durationMillis = 150
                        ) {
                            DialerButton(modifier = Modifier.offset(x = callButtonOffset))
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
        modifier = modifier,
        icon = {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Warning else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play"
            )
        }
    )
    return isPlaying
}

@Composable
fun DialerButton(modifier: Modifier = Modifier) {
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

@Preview(showBackground = true)
@Composable
fun ButtonsPreview() {
    JetpackComposeTheme {
        Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
            var isExpanded by remember { mutableStateOf(false) }
            
            Row(
                modifier = Modifier.animateContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlayButton(
                    onPlayStateChanged = { isExpanded = it }
                )
                
                AnimatedButtonVisibility(
                    visible = isExpanded,
                    fromRight = false
                ) {
                    Spacer(modifier = Modifier.width(16.dp))
                    DialerButton()
                }
            }
        }
    }
}