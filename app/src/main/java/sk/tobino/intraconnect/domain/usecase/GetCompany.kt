package sk.tobino.intraconnect.domain.usecase

import sk.tobino.intraconnect.data.remote.supabase.CompanyRepository

class GetCompany(private val repo: CompanyRepository) {
    suspend operator fun invoke(companyId: String) = repo.getCompany(companyId)
}