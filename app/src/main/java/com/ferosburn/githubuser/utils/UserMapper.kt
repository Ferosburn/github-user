package com.ferosburn.githubuser.utils

import com.ferosburn.githubuser.data.local.entity.UserEntity
import com.ferosburn.githubuser.data.remote.response.UserData

fun userDataToUserEntity(user: UserData, isFavorite: Boolean = false, id: Int = 0): UserEntity {
    return UserEntity(
        id = id,
        username = user.login,
        avatarUrl = user.avatarUrl,
        name = user.name,
        followers = user.followers,
        following = user.following,
        isFavorite = isFavorite
    )
}