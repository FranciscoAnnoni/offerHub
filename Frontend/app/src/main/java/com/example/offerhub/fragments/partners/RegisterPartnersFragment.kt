package com.example.offerhub.fragments.partners

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
import com.example.offerhub.data.UserPartner
import com.example.offerhub.databinding.FragmentRegisterPartnersBinding
import com.example.offerhub.util.RegisterValidation
import com.example.offerhub.util.Resource
import com.example.offerhub.viewmodel.RegisterPartnersViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.log

@AndroidEntryPoint
class RegisterPartnersFragment: Fragment() {

    private lateinit var binding: FragmentRegisterPartnersBinding

    private val viewModel by viewModels<RegisterPartnersViewModel>()

    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterPartnersBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rootView = view // Asignar la vista raíz del fragmento

        // ACA ES DONDE TENGO QUE VERLO CON FACU PARA QUE ESTOS VALORES SE LOS ASIGNE A UN USER= USER-PARTHENRS
        binding.apply {
            btnRegisterEmpresa.setOnClickListener {
                val email =  edEmailRegisterDeEmpresa.text.toString().trim()
                val password = edPassowrdRegisterEmpresa.text.toString()
                val password2 = edPassowrdRegisterEmpresaRepetida.text.toString()

                viewModel.createAccountWithEmailAndPassword(email,password, password2)

                viewModel.onRegistrationFailure = {
                    Snackbar.make(rootView, "Error con la base de datos", Snackbar.LENGTH_SHORT).show()
                }

                }

        }

        // VOLVER A LA PANTALLA DE ATRAS
        binding.atras.setOnClickListener {
            findNavController().navigate(R.id.action_registerPartnersFragment_to_loginPartnersFragment)
        }

        // ACA ES DONDE EL VIEW MODEL DEVUELVE EL USUARIO CREADO Y CONFIRMA QUE SE CREO CORRECTAMENTE
        lifecycleScope.launchWhenStarted {
            viewModel.register.collect{
                when(it){
                    is Resource.Loading -> {
                        binding.btnRegisterEmpresa.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.btnRegisterEmpresa.revertAnimation()
                        findNavController().navigate(R.id.action_registerPartnersFragment_to_registerPartnersFragment2)
                    }

                    is Resource.Error -> {
                        binding.btnRegisterEmpresa.revertAnimation()
                        binding.btnRegisterEmpresa.setBackgroundResource(R.drawable.rounded_button_background)
                    }
                    else -> Unit
                }
            }
        }

        // ACA VALIDO QUE LA CONTRRASENIA Y EL MAIL SEAN CORRECTOS
        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect {
                validation ->
                if (validation.email is RegisterValidation.Failed){
                    withContext(Dispatchers.Main) {
                        binding.edEmailRegisterDeEmpresa.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }

                if (validation.password is RegisterValidation.Failed){
                    withContext(Dispatchers.Main) {
                        binding.edPassowrdRegisterEmpresa.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }

                if (validation.password2 is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.edPassowrdRegisterEmpresaRepetida.apply {
                            requestFocus()
                            error = validation.password2.message
                        }
                    }
                }


            }
        }

    }

}