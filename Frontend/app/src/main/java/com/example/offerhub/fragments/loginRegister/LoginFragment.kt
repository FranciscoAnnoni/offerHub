package com.example.offerhub.fragments.loginRegister

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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.offerhub.Funciones
import com.example.offerhub.KelineApplication
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.example.offerhub.activities.ShoppingActivity
import com.example.offerhub.databinding.FragmentLoginBinding
import com.example.offerhub.dialog.setupBottomSheetDialog
import com.example.offerhub.util.Resource
import com.example.offerhub.viewmodel.LoginViewModel
import com.example.offerhub.viewmodel.ProfileViewModel
import com.example.offerhub.viewmodel.UserViewModelCache
import com.example.offerhub.viewmodel.UserViewModelSingleton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var rootViewLogin: View
    private val viewModel by viewModels<LoginViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
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
        binding.edEmailLogin.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                binding.edPasswordLogin.requestFocus()
                return@setOnEditorActionListener true
            }
            false
        }

        // Configura el campo de contraseña
        binding.edPasswordLogin.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                hideKeyboard()
                return@setOnEditorActionListener true
            }
            false
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

        binding.registerNow.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.loginRegistrarEmpresa.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_loginPartnersFragment)
        }

        binding.apply {
            btnLogin.setOnClickListener {
                val email = edEmailLogin.text.toString().trim()
                val password = edPasswordLogin.text.toString()

                viewModel.login(email, password)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.login.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnLogin.startAnimation()
                    }
                    is Resource.Success -> {
                        CoroutineScope(Dispatchers.Main).launch {
                            val userViewModel: UserViewModel by viewModels()
                            val userViewModelCache = UserViewModelCache()
                            val userViewModelCacheado = userViewModelCache.cargarUserViewModel()
                                if (userViewModelCacheado != null) {
                                    UserViewModelSingleton.initialize(userViewModelCacheado)
                                } else {
                                    userViewModel.usuario =Funciones().traerUsuarioActual()
                                    userViewModel.listadoDePromosDisp = Funciones().obtenerPromociones(userViewModel.usuario!!)
                                    userViewModel.favoritos = Funciones().obtenerPromocionesFavoritas(userViewModel.usuario!!,userViewModel.listadoDePromosDisp as MutableList<Promocion>)
                                    userViewModel.reintegros = Funciones().obtenerPromocionesReintegro(userViewModel.usuario!!,userViewModel.listadoDePromosDisp as MutableList<Promocion>)
                                    userViewModelCache.guardarUserViewModel(userViewModel)
                                    UserViewModelSingleton.initialize(userViewModel)
                                }
                        }.invokeOnCompletion {
                            Snackbar.make(
                                rootViewLogin,
                                "Login exitoso",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            binding.btnLogin.revertAnimation()
                            binding.btnLogin.setBackgroundResource(R.drawable.rounded_button_background)

                            Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
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
                        binding.btnLogin.revertAnimation()
                        binding.btnLogin.setBackgroundResource(R.drawable.rounded_button_background)
                    }
                    else -> Unit
                }
            }
        }


    }


}
