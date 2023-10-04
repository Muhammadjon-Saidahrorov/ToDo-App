package uz.gita.todoappmn.ui.screens.task

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import uz.gita.todoappmn.data.models.Priority
import uz.gita.todoappmn.data.models.ToDoTask
import uz.gita.todoappmn.ui.viewmodels.SharedViewModel
import uz.gita.todoappmn.utils.Action
import uz.gita.todoappmn.workmanager.setWork
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun TaskScreen(
    selectedTask: ToDoTask?,
    sharedViewModel: SharedViewModel,
    navigateToListScreen: (Action) -> Unit
) {
    val title: String by sharedViewModel.title
    val description: String by sharedViewModel.description
    val priority: Priority by sharedViewModel.priority
    val date: String by sharedViewModel.date
    val time: String by sharedViewModel.time
    val workId: UUID by sharedViewModel.workId

    val context = LocalContext.current

    BackHandler { navigateToListScreen(Action.NO_ACTION) }

    Scaffold(
        topBar = {
            TaskAppBar(
                selectedTask = selectedTask,
                navigateToListScreen = { action ->
                    if (action == Action.NO_ACTION)
                        navigateToListScreen(action)
                    else {
                        if (sharedViewModel.validateFields())
                            navigateToListScreen(action)
                        else toast(context = context)
                    }
                }
            )
        },
        content = {
            TaskContent(
                title = title,
                onTitleChange = {
                    sharedViewModel.updateTitle(it)
                },
                description = description,
                onDescriptionChange = {
                    sharedViewModel.description.value = it
                },
                priority = priority,
                onPrioritySelected = {
                    sharedViewModel.priority.value = it
                },
                date = date,
                onDateChange = {
                    sharedViewModel.date.value = it
                },
                time = time,
                onTimeChange = {
                    sharedViewModel.time.value = it
                }
            )
        }
    )
}

fun toast(context: Context) {
    Toast.makeText(context, "Fields Empty.", Toast.LENGTH_SHORT).show()
}