package sk.tobino.intraconnect.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class UserDto (
    @SerialName("id")
    val id: String,

    @SerialName("company_id")
    val companyId: String,

    @SerialName("position")
    val position: String?,

    @SerialName("phone")
    val phone: String?,

    @SerialName("name")
    val name: String,

    @SerialName("department_id")
    val departmentId: String?,

    @SerialName("avatar_url")
    val avatarUrl: String? = null
)