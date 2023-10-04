package uz.gita.todoappmn.workmanager

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.*
import uz.gita.todoappmn.data.models.ToDoTask
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit

class MyWorker(val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    override fun doWork(): Result {

        Log.d("TTT", "doWork: ")
        val title = inputData.getString("title").toString()
        val desc = inputData.getString("description").toString()

        Notification(context).createNotification(title, desc)

        return Result.success()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun setWork(
    context: Context,
    toDoTask: ToDoTask
) {

    val userSelectedDateTime = Calendar.getInstance()

    val chosenYear = toDoTask.date.substring(0, 4).toInt()
    val chosenMonth = toDoTask.date.substring(5, 7).toInt()
    val chosenDay = toDoTask.date.substring(8).toInt()

    val chosenHour = toDoTask.time.substring(0, 2).toInt()
    val chosenMin = toDoTask.time.substring(3).toInt()

    userSelectedDateTime.set(
        chosenYear,
        chosenMonth,
        chosenDay,
        chosenHour,
        chosenMin
    )
    val todayDateTime = Calendar.getInstance()
    todayDateTime.set(
        LocalDateTime.now().year,
        LocalDate.now().monthValue,
        LocalDateTime.now().dayOfMonth,
        LocalDateTime.now().hour, LocalDateTime.now().minute
    )

    val delayInSeconds = (userSelectedDateTime.timeInMillis / 1000L) - (todayDateTime.timeInMillis / 1000L)-20L

    val request = OneTimeWorkRequestBuilder<MyWorker>()
        .setId(toDoTask.workId)
        .setInputData(workDataOf("title" to toDoTask.title, "description" to toDoTask.description))
        .setInitialDelay(delayInSeconds, TimeUnit.SECONDS)
        .build()

    WorkManager.getInstance(context)
        .enqueueUniqueWork(toDoTask.workId.toString(), ExistingWorkPolicy.REPLACE, request)
}

fun cancelWork(context: Context, workId: UUID) {
    WorkManager.getInstance(context).cancelWorkById(workId)
}