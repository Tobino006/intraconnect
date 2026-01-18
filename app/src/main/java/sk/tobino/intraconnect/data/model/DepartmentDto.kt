package sk.tobino.intraconnect.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DepartmentDto (
    @SerialName("id")
    val id: String,

    @SerialName("company_id")
    val companyId: String,

    @SerialName("name")
    val name: String
)