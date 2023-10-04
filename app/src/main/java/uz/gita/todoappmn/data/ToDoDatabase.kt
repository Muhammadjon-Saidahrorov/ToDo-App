package uz.gita.todoappmn.data

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.gita.todoappmn.data.ToDoDao
import uz.gita.todoappmn.data.models.ToDoTask

@Database(entities = [ToDoTask::class], version = 1, exportSchema = false)
abstract class ToDoDatabase : RoomDatabase() {
    abstract fun toDoDao(): ToDoDao
}