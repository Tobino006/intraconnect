package sk.tobino.intraconnect.data.remote.supabase

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import sk.tobino.intraconnect.data.model.DepartmentDto

class DepartmentRepository (
    private val client: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun getDepartment(departmentId: String) : DepartmentDto? {
        Log.d("DepartmentRepository", "Querying department for id=$departmentId")

        val result = client.postgrest["department"].select {
            filter {
                eq("id", departmentId)
            }
        }

        // rovnaký prístup ako v NotificationRepository
        val departments = result.decodeList<DepartmentDto>()
        Log.d(
            "DepartmentRepository",
            "getDepartment: rows.size=${departments.size}, rows=${departments.map { it.id }}"
        )

        val department = departments.firstOrNull()
        if (department != null) {
            Log.d("DepartmentRepository", "Loaded department ${department.name}")
        } else {
            Log.e("DepartmentRepository", "No department found for id=$departmentId")
        }
        return department
    }

    suspend fun getDepartmentName(departmentId: String) : String? {
        val department = getDepartment(departmentId)
        return department?.name
    }
}