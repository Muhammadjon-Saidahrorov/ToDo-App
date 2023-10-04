package uz.gita.todoappmn.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import uz.gita.todoappmn.utils.Constants.DATABASE_TABLE
import java.util.*

@Entity(tableName = DATABASE_TABLE)
data class ToDoTask(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val priority: Priority,
    val date: String,
    val time: String,
    val workId: UUID
)