package com.ferosburn.githubuser.ui.favorite_users

import androidx.lifecycle.ViewModel
import com.ferosburn.githubuser.data.UserRepository

class FavoriteUsersViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun getFavoriteUsers() = userRepository.getFavoriteUsers()

}