package com.ferosburn.githubuser.ui.search_user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ferosburn.githubuser.R
import com.ferosburn.githubuser.data.local.entity.UserEntity
import com.ferosburn.githubuser.databinding.FragmentSearchUserBinding
import com.ferosburn.githubuser.helper.ViewModelFactory

class SearchUserFragment : Fragment() {
    private var _binding: FragmentSearchUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchUserBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: SearchUserViewModel by viewModels { factory }

        val layoutManager = LinearLayoutManager(context)
        val itemDecoration = DividerItemDecoration(context, layoutManager.orientation)

        with(viewModel) {
            users.observe(viewLifecycleOwner) { users ->
                if (users.isNullOrEmpty()) {
                    binding.emptyState.root.visibility = View.VISIBLE
                    binding.emptyState.tvInfo.text = getString(R.string.no_user)
                    binding.rvUsers.visibility = View.GONE
                } else {
                    binding.emptyState.root.visibility = View.GONE
                    binding.rvUsers.visibility = View.VISIBLE
                    setUserListData(users, view)
                }
            }
            isLoading.observe(viewLifecycleOwner) { isLoading -> showLoading(isLoading) }
            errorMessage.observe(viewLifecycleOwner) {
                it.getContentIfNotHandled()?.let { errorMessage ->
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
            getThemeSetting().observe(viewLifecycleOwner) { isDarkModeActive ->
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView.editText.setOnEditorActionListener { _, _, _ ->
                searchBar.setText(searchView.text)
                searchView.hide()
                viewModel.searchUser(searchView.text.toString())
                false
            }
            rvUsers.layoutManager = layoutManager
            rvUsers.addItemDecoration(itemDecoration)
            topAppBar.setOnMenuItemClickListener { menu ->
                when (menu.itemId) {
                    R.id.favorites -> {
                        view.findNavController()
                            .navigate(SearchUserFragmentDirections.actionSearchUserFragmentToFavoriteUsersFragment())
                        true
                    }

                    R.id.settings -> {
                        view.findNavController()
                            .navigate(SearchUserFragmentDirections.actionSearchUserFragmentToSettingsFragment())
                        true
                    }

                    else -> false
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun navigateToUserDetail(item: UserEntity, view: View) {
        val toUserDetailFragment =
            SearchUserFragmentDirections.actionSearchUserFragmentToUserDetailFragment()
        toUserDetailFragment.login = item.username
        view.findNavController().navigate(toUserDetailFragment)
    }

    private fun setUserListData(userList: List<UserEntity>, view: View) {
        val adapter = SearchUserListAdapter { navigateToUserDetail(it, view) }
        adapter.submitList(userList)
        binding.rvUsers.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}