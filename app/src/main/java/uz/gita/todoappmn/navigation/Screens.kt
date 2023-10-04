package uz.gita.todoappmn.navigation

import androidx.navigation.NavHostController
import uz.gita.todoappmn.utils.Action
import uz.gita.todoappmn.utils.Constants.LIST_SCREEN
import uz.gita.todoappmn.utils.Constants.SPLASH_SCREEN

class Screens(navController: NavHostController) {
    val splash: () -> Unit = {
        navController.navigate(route = "list/${Action.NO_ACTION}") {
            popUpTo(SPLASH_SCREEN) { inclusive = true }
        }
    }

    val list: (Int) -> Unit = { taskId ->
        navController.navigate(route = "task/$taskId")
    }

    val task: (Action) -> Unit = { action ->
        navController.navigate(route = "list/${action.name}") {
            popUpTo(LIST_SCREEN) { inclusive = true }
        }
    }
}