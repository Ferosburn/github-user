package com.ferosburn.githubuser.ui.user_detail.follows

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferosburn.githubuser.data.Result
import com.ferosburn.githubuser.data.UserRepository
import com.ferosburn.githubuser.data.local.entity.UserEntity
import com.ferosburn.githubuser.utils.Event
import kotlinx.coroutines.launch

class FollowsViewModel(private val userRepository: UserRepository) : ViewModel() {
    private var _userList = MutableLiveData<List<UserEntity>>()
    val userList: LiveData<List<UserEntity>> = _userList

    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var _hasLoaded = MutableLiveData(Event(false))
    val hasLoaded: LiveData<Event<Boolean>> = _hasLoaded

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage

    fun getFollows(
        username: String,
        followType: String
    ) {
        viewModelScope.launch {
            userRepository.getFollow(username, followType).collect { result ->
                when (result) {
                    is Result.Loading -> _isLoading.value = true
                    is Result.Error -> {
                        _isLoading.value = false
                        _errorMessage.value = Event(result.error)
                    }

                    is Result.Success -> {
                        _isLoading.value = false
                        result.data.also { _userList.value = it }
                    }
                }
            }
        }
    }
}