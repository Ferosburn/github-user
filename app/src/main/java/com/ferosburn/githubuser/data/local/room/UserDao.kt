package com.ferosburn.githubuser.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ferosburn.githubuser.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Query("select * from users order by is_favorite, id asc")
    fun getUsers(): Flow<List<UserEntity>>

    @Query("select * from users where is_favorite = 1")
    fun getFavoriteUsers(): LiveData<List<UserEntity>>

    @Query("select * from users where username = :username")
    fun getUser(username: String): Flow<UserEntity>

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("delete from users where is_favorite = 0")
    suspend fun deleteAll()

    @Query("select exists(select * from users where username = :username and is_favorite = 1)")
    suspend fun isUserFavorite(username: String): Boolean

    @Query("select id from users where username = :username")
    suspend fun getRoomId(username: String): Int
}