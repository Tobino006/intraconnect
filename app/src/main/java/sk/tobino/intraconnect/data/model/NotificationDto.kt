package sk.tobino.intraconnect.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class NotificationDto (
    @SerialName("id")
    val id: String,

    @SerialName("company_id")
    val companyId: String,

    @SerialName("title")
    val title: String,

    @SerialName("message")
    val message: String,

    @SerialName("published_at")
    val publishedAt: String,

    @SerialName("updated_at")
    val updatedAt: String?,

    @SerialName("created_by")
    val createdBy: String,

    @SerialName("is_global")
    val isGlobal: Boolean
)