package com.example.singlealarm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EnhancedAlarmSetterScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedAlarmSetterScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Get current time as default
    val currentTime = Calendar.getInstance()
    var selectedHour by remember { mutableStateOf(currentTime.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableStateOf(currentTime.get(Calendar.MINUTE)) }
    var alarmMessage by remember { mutableStateOf("Wake up!") }
    var showTimePicker by remember { mutableStateOf(false) }
    var showMessageDialog by remember { mutableStateOf(false) }
    var isAlarmSet by remember { mutableStateOf(false) }
    var animateCard by remember { mutableStateOf(false) }

    // Animation states
    val cardScale = animateFloatAsState(
        targetValue = if (animateCard) 1.05f else 1f,
        animationSpec = tween(300)
    )

    LaunchedEffect(isAlarmSet) {
        if (isAlarmSet) {
            animateCard = true
            delay(300)
            animateCard = false
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        "Simple Alarm",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showTimePicker = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Set Alarm")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Clock display
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(cardScale.value),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.alarm),
                        contentDescription = "Alarm Clock",
                        modifier = Modifier
                            .size(48.dp)
                            .padding(bottom = 16.dp)
                    )

                    Text(
                        text = formatTime(selectedHour, selectedMinute),
                        style = MaterialTheme.typography.displayLarge,
                        fontSize = 64.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = alarmMessage,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )

                    AnimatedVisibility(
                        visible = isAlarmSet,
                        enter = fadeIn(animationSpec = tween(500)),
                        exit = fadeOut(animationSpec = tween(500))
                    ) {
                        Card(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Alarm set!",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { showTimePicker = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Set Time", fontSize = 16.sp)
                }

                Button(
                    onClick = { showMessageDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Text("Set Message", fontSize = 16.sp)
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        val success = setAlarm(context, selectedHour, selectedMinute, alarmMessage)
                        isAlarmSet = success
                        if (success) {
                            delay(3000)
                            isAlarmSet = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Set Alarm", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (showTimePicker) {
            EnhancedTimePickerDialog(
                onDismissRequest = { showTimePicker = false },
                onConfirm = { hour, minute ->
                    selectedHour = hour
                    selectedMinute = minute
                    showTimePicker = false
                },
                initialHour = selectedHour,
                initialMinute = selectedMinute
            )
        }

        if (showMessageDialog) {
            AlarmMessageDialog(
                onDismissRequest = { showMessageDialog = false },
                onConfirm = { message ->
                    alarmMessage = message
                    showMessageDialog = false
                },
                initialMessage = alarmMessage
            )
        }
    }
}

@Composable
fun EnhancedTimePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    initialHour: Int,
    initialMinute: Int
) {
    var selectedHour by remember { mutableStateOf(initialHour) }
    var selectedMinute by remember { mutableStateOf(initialMinute) }
    var period by remember { mutableStateOf(if (initialHour < 12) "AM" else "PM") }

    val displayHour = remember(selectedHour) {
        when {
            selectedHour == 0 -> 12  // 12 AM
            selectedHour > 12 -> selectedHour - 12  // PM hours
            else -> selectedHour  // AM hours or 12 PM
        }
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Set Alarm Time",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hour picker
                    EnhancedNumberPicker(
                        value = displayHour,
                        onValueChange = {
                            val newHour = when {
                                period == "AM" && it == 12 -> 0  // 12 AM = 0 in 24h
                                period == "AM" -> it
                                period == "PM" && it == 12 -> 12  // 12 PM = 12 in 24h
                                period == "PM" -> it + 12  // Other PM hours
                                else -> it
                            }
                            selectedHour = newHour
                        },
                        range = 1..12,
                        label = "Hour"
                    )

                    Text(
                        text = ":",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    // Minute picker
                    EnhancedNumberPicker(
                        value = selectedMinute,
                        onValueChange = { selectedMinute = it },
                        range = 0..59,
                        label = "Minute",
                        zeroPad = true
                    )

                    // AM/PM selector
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Button(
                            onClick = {
                                period = "AM"
                                if (selectedHour >= 12) {
                                    selectedHour -= 12
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (period == "AM")
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (period == "AM")
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.width(60.dp)
                        ) {
                            Text("AM")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                period = "PM"
                                if (selectedHour < 12) {
                                    selectedHour += 12
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (period == "PM")
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (period == "PM")
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.width(60.dp)
                        ) {
                            Text("PM")
                        }
                    }
                }

                // Preview of selected time
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text(
                        text = formatTimeWithPeriod(selectedHour, selectedMinute),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = { onConfirm(selectedHour, selectedMinute) },
                        modifier = Modifier.padding(start = 8.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedNumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    label: String,
    zeroPad: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        IconButton(
            onClick = {
                val newValue = if (value >= range.last) range.first else value + 1
                onValueChange(newValue)
            },
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowUp,
                contentDescription = "Increase",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Box(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .width(64.dp)
                .height(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color = MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (zeroPad) value.toString().padStart(2, '0') else value.toString(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        IconButton(
            onClick = {
                val newValue = if (value <= range.first) range.last else value - 1
                onValueChange(newValue)
            },
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "Decrease",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun AlarmMessageDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit,
    initialMessage: String
) {
    var message by remember { mutableStateOf(initialMessage) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Alarm Message",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Message") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = { onConfirm(message) },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

fun setAlarm(context: Context, hour: Int, minute: Int, message: String): Boolean {
    return try {
        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_HOUR, hour)
            putExtra(AlarmClock.EXTRA_MINUTES, minute)
            putExtra(AlarmClock.EXTRA_MESSAGE, message)
            // Skip UI for better user experience
            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
        }

        context.startActivity(intent)
        Toast.makeText(context, "Alarm set for ${formatTimeWithPeriod(hour, minute)}", Toast.LENGTH_SHORT).show()
        true
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to set alarm: ${e.message}", Toast.LENGTH_LONG).show()
        false
    }
}

fun formatTime(hour: Int, minute: Int): String {
    return String.format("%02d:%02d", hour, minute)
}

fun formatTimeWithPeriod(hour: Int, minute: Int): String {
    val period = if (hour < 12) "AM" else "PM"
    val displayHour = when {
        hour == 0 -> 12  // 12 AM
        hour > 12 -> hour - 12  // PM hours
        else -> hour  // AM hours or 12 PM
    }
    return String.format("%d:%02d %s", displayHour, minute, period)
}

@Preview(showBackground = true)
@Composable
fun AlarmAppPreview() {
    MaterialTheme {
        EnhancedAlarmSetterScreen()
    }
}