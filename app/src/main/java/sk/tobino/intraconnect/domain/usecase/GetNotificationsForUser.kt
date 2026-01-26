package sk.tobino.intraconnect.domain.usecase

import sk.tobino.intraconnect.data.model.NotificationDto
import sk.tobino.intraconnect.data.remote.supabase.NotificationRepository

class GetNotificationsForUser(private val repo: NotificationRepository) {
    suspend operator fun invoke (
        companyId: String,
        departmentId: String?,
        limit: Int,
        offset: Int
    ) : List<NotificationDto> {
        val global = repo.getGlobal(companyId)
        val department = if (departmentId != null) repo.getForDepartment(departmentId) else emptyList()

        val merged = (global + department)
            .distinctBy { it.id }
            .sortedByDescending { it.publishedAt }

        return merged
            .drop(offset)
            .take(limit)

    }
}