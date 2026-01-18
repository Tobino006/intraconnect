package sk.tobino.intraconnect.data.remote.supabase

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import sk.tobino.intraconnect.data.model.CompanyDto

class CompanyRepository(private val client: SupabaseClient = SupabaseClientProvider.client) {
    suspend fun getCompany(companyId: String): CompanyDto {
        val result = client.postgrest["company"].select {
            filter {
                eq("id", companyId)
            }
        }

        Log.d("CompanyRepository", "Loaded company ${result.decodeSingle<CompanyDto>().name}")
        return result.decodeSingle<CompanyDto>()
    }

    suspend fun getCompanyName(companyId: String) : String {
        val company = getCompany(companyId)
        return company.name
    }
}