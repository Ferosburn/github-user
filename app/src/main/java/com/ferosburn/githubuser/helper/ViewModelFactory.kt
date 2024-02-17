package com.ferosburn.githubuser.helper

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ferosburn.githubuser.data.UserRepository
import com.ferosburn.githubuser.di.Injection
import com.ferosburn.githubuser.ui.favorite_users.FavoriteUsersViewModel
import com.ferosburn.githubuser.ui.search_user.SearchUserViewModel
import com.ferosburn.githubuser.ui.settings.SettingsViewModel
import com.ferosburn.githubuser.ui.user_detail.UserDetailViewModel
import com.ferosburn.githubuser.ui.user_detail.follows.FollowsViewModel
import kotlin.concurrent.Volatile

class ViewModelFactory private constructor(private val userRepository: UserRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(SearchUserViewModel::class.java) -> return SearchUserViewModel(userRepository) as T
            modelClass.isAssignableFrom(UserDetailViewModel::class.java) -> return UserDetailViewModel(userRepository) as T
            modelClass.isAssignableFrom(FollowsViewModel::class.java) -> return FollowsViewModel(userRepository) as T
            modelClass.isAssignableFrom(FavoriteUsersViewModel::class.java) -> return FavoriteUsersViewModel(userRepository) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> return SettingsViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory = instance ?: synchronized(this) {
            instance ?: ViewModelFactory(Injection.provideRepository(context))
        }.also { instance = it }
    }
}