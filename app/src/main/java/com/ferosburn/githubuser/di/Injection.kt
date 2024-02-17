package com.ferosburn.githubuser.di

import android.content.Context
import com.ferosburn.githubuser.data.UserRepository
import com.ferosburn.githubuser.data.local.datastore.SettingPreferences
import com.ferosburn.githubuser.data.local.datastore.datastore
import com.ferosburn.githubuser.data.local.room.GithubUserDatabase
import com.ferosburn.githubuser.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val apiService = ApiConfig.getApiService()
        val database = GithubUserDatabase.getInstance(context)
        val dao = database.userDao()
        val settingPreferences = SettingPreferences.getInstance(context.datastore)
        return UserRepository.getInstance(apiService, dao, settingPreferences)
    }
}