package uz.gita.todoappmn.ui.screens.task

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockSelection
import uz.gita.todoappmn.components.PriorityDropDown
import uz.gita.todoappmn.data.models.Priority
import uz.gita.todoappmn.ui.theme.MEDIUM_PADDING
import uz.gita.todoappmn.ui.theme.SMALL_PADDING
import uz.gita.todoappmn.R
import uz.gita.todoappmn.ui.theme.splashScreenBackground
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskContent(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    priority: Priority,
    onPrioritySelected: (Priority) -> Unit,
    date: String,
    onDateChange: (String) -> Unit,
    time: String,
    onTimeChange: (String) -> Unit,
) {

    val calendarState = rememberSheetState()

    CalendarDialog(state = calendarState,
        config = CalendarConfig(monthSelection = true, yearSelection = true),
        selection = CalendarSelection.Date { dateCalendar ->
            onDateChange(dateCalendar.toString())
        })


    val clockState = rememberSheetState()


    ClockDialog(state = clockState, selection = ClockSelection.HoursMinutes { hours, minutes ->
        if (hours.toString().length == 1 && minutes.toString().length == 1) {
            onTimeChange("0$hours:0$minutes")
        } else if (minutes.toString().length == 1) {
            onTimeChange("$hours:0$minutes")
        } else if (hours.toString().length == 1) {
            onTimeChange("0$hours:$minutes")
        } else {
            onTimeChange("$hours:$minutes")
        }

    })


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(all = MEDIUM_PADDING)
    ) {

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = title,
            onValueChange = { onTitleChange(it) },
            label = { Text(text = stringResource(id = R.string.title)) },
            textStyle = MaterialTheme.typography.body1,
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colors.splashScreenBackground,
                cursorColor = MaterialTheme.colors.splashScreenBackground,
                focusedLabelColor = MaterialTheme.colors.splashScreenBackground,
            )
        )

        Divider(
            modifier = Modifier.height(SMALL_PADDING),
            color = MaterialTheme.colors.background
        )

        PriorityDropDown(
            priority = priority,
            onPrioritySelected = onPrioritySelected
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { calendarState.show() }, modifier = Modifier
                    .weight(1f)
            )
            {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        tint = MaterialTheme.colors.splashScreenBackground,
                        painter = painterResource(id = R.drawable.calendar),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = date, color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.width(2.dp))

            OutlinedButton(
                onClick = { clockState.show() }, modifier = Modifier
                    .weight(1f)
            )
            {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        tint = MaterialTheme.colors.splashScreenBackground,
                        painter = painterResource(id = R.drawable.clock),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = time, color = Color.Black)
                }
            }
        }

        OutlinedTextField(
            modifier = Modifier.fillMaxSize(),
            value = description,
            onValueChange = { onDescriptionChange(it) },
            label = { Text(text = stringResource(id = R.string.description)) },
            textStyle = MaterialTheme.typography.body1,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colors.splashScreenBackground,
                cursorColor = MaterialTheme.colors.splashScreenBackground,
                focusedLabelColor = MaterialTheme.colors.splashScreenBackground,
            )
        )

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Preview
private fun TaskContentPreview() {
//    TaskContent(
//        title = "",
//        onTitleChange = {},
//        description = "",
//        onDescriptionChange = {},
//        priority = Priority.MEDIUM,
//        onPrioritySelected = {},
//
//    )
}