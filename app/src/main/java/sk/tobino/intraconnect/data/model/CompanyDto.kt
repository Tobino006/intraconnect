package sk.tobino.intraconnect.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class CompanyDto (
    @SerialName("id")
    val id: String,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("name")
    val name: String,

    @SerialName("logo_url")
    val logoUrl: String?,

    @SerialName("color_theme")
    val colorTheme: String?
)