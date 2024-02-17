package com.ferosburn.githubuser.ui.user_detail.follows

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ferosburn.githubuser.R
import com.ferosburn.githubuser.data.local.entity.UserEntity
import com.ferosburn.githubuser.databinding.FragmentFollowBinding
import com.ferosburn.githubuser.helper.ViewModelFactory
import com.ferosburn.githubuser.ui.user_detail.UserDetailFragment
import com.ferosburn.githubuser.ui.user_detail.adapter.FollowsListAdapter

class FollowsFragment : Fragment() {
    private var _binding: FragmentFollowBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FollowsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(this, factory)[FollowsViewModel::class.java]

        val layoutManager = LinearLayoutManager(context)
        val itemDecoration = DividerItemDecoration(context, layoutManager.orientation)
        val tab = arguments?.getInt(ARG_TAB_NAME) ?: UserDetailFragment.TAB_TITLES[0]
        val username = arguments?.getString(ARG_USERNAME) ?: ""

        with(binding) {
            rvUsers.layoutManager = layoutManager
            rvUsers.addItemDecoration(itemDecoration)
        }

        with(viewModel) {
            hasLoaded.observe(viewLifecycleOwner) {
                it.getContentIfNotHandled()?.let { hasLoaded ->
                    if (!hasLoaded) {
                        getFollows(username, getString(tab).lowercase())
                    }
                }
            }
            userList.observe(viewLifecycleOwner) {
                if (it.isNullOrEmpty()) {
                    binding.emptyState.root.visibility = View.VISIBLE
                    binding.emptyState.tvInfo.text = getString(R.string.no_user)
                    binding.rvUsers.visibility = View.GONE
                } else {
                    binding.emptyState.root.visibility = View.GONE
                    binding.rvUsers.visibility = View.VISIBLE
                    setUserListData(it)
                }
            }
            isLoading.observe(viewLifecycleOwner) { showLoading(it) }
            errorMessage.observe(viewLifecycleOwner) {
                it.getContentIfNotHandled()?.let { errorMessage ->
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setUserListData(userList: List<UserEntity>) {
        with(binding) {
            val adapter = FollowsListAdapter()
            adapter.submitList(userList)
            rvUsers.adapter = adapter
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val ARG_TAB_NAME = "ARG_TAB_NAME"
        const val ARG_USERNAME = "ARG_USERNAME"
    }
}