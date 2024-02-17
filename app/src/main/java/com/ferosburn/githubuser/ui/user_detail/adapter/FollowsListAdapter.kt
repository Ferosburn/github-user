package com.ferosburn.githubuser.ui.user_detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ferosburn.githubuser.R
import com.ferosburn.githubuser.data.local.entity.UserEntity
import com.ferosburn.githubuser.databinding.UserListItemBinding

class FollowsListAdapter :
    ListAdapter<UserEntity, FollowsListAdapter.FollowsListViewHolder>(DIFF_CALLBACK) {
    class FollowsListViewHolder(private val binding: UserListItemBinding) :
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowsListViewHolder {
        val binding =
            UserListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FollowsListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FollowsListViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }
}