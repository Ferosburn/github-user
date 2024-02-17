package com.ferosburn.githubuser.ui.favorite_users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ferosburn.githubuser.R
import com.ferosburn.githubuser.data.local.entity.UserEntity
import com.ferosburn.githubuser.databinding.UserListItemBinding

class FavoriteUsersListAdapter(
    private val listener: (UserEntity) -> Unit,
) : ListAdapter<UserEntity, FavoriteUsersListAdapter.UserListViewHolder>(DIFF_CALLBACK) {
    class UserListViewHolder(private val binding: UserListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(userItem: UserEntity) {
            binding.apply {
                ivUserAvatar.load(userItem.avatarUrl) {
                    placeholder(R.drawable.ic_avatar_placeholder)
                    error(R.drawable.ic_avatar_placeholder)
                }
                tvUsername.text = userItem.username
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        val binding =
            UserListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
        holder.itemView.setOnClickListener { listener(user) }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserEntity>() {
            override fun areContentsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
                return oldItem.username == newItem.username
            }
        }
    }
}