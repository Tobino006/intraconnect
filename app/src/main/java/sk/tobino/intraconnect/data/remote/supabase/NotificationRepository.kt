package sk.tobino.intraconnect.data.remote.supabase

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import sk.tobino.intraconnect.data.model.NotificationDepartmentDto
import sk.tobino.intraconnect.data.model.NotificationDto

class NotificationRepository(
    private val client: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun getGlobal(companyId: String): List<NotificationDto> {
        val result = client.postgrest["notification"].select {
            filter {
                eq("company_id", companyId)
                eq("is_global", true)
            }
        }

        val list = result.decodeList<NotificationDto>()
        Log.d("NotificationRepository", "Loaded ${list.size} global notifications")
        return list
    }

    suspend fun getForDepartment(departmentId: String): List<NotificationDto> {
        // get all notifications for this department
        val depResult = client.postgrest["notification_department"].select {
            filter {
                eq("department_id", departmentId)
            }
        }

        val depRows = depResult.decodeList<NotificationDepartmentDto>()
        Log.d("NotificationRepository", "Loaded ${depRows.size} notifications for department $departmentId")

        if (depRows.isEmpty()) {
            return emptyList()
        }

        // get all notifications for those ids
        val notificationIds = depRows.map { it.notificationId }

        // get notifications
        val notifResult = client.postgrest["notification"].select {
            filter {
                isIn("id", notificationIds)
            }
        }

        val notifs = notifResult.decodeList<NotificationDto>()
        Log.d(
            "NotificationRepository", "getForDepartment: fetched notifications.size=${notifs.size}, notifIds=${notifs.map { it.id }}"
        )

        return notifResult.decodeList<NotificationDto>()
    }

    suspend fun getById(notificationId: String): NotificationDto? {
        Log.d("NotificationRepository", "Querying notification for id=$notificationId")

        return try {
            val result = client.postgrest["notification"].select {
                filter {
                    eq("id", notificationId)
                }
            }

            val notifications = result.decodeList<NotificationDto>()
            val notification = notifications.firstOrNull()
            Log.d("NotificationRepository", "Loaded notification ${notification?.id}")
            notification
        } catch (e: Exception) {
            Log.e("NotificationRepository", "Error loading notification $notificationId", e)
            null
        }
    }


    suspend fun getDepartmentsForNotification(notificationId: String): List<NotificationDepartmentDto> {
        Log.d("NotificationRepository", "Querying departments for notification $notificationId")

        val depResult = client.postgrest["notification_department"].select {
            filter {
                eq("notification_id", notificationId)
            }
        }

        val depRows = depResult.decodeList<NotificationDepartmentDto>()
        Log.d (
            "NotificationRepository",
            "getDepartmentsForNotification: rows.size=${depRows.size}, depIds=${depRows.map { it.departmentId}}"
        )
        return depRows
    }
}