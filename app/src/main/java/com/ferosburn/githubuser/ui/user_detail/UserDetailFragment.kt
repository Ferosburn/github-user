package com.ferosburn.githubuser.ui.user_detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import coil.load
import com.ferosburn.githubuser.R
import com.ferosburn.githubuser.data.local.entity.UserEntity
import com.ferosburn.githubuser.databinding.FragmentUserDetailBinding
import com.ferosburn.githubuser.helper.ViewModelFactory
import com.ferosburn.githubuser.ui.user_detail.adapter.UserFollowsPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class UserDetailFragment : Fragment() {
    private var _binding: FragmentUserDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: UserDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = UserDetailFragmentArgs.fromBundle(arguments as Bundle).login
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(this, factory)[UserDetailViewModel::class.java]

        val userFollowsPagerAdapter = UserFollowsPagerAdapter(this, username)

        with(binding) {
            viewPager.adapter = userFollowsPagerAdapter
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = resources.getString(TAB_TITLES[position])
            }.attach()
            topAppBar.setNavigationOnClickListener {
                view.findNavController().navigateUp()
            }
            topAppBar.setOnMenuItemClickListener { menu ->
                when (menu.itemId) {
                    R.id.share -> {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "https://github.com/$username")
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        startActivity(shareIntent)
                        true
                    }

                    else -> false
                }
            }
        }

        with(viewModel) {
            hasLoaded.observe(viewLifecycleOwner) {
                it.getContentIfNotHandled()?.let { hasLoaded ->
                    if (!hasLoaded) {
                        getUserData(username)
                    }
                }
            }
            user.observe(viewLifecycleOwner) { user ->
                setUserData(user)
                setFavoriteState(user.isFavorite ?: false)
                binding.fabFavorite.setOnClickListener {
                    if (user.isFavorite == true) {
                        setFavoriteUser(false)
                    } else {
                        setFavoriteUser(true)
                    }
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

    private fun setUserData(user: UserEntity) {
        with(binding) {
            ivUserAvatar.load(user.avatarUrl) {
                placeholder(R.drawable.ic_avatar_placeholder)
                error(R.drawable.ic_avatar_placeholder)
            }
            tvFollow.text = getString(R.string.following_followers, user.following, user.followers)
            if (user.name.isNullOrBlank()) {
                tvName.text = user.username
                tvUsername.visibility = View.GONE
            } else {
                tvName.text = user.name
                tvUsername.text = user.username
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setFavoriteState(isFavorite: Boolean) {
        if (isFavorite) {
            binding.fabFavorite.setImageResource(R.drawable.ic_favorite)
        } else {
            binding.fabFavorite.setImageResource(R.drawable.ic_favorite_border)
        }
    }

    companion object {
        val TAB_TITLES = intArrayOf(
            R.string.following,
            R.string.followers
        )
    }
}