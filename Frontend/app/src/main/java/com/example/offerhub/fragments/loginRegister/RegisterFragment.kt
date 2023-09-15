package com.example.offerhub.fragments.loginRegister


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.offerhub.R
import com.example.offerhub.data.User
import com.example.offerhub.databinding.FragmentRegisterBinding
import com.example.offerhub.util.RegisterValidation
import com.example.offerhub.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

import com.example.offerhub.util.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


private val TAG = "RegisterFragment"

@AndroidEntryPoint
class RegisterFragment:Fragment() {

    private lateinit var binding: FragmentRegisterBinding

    private val viewModel by viewModels<RegisterViewModel>()

    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rootView = view // Asignar la vista raíz del fragmento

        binding.apply {
            btnRegister.setOnClickListener {
                val user = User(
                    edNamerRegister.text.toString().trim(),
                    edEmailRegister.text.toString().trim()

                )
                val password = edPassowrdRegister.text.toString()
                viewModel.createAccountWithEmailAndPassword(user,password)
            }
        }

        binding.atras.setOnClickListener {
            findNavController().navigateUp()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.register.collect{
                when(it){
                    is Resource.Loading -> {
                        binding.btnRegister.startAnimation()
                    }
                    is Resource.Success -> {
                        Log.d("test", it.data.toString())
                        binding.btnRegister.revertAnimation()
                        Snackbar.make(rootView, "Registro exitoso", Snackbar.LENGTH_SHORT).show()
                        // Puedes agregar lógica adicional aquí, como redirigir al usuario a la pantalla de inicio de sesión
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                    }
                    is Resource.Error -> {
                        Log.e(TAG,it.message.toString())
                        binding.btnRegister.revertAnimation()
                        binding.btnRegister.setBackgroundResource(R.drawable.rounded_button_background)
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect {
                validation ->
                if (validation.email is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.edEmailRegister.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }

                if (validation.password is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.edPassowrdRegister.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }
            }
        }

    }

}