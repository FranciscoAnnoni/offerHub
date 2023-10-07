package com.example.offerhub.fragments.partners

import UserViewModel
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.offerhub.Funciones
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.example.offerhub.activities.ShoppingActivity
import com.example.offerhub.activities.ShoppingPartnersActivity
import com.example.offerhub.databinding.FragmentLoginPartnersBinding
import com.example.offerhub.dialog.setupBottomSheetDialog
import com.example.offerhub.util.Constants
import com.example.offerhub.util.Resource
import com.example.offerhub.viewmodel.LoginPartnersViewModel
import com.example.offerhub.viewmodel.UserViewModelCache
import com.example.offerhub.viewmodel.UserViewModelSingleton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch




@AndroidEntryPoint
class LoginPartnersFragment : Fragment(R.layout.fragment_login_partners) {
    private lateinit var binding: FragmentLoginPartnersBinding
    private lateinit var rootViewLogin: View
    private val viewModel by viewModels<LoginPartnersViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginPartnersBinding.inflate(inflater)
        return binding.root
    }

    // Función para ocultar el teclado
    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rootViewLogin = view

        // Configura el campo de correo electrónico
        binding.edEmailLoginEmpresa.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                binding.edPasswordLoginEmpresa.requestFocus()
                return@setOnEditorActionListener true
            }
            false
        }

        // Configura el campo de contraseña
        binding.edPasswordLoginEmpresa.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                hideKeyboard()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.atras.setOnClickListener {
            findNavController().navigate(R.id.action_loginPartnersFragment_to_loginFragment2)
        }
        binding.tvUpdatePassword.setOnClickListener {
            setupBottomSheetDialog { email ->
                viewModel.resetPassword(email)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collect {
                when (it) {
                    is Resource.Loading -> {
                        // Handle loading state
                    }
                    is Resource.Success -> {
                        Snackbar.make(
                            requireView(),
                            "Se ha enviado un correo electrónico a su cuenta para restablecer la contraseña.",
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

        binding.registrarPartner.setOnClickListener {
            findNavController().navigate(R.id.action_loginPartnersFragment_to_introductionFragmentPartners)
        }

        binding.apply {
            btnLoginEmpresa.setOnClickListener {
                val email = edEmailLoginEmpresa.text.toString().trim()
                val password = edPasswordLoginEmpresa.text.toString()

                viewModel.login(email, password)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.login.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnLoginEmpresa.startAnimation()
                    }
                    // esto lo tengo que ver con axel porque no tengo ni puta idea que esta haciendo aca con el usuario
                    is Resource.Success -> {
                        CoroutineScope(Dispatchers.Main).launch {
                            val userViewModel: UserViewModel by viewModels()
                            val userViewModelCache = UserViewModelCache()
                            val userViewModelCacheado = userViewModelCache.cargarUserViewModel()
                                if (userViewModelCacheado != null) {
                                    UserViewModelSingleton.initialize(userViewModelCacheado)
                                } else {
                                    userViewModel.usuarioPartner =Funciones().traerUsuarioPartnerActual()
                                    //userViewModel.listadoDePromosDisp = Funciones().obtenerPromociones(userViewModel.usuarioPartner!!))
                                    userViewModelCache.guardarUserViewModel(userViewModel)
                                    UserViewModelSingleton.initialize(userViewModel)

                                    val listaDePartners = Funciones().traerIdsPartners() //traigo la lista de partners que exista y la guardo en una variable global
                                }
                        }.invokeOnCompletion {
                            Snackbar.make(
                                rootViewLogin,
                                "Login exitoso",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            binding.btnLoginEmpresa.revertAnimation()
                            binding.btnLoginEmpresa.setBackgroundResource(R.drawable.rounded_button_background)


                     // Ejecuta la actividad de shopping
                            Intent(requireActivity(), ShoppingPartnersActivity::class.java).also { intent ->
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)


                            }
                        }
                    }
                    is Resource.Error -> {
                        Snackbar.make(
                            rootViewLogin,
                            "Error de Login, la contraseña o el correo son incorrectos",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        binding.btnLoginEmpresa.revertAnimation()
                        binding.btnLoginEmpresa.setBackgroundResource(R.drawable.rounded_button_background)
                    }
                    else -> Unit
                }
            }
        }
    }
}
