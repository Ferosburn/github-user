package com.ferosburn.githubuser.ui.user_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferosburn.githubuser.data.Result
import com.ferosburn.githubuser.data.UserRepository
import com.ferosburn.githubuser.data.local.entity.UserEntity
import com.ferosburn.githubuser.utils.Event
import kotlinx.coroutines.launch

class UserDetailViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _user = MutableLiveData<UserEntity>()
    val user: LiveData<UserEntity> = _user

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage

    private val _hasLoaded = MutableLiveData(Event(false))
    val hasLoaded: LiveData<Event<Boolean>> = _hasLoaded

    fun getUserData(username: String) {
        viewModelScope.launch {
            userRepository.getUser(username).collect { result ->
                when (result) {
                    is Result.Loading -> _isLoading.value = true
                    is Result.Success -> {
                        _isLoading.value = false
                        result.data.also { _user.value = it }
                    }

                    is Result.Error -> {
                        _isLoading.value = false
                        _errorMessage.value = Event(result.error)
                    }
                }
            }
        }
    }

    fun setFavoriteUser(favoriteState: Boolean) {
        viewModelScope.launch {
            user.value?.let { userRepository.setFavoriteUser(it, favoriteState) }
        }
    }
}