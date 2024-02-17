package com.ferosburn.githubuser.ui.favorite_users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ferosburn.githubuser.R
import com.ferosburn.githubuser.data.local.entity.UserEntity
import com.ferosburn.githubuser.databinding.FragmentFavoriteUsersBinding
import com.ferosburn.githubuser.helper.ViewModelFactory

class FavoriteUsersFragment : Fragment() {
    private var _binding: FragmentFavoriteUsersBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FavoriteUsersViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteUsersBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(this, factory)[FavoriteUsersViewModel::class.java]

        val layoutManager = LinearLayoutManager(context)
        val itemDecoration = DividerItemDecoration(context, layoutManager.orientation)

        viewModel.getFavoriteUsers().observe(viewLifecycleOwner) { favoriteUsers ->
            if (favoriteUsers.isNullOrEmpty()) {
                binding.emptyState.root.visibility = View.VISIBLE
                binding.emptyState.tvInfo.text = getString(R.string.no_user)
                binding.rvUsers.visibility = View.GONE
            } else {
                binding.emptyState.root.visibility = View.GONE
                binding.rvUsers.visibility = View.VISIBLE
                setUserListData(favoriteUsers, view)
            }
        }

        with(binding) {
            rvUsers.layoutManager = layoutManager
            rvUsers.addItemDecoration(itemDecoration)
            topAppBar.setNavigationOnClickListener {
                view.findNavController().navigateUp()
            }
        }
    }

    private fun navigateToUserDetail(item: UserEntity, view: View) {
        val toUserDetailFragment =
            FavoriteUsersFragmentDirections.actionFavoriteUsersFragmentToUserDetailFragment()
        toUserDetailFragment.login = item.username
        view.findNavController().navigate(toUserDetailFragment)
    }

    private fun setUserListData(userList: List<UserEntity>, view: View) {
        val adapter = FavoriteUsersListAdapter { navigateToUserDetail(it, view) }
        adapter.submitList(userList)
        binding.rvUsers.adapter = adapter
    }
}