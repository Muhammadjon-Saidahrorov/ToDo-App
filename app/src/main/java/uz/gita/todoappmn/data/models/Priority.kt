package uz.gita.todoappmn.data.models

import androidx.compose.ui.graphics.Color
import uz.gita.todoappmn.ui.theme.HighPriorityColor
import uz.gita.todoappmn.ui.theme.LowPriorityColor
import uz.gita.todoappmn.ui.theme.MediumPriorityColor
import uz.gita.todoappmn.ui.theme.NonePriorityColor

enum class Priority(val color: Color) {
    HIGH(HighPriorityColor),
    MEDIUM(MediumPriorityColor),
    LOW(LowPriorityColor),
    NONE(NonePriorityColor)
}