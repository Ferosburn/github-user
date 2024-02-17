package com.ferosburn.githubuser.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ferosburn.githubuser.data.local.entity.UserEntity

@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
abstract class GithubUserDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        private var instance: GithubUserDatabase? = null
        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext, GithubUserDatabase::class.java, "github_user.db"
            ).build()
        }
    }
}