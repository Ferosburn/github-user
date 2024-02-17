package com.ferosburn.githubuser.ui.user_detail.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ferosburn.githubuser.ui.user_detail.UserDetailFragment
import com.ferosburn.githubuser.ui.user_detail.follows.FollowsFragment

class UserFollowsPagerAdapter(
    fragment: Fragment, private val username: String
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = UserDetailFragment.TAB_TITLES.size

    override fun createFragment(position: Int): Fragment {
        val fragment = FollowsFragment()
        fragment.arguments = Bundle().apply {
            putInt(FollowsFragment.ARG_TAB_NAME, UserDetailFragment.TAB_TITLES[position])
            putString(FollowsFragment.ARG_USERNAME, username)
        }
        return fragment
    }
}