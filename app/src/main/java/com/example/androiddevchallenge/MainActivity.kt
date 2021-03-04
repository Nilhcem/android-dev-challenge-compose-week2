/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androiddevchallenge.ui.theme.MyTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

// Start building your app here!
@Composable
fun MyApp(countdownViewModel: CountdownViewModel = viewModel()) {
    val context = LocalContext.current

    val isStarted by countdownViewModel.isStarted.observeAsState(false)
    val seconds by countdownViewModel.setupSeconds.observeAsState(initial = 0L)
    val progress by countdownViewModel.countdownProgress.observeAsState(1.0f)
    val label by countdownViewModel.countdownLabel.observeAsState(initial = "")

    MyScreen(
        isStarted = isStarted,
        seconds = if (seconds == 0L) "" else seconds.toString(),
        progress = progress,
        label = label,
        onSecondsChange = { countdownViewModel.setSeconds(it.toLongOrNull() ?: 0L) },
        onStartCountdown = {
            Toast.makeText(context, "Started!", Toast.LENGTH_SHORT).show()
            countdownViewModel.startCountdown()
        },
        onStopCountdown = {
            Toast.makeText(context, "Stopped!", Toast.LENGTH_SHORT).show()
            countdownViewModel.stopCountdown()
        }
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MyScreen(
    isStarted: Boolean,
    seconds: String,
    progress: Float,
    label: String,
    onSecondsChange: (String) -> Unit,
    onStartCountdown: () -> Unit,
    onStopCountdown: () -> Unit
) {
    Surface(color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(visible = isStarted) {
                Countdown(progress, label)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(visible = !isStarted) {
                    OutlinedTextField(
                        modifier = Modifier.requiredWidth(240.dp),
                        value = seconds,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions { onStartCountdown() },
                        onValueChange = onSecondsChange,
                        label = { Text("Seconds") }
                    )
                }

                Button(
                    modifier = Modifier.padding(vertical = 24.dp),
                    enabled = isStarted || !isStarted && seconds.isNotBlank(),
                    onClick = { if (isStarted) onStopCountdown() else onStartCountdown() }
                ) {
                    Text(text = if (isStarted) "STOP!" else "START!")
                }
            }
        }
    }
}

@Composable
fun Countdown(progress: Float, label: String) {
    Box(modifier = Modifier.aspectRatio(ratio = 1f), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.fillMaxSize(),
            progress = progress
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                style = MaterialTheme.typography.h4
            )
        }
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
