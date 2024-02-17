package com.ferosburn.githubuser.ui.search_user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ferosburn.githubuser.data.Result
import com.ferosburn.githubuser.data.UserRepository
import com.ferosburn.githubuser.data.local.entity.UserEntity
import com.ferosburn.githubuser.utils.Event
import kotlinx.coroutines.launch

class SearchUserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private var _users = MutableLiveData<List<UserEntity>?>()
    val users: MutableLiveData<List<UserEntity>?> = _users

    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage

    init {
        searchUser()
    }

    fun searchUser(query: String = "dicoding") {
        viewModelScope.launch {
            userRepository.searchUsers(query).collect { result ->
                when (result) {
                    is Result.Loading -> _isLoading.value = true
                    is Result.Success -> {
                        _isLoading.value = false
                        _users.value = result.data
                    }

                    is Result.Error -> {
                        _isLoading.value = false
                        _errorMessage.value = Event(result.error)
                    }
                }
            }
        }
    }

    fun getThemeSetting(): LiveData<Boolean> = userRepository.getThemeSetting().asLiveData()
}