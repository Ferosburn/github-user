package com.ferosburn.githubuser.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.ferosburn.githubuser.data.local.datastore.SettingPreferences
import com.ferosburn.githubuser.data.local.entity.UserEntity
import com.ferosburn.githubuser.data.local.room.UserDao
import com.ferosburn.githubuser.data.remote.retrofit.ApiService
import com.ferosburn.githubuser.utils.userDataToUserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userDao: UserDao,
    private val settingPreferences: SettingPreferences,
) {

    fun searchUsers(query: String): Flow<Result<List<UserEntity>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getUsersSearch(query)
            val userList = response.items.map { user -> userDataToUserEntity(user) }
            userDao.deleteAll()
            userDao.insertUsers(userList)
        } catch (e: Exception) {
            Log.e("UserRepository", "searchUsers: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
        val localData: Flow<Result<List<UserEntity>>> =
            userDao.getUsers().map { Result.Success(it) }
        emitAll(localData)
    }

    fun getUser(username: String): Flow<Result<UserEntity>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getUser(username)
            val isFavorite = userDao.isUserFavorite(response.login)
            val id = userDao.getRoomId(username)
            val user = userDataToUserEntity(response, isFavorite, id)
            userDao.updateUser(user)
        } catch (e: Exception) {
            Log.e("UserRepository", "getUser: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
        val localData: Flow<Result<UserEntity>> =
            userDao.getUser(username).map { Result.Success(it) }
        emitAll(localData)
    }

    fun getFollow(username: String, followType: String): Flow<Result<List<UserEntity>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getFollow(username, followType)
            val userList = response.map { user -> userDataToUserEntity(user) }
            emit(Result.Success(userList))
        } catch (e: Exception) {
            Log.e("UserRepository", "getFollow: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getFavoriteUsers(): LiveData<List<UserEntity>> = userDao.getFavoriteUsers()

    suspend fun setFavoriteUser(user: UserEntity, favoriteState: Boolean) {
        user.isFavorite = favoriteState
        userDao.updateUser(user)
    }

    fun getThemeSetting(): Flow<Boolean> = settingPreferences.getThemeSetting()

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) =
        settingPreferences.saveThemeSetting(isDarkModeActive)

    companion object {
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            userDao: UserDao,
            settingPreferences: SettingPreferences
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userDao, settingPreferences)
            }.also { instance = it }
    }
}