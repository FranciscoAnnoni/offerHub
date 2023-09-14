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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.offerhub.R
import com.example.offerhub.activities.ShoppingActivity
import com.example.offerhub.databinding.FragmentLoginBinding
import com.example.offerhub.dialog.setupBottomSheetDialog
import com.example.offerhub.util.Resource
import com.example.offerhub.viewmodel.LoginViewModel
import com.example.offerhub.viewmodel.ProfileViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment:Fragment(R.layout.fragment_login) {
   private lateinit var binding: FragmentLoginBinding

    //private lateinit var sharedViewModel: ProfileViewModel para el log out exitoso

    private lateinit var rootViewLogin: View // Reemplaza con tu vista raíz de la actividad

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

        //es para poder enviar un mensaje de Log out exitoso pero no pude
        /*
        sharedViewModel = ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
        if(sharedViewModel.logoutSuccessLiveData.value == true){
            Snackbar.make(rootViewLogin, "Logout exitoso",Snackbar.LENGTH_SHORT).show()
            // Restablecer el valor en el ViewModel compartido
            sharedViewModel.logoutSuccessLiveData.value = false
        }
        */

        rootViewLogin = view // Asignar la vista raíz del fragmento

        binding.tvUpdatePassword.setOnClickListener {
            setupBottomSheetDialog {email ->
            viewModel.resetPassword(email)
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collect{
                when(it){
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        Snackbar.make(requireView(), "Se ha enviado un correo electrónico a su cuenta para restablecer la contraseña.", Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(), "Error: ${it.message}", Snackbar.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

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
                        Snackbar.make(rootViewLogin, "Login exitoso", Snackbar.LENGTH_SHORT).show()
                        binding.btnLogin.revertAnimation()
                        binding.btnLogin.setBackgroundResource(R.drawable.rounded_button_background)

                        Snackbar.make(rootViewLogin, "Login exitoso", Snackbar.LENGTH_SHORT).show()
                       Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                           startActivity(intent)
                       }
                    }
                    is Resource.Error -> {
                        Snackbar.make(rootViewLogin, "Error de Login, la contraseña o el mail son incorrectos", Snackbar.LENGTH_SHORT).show()
                        binding.btnLogin.revertAnimation()
                        binding.btnLogin.setBackgroundResource(R.drawable.rounded_button_background)
                    }
                    else -> Unit
                }
            }

        }
    }
}