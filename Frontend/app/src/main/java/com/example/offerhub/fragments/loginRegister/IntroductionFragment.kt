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
import com.example.offerhub.databinding.FragmentUserAccountBinding
import com.example.offerhub.viewmodel.IntroductionViewModel
import com.example.offerhub.viewmodel.IntroductionViewModel.Companion.ACCOUNT_OPTIONS_FRAGMENT
import com.example.offerhub.viewmodel.IntroductionViewModel.Companion.SHOPPING_ACTIVITY
import com.example.offerhub.viewmodel.IntroductionViewModel.Companion.SHOPPING_ACTIVITY_ADMIN
import com.example.offerhub.viewmodel.IntroductionViewModel.Companion.SHOPPING_ACTIVITY_PARTNERS
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroductionFragment:Fragment(R.layout.fragment_introduction) {
    private lateinit var binding: FragmentIntroductionBinding
    private val viewModel by viewModels<IntroductionViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntroductionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launchWhenStarted {
            viewModel.navigate.collect{

                when(it){
                    SHOPPING_ACTIVITY_PARTNERS ->{
                        hide()
                        Intent(requireActivity(), ShoppingPartnersActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }

                    SHOPPING_ACTIVITY ->{
                        hide()
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }

                    SHOPPING_ACTIVITY_ADMIN ->{
                        hide()
                        Intent(requireActivity(), ShoppingAdminActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }

                    ACCOUNT_OPTIONS_FRAGMENT -> {
                        hide()
                        findNavController().navigate(R.id.action_introductionFragment_to_loginFragment)
                    }
                    else -> Unit
                }
            }
        }

        binding.botonStart.setOnClickListener{
            viewModel.startButtonClick()
            hide()
            findNavController().navigate(R.id.action_introductionFragment_to_loginFragment)
        }
    }

    private fun hide() {
        binding.apply {
            carga.visibility = View.INVISIBLE
            botonStart.visibility = View.INVISIBLE
            logoIntro.visibility = View.INVISIBLE
            tituloIntroduccion.visibility = View.INVISIBLE
            centraliza.visibility = View.INVISIBLE
            offerHub.visibility = View.INVISIBLE
            con.visibility = View.INVISIBLE
        }
    }

    private fun show() {
        binding.apply {
            carga.visibility = View.INVISIBLE
            botonStart.visibility = View.VISIBLE
            logoIntro.visibility = View.VISIBLE
            tituloIntroduccion.visibility = View.VISIBLE
            centraliza.visibility = View.VISIBLE
            offerHub.visibility = View.VISIBLE
            con.visibility = View.VISIBLE
        }
    }

}
