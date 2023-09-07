package com.example.offerhub.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.offerhub.R
import com.example.offerhub.activities.ShoppingActivity
import com.example.offerhub.databinding.FragmentLoginBinding
import com.example.offerhub.util.Resource
import com.example.offerhub.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment:Fragment(R.layout.fragment_login) {
   private lateinit var binding: FragmentLoginBinding


    private lateinit var rootView: View // Reemplaza con tu vista raíz de la actividad

    private val viewModel by viewModels<LoginViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rootView = view // Asignar la vista raíz del fragmento

        binding.registerNow.setOnClickListener{
         findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }


        binding.apply {
            btnLogin.setOnClickListener{
                val email = edEmailLogin.text.toString().trim()
                val password = edPasswordLogin.text.toString()

                viewModel.login(email, password)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.login.collect{
                when(it){
                    is Resource.Loading -> {
                        binding.btnLogin.startAnimation()
                    }
                    is Resource.Success -> {
                        Snackbar.make(rootView, "Login exitoso", Snackbar.LENGTH_SHORT).show()
                        binding.btnLogin.revertAnimation()
                        Snackbar.make(rootView, "Login exitoso", Snackbar.LENGTH_SHORT).show()
                       Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                           startActivity(intent)
                       }
                    }
                    is Resource.Error -> {
                        Snackbar.make(rootView, "Error de Login", Snackbar.LENGTH_SHORT).show()
                        Toast.makeText(requireContext(), it.message , Toast.LENGTH_LONG).show()
                        binding.btnLogin.revertAnimation()
                    }
                    else -> Unit
                }
            }

        }
    }
}