package uz.gita.todoappmn.ui.viewmodels

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.gita.todoappmn.data.models.Priority
import uz.gita.todoappmn.data.models.ToDoTask
import uz.gita.todoappmn.data.repositories.DataStoreRepository
import uz.gita.todoappmn.data.repositories.ToDoRepository
import uz.gita.todoappmn.utils.Action
import uz.gita.todoappmn.utils.Constants.MAX_TITLE_LENGTH
import uz.gita.todoappmn.utils.RequestState
import uz.gita.todoappmn.utils.SearchAppBarState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import uz.gita.todoappmn.app.App
import uz.gita.todoappmn.workmanager.cancelWork
import uz.gita.todoappmn.workmanager.setWork
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val repository: ToDoRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    val action: MutableState<Action> = mutableStateOf(Action.NO_ACTION)

    val id: MutableState<Int> = mutableStateOf(0)
    val title: MutableState<String> = mutableStateOf("0")
    val description: MutableState<String> = mutableStateOf("0")
    val priority: MutableState<Priority> = mutableStateOf(Priority.LOW)
    var date: MutableState<String> =  mutableStateOf( "0")
    var time: MutableState<String> = mutableStateOf( "0")
    val workId: MutableState<UUID> = mutableStateOf( UUID.randomUUID())

    val searchAppBarState: MutableState<SearchAppBarState> =
        mutableStateOf(SearchAppBarState.CLOSED)

    val searchTextState: MutableState<String> = mutableStateOf("")

    private val _allTasks =
        MutableStateFlow<RequestState<List<ToDoTask>>>(RequestState.Idle)
    val allTasks: StateFlow<RequestState<List<ToDoTask>>> = _allTasks

    private val _searchTasks =
        MutableStateFlow<RequestState<List<ToDoTask>>>(RequestState.Idle)
    val searchedTasks: StateFlow<RequestState<List<ToDoTask>>> = _searchTasks

    private val _sortState =
        MutableStateFlow<RequestState<Priority>>(RequestState.Idle)
    val sortState: StateFlow<RequestState<Priority>> = _sortState

    private val _selectedTask: MutableStateFlow<ToDoTask?> = MutableStateFlow(null)
    val selectedTask: StateFlow<ToDoTask?> = _selectedTask

    val lowPriorityTasks: StateFlow<List<ToDoTask>> =
        repository.sortByLowPriority.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            emptyList()
        )

    val highPriorityTasks: StateFlow<List<ToDoTask>> =
        repository.sortByHighPriority.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            emptyList()
        )

    init {
        getAllTasks()
        readSortState()
    }

    private fun getAllTasks() {
        _allTasks.value = RequestState.Loading
        try {
            viewModelScope.launch {
                repository.getAllTasks.collect {
                    _allTasks.value = RequestState.Success(it)
                }
            }
        } catch (e: Exception) {
            _allTasks.value = RequestState.Error(e)
        }
    }

    fun getSelectedTask(taskId: Int) {
        viewModelScope.launch {
            repository.getSelectedTasks(taskId = taskId).collect { task ->
                _selectedTask.value = task
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addTask() {
        viewModelScope.launch(Dispatchers.IO) {
            val toDoTask = ToDoTask(
                title = title.value,
                description = description.value,
                priority = priority.value,
                date = date.value,
                time = time.value,
                workId = workId.value
            )
            repository.addTask(toDoTask = toDoTask)
            setWork(App.context, toDoTask)
        }
        searchAppBarState.value = SearchAppBarState.CLOSED
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateTask() {
        viewModelScope.launch(Dispatchers.IO) {
            val toDoTask = ToDoTask(
                id = id.value,
                title = title.value,
                description = description.value,
                priority = priority.value,
                date = date.value,
                time = time.value,
                workId = workId.value
            )
            repository.updateTask(toDoTask = toDoTask)
            setWork(App.context, toDoTask)
        }
    }

    fun updateTaskFields(selectedTask: ToDoTask?) {
        if (selectedTask != null) {
            id.value = selectedTask.id
            title.value = selectedTask.title
            description.value = selectedTask.description
            priority.value = selectedTask.priority
            date.value = selectedTask.date
            time.value = selectedTask.time
            workId.value = selectedTask.workId
        } else {
            id.value = 0
            title.value = ""
            description.value = ""
            priority.value = Priority.LOW
            date.value = ""
            time.value = ""
        }
    }

    fun updateTitle(newTitle: String) {
        if (newTitle.length < MAX_TITLE_LENGTH) title.value = newTitle
    }

    fun searchDatabase(searchQuery: String) {
        _searchTasks.value = RequestState.Loading
        try {
            viewModelScope.launch {
                repository.searchDatabase(searchQuery = "%$searchQuery%")
                    .collect { searchedTasks ->
                        _searchTasks.value = RequestState.Success(searchedTasks)
                    }
            }
        } catch (e: Exception) {
            _searchTasks.value = RequestState.Error(e)
        }
        searchAppBarState.value = SearchAppBarState.TRIGGERED
    }

    private fun readSortState() {
        _sortState.value = RequestState.Loading
        try {
            viewModelScope.launch {
                dataStoreRepository.readSortState
                    .map { Priority.valueOf(it) }
                    .collect {
                        _sortState.value = RequestState.Success(it)
                    }
            }
        } catch (e: Exception) {
            _sortState.value = RequestState.Error(e)
        }
    }

    fun persistSortState(priority: Priority) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.persistSortState(priority = priority)
        }
    }

    private fun deleteTask() {
        viewModelScope.launch(Dispatchers.IO) {
            val toDoTask = ToDoTask(
                id = id.value,
                title = title.value,
                description = description.value,
                priority = priority.value,
                date = date.value,
                time = time.value,
                workId = workId.value
            )
            repository.deleteTask(toDoTask = toDoTask)
        }
        cancelWork(App.context, workId.value)
    }

    private fun deleteAllTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllTasks()
        }
    }

    @SuppressLint("NewApi")
    fun handleDatabaseActions(action: Action) {
        when (action) {
            Action.ADD -> {
                addTask()
            }
            Action.UPDATE -> {
                updateTask()
            }
            Action.DELETE -> {
                deleteTask()
            }
            Action.DELETE_ALL -> {
                deleteAllTasks()
            }
            Action.UNDO -> {
                addTask()
            }
            else -> {}
        }
    }

    fun validateFields(): Boolean =
        title.value.isNotEmpty() && description.value.isNotEmpty() && date.value.isNotEmpty() && time.value.isNotEmpty()
}