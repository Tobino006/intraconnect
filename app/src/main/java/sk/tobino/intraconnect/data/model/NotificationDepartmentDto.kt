package sk.tobino.intraconnect.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationDepartmentDto (
    @SerialName("notification_id")
    val notificationId: String,

    @SerialName("department_id")
    val departmentId: String
)