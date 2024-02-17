package com.ferosburn.githubuser.data.remote.retrofit

import com.ferosburn.githubuser.data.remote.response.SearchUserResponse
import com.ferosburn.githubuser.data.remote.response.UserData
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("search/users")
    suspend fun getUsersSearch(
        @Query("q") username: String
    ): SearchUserResponse

    @GET("users/{username}")
    suspend fun getUser(
        @Path("username") username: String
    ): UserData

    @GET("users/{username}/{follow}")
    suspend fun getFollow(
        @Path("username") username: String,
        @Path("follow") follow: String,
    ): List<UserData>
}