package com.example.offerhub.fragments.shopping

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.offerhub.R
import com.example.offerhub.activities.LoginRegisterActivity
import com.example.offerhub.databinding.FragmentProfileBinding
import com.example.offerhub.util.Resource
import com.example.offerhub.util.showBottomNavigationView
import com.example.offerhub.viewmodel.ProfileViewModel
import com.google.android.material.snackbar.Snackbar

import dagger.hilt.android.AndroidEntryPoint
import io.grpc.android.BuildConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    val viewModel by viewModels<ProfileViewModel>()
    private lateinit var rootView: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootView = view

        binding.constraintProfile.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)
        }
        binding.linearLegals.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_legalsInfoFragment)
        }

        binding.linearLogOut.setOnClickListener{
            viewModel.logout()
            // esto hace que se envie el mensaje de log Out
            // viewModel.logoutSuccessLiveData.value = true

            val intent = Intent(requireActivity(),LoginRegisterActivity::class.java)
            startActivity(intent)
            requireActivity().finish()

        }

        binding.tvVersion.text = "Version ${BuildConfig.VERSION_CODE}"

        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    binding.progressbarSettings.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarSettings.visibility = View.GONE
                        binding.tvUserName.text = it.data?.nombre
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.progressbarSettings.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()

        showBottomNavigationView()
    }

}