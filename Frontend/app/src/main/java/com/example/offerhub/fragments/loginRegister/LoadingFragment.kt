package com.example.offerhub.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.offerhub.R
import com.example.offerhub.activities.ShoppingActivity
import com.example.offerhub.activities.ShoppingAdminActivity
import com.example.offerhub.activities.ShoppingPartnersActivity
import com.example.offerhub.databinding.FragmentIntroductionBinding
import com.example.offerhub.databinding.FragmentLoadingBinding
import com.example.offerhub.databinding.FragmentUserAccountBinding
import com.example.offerhub.viewmodel.IntroductionViewModel
import com.example.offerhub.viewmodel.IntroductionViewModel.Companion.ACCOUNT_OPTIONS_FRAGMENT
import com.example.offerhub.viewmodel.IntroductionViewModel.Companion.SHOPPING_ACTIVITY
import com.example.offerhub.viewmodel.IntroductionViewModel.Companion.SHOPPING_ACTIVITY_ADMIN
import com.example.offerhub.viewmodel.IntroductionViewModel.Companion.SHOPPING_ACTIVITY_PARTNERS
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class LoadingFragment:Fragment(R.layout.fragment_loading) {
    private lateinit var binding: FragmentLoadingBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoadingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            delay(6500)

            findNavController().navigate(R.id.action_loadingFragment_to_introductionFragment)
        }


    }


}
