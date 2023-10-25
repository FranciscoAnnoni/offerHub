package com.example.offerhub.fragments.settings


import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.offerhub.R
import com.example.offerhub.databinding.FragmentLegalsBinding
import com.example.offerhub.databinding.FragmentNotificarErrorBinding
import com.example.offerhub.util.Resource
import com.example.offerhub.util.hideBottomNavigationView
import com.example.offerhub.util.showBottomNavigationView
import com.example.offerhub.viewmodel.LoginViewModel
import com.example.offerhub.viewmodel.NotificarErrorViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class NotificarErrorFragment : Fragment() {
    private lateinit var binding: FragmentNotificarErrorBinding
    private val viewModel by viewModels<NotificarErrorViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificarErrorBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.privacidad.paintFlags = Paint.FAKE_BOLD_TEXT_FLAG


        // ver con facu actividad
        binding.apply {
            buttonSend.setOnClickListener {
                val notificacionDeError = edNotificacionDeError.text.toString().trim()
                viewModel.enviarNotificacionDeError(notificacionDeError)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.notificacionExitosa.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonSend.startAnimation()
                    }
                    is Resource.Success -> {


                        binding.buttonSend.revertAnimation()
                        binding.buttonSend.setBackgroundResource(R.drawable.rounded_button_background)

                        findNavController().navigate(R.id.action_notificarErrorFragment_to_profileFragment)


                        Snackbar.make(
                            requireView(),
                            "Su comentario se a enviado correctamente",
                            Snackbar.LENGTH_LONG
                        ).show()

                    }
                    is Resource.Error -> {
                        Snackbar.make(
                            requireView(),
                            "Error: ${it.message}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    else -> Unit
                }
            }
        }

        binding.imageCloseLegals.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    override fun onResume() {
        super.onResume()
        hideBottomNavigationView()
    }




}

/*
class FavFragment: Fragment(R.layout.fragment_legals){
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

}

 */