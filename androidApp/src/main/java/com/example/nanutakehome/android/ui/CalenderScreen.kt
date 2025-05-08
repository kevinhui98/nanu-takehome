package com.example.nanutakehome.android.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.example.nanutakehome.android.repository.FirestoreLogRepository


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen() {
    val context = LocalContext.current
    val today = remember { LocalDate.now() }
    var selectedDate by remember { mutableStateOf(today) }

    val currentMonth = remember { YearMonth.now() }
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value % 7 // Sunday = 0

    val days = List(firstDayOfWeek) { null } + (1..daysInMonth).map { currentMonth.atDay(it) }
    val logs = remember { mutableStateMapOf<LocalDate, String>() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " ${currentMonth.year}",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(300.dp)
        ) {

            items(days) { day ->
                val isToday = day == today
                val isSelected = day == selectedDate
                val backgroundColor = when {
                    isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    isToday -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                    else -> Color.Transparent
                }
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(4.dp)
                        .background(backgroundColor, shape = MaterialTheme.shapes.small) // ✅ apply color
                        .clickable(enabled = day != null) {
                            if (day != null) selectedDate = day
                        },
                    contentAlignment = Alignment.Center
                ){
                    if (day != null) {
                        Text(
                            text = "${day.dayOfMonth}",
                            fontWeight = when {
                                isSelected -> FontWeight.Bold
                                isToday -> FontWeight.Medium
                                else -> FontWeight.Normal
                            },
                            color = when {
                                isSelected -> MaterialTheme.colorScheme.primary
                                isToday -> MaterialTheme.colorScheme.tertiary
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Selected Date: $selectedDate", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))

        val logText = logs[selectedDate] ?: ""
        OutlinedTextField(
            value = logText,
            onValueChange = { logs[selectedDate] = it },
            label = { Text("Write your log...") },
            modifier = Modifier.fillMaxWidth()
        )
        if (logText.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Saved log: $logText", style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
                logs.filterValues { it.isNotBlank() }.forEach { (date, note) ->
                    FirestoreLogRepository.uploadLog(date, note) { success ->
                        if (success) {
                            Toast.makeText(context, "Log for $date submitted", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Failed to submit log for $date", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
        ) {
            Text(text = "Submit")
        }
    }

}
@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    MaterialTheme {
        CalendarScreen()
    }
}
