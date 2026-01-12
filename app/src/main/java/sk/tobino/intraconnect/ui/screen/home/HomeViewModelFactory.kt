package sk.tobino.intraconnect.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sk.tobino.intraconnect.data.remote.supabase.CompanyRepository
import sk.tobino.intraconnect.data.remote.supabase.NotificationRepository
import sk.tobino.intraconnect.domain.usecase.GetCompany
import sk.tobino.intraconnect.domain.usecase.GetNotificationsForUser

class HomeViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val companyRepo = CompanyRepository()
        val notificationRepo = NotificationRepository()
        val getCompany = GetCompany(companyRepo)
        val getNotifications = GetNotificationsForUser(notificationRepo)

        return HomeViewModel (
            getCompany = getCompany,
            getNotifications = getNotifications
        ) as T
    }

}