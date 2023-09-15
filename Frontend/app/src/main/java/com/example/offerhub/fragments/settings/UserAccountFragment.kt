package com.example.offerhub.fragments.settings

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.offerhub.Funciones
import com.example.offerhub.R
import com.example.offerhub.Usuario
import com.example.offerhub.activities.LoginRegisterActivity
import com.example.offerhub.data.User
import com.example.offerhub.databinding.FragmentUserAccountBinding
import com.example.offerhub.dialog.setupBottomSheetDialog
import com.example.offerhub.util.Resource
import com.example.offerhub.viewmodel.LoginViewModel
import com.example.offerhub.viewmodel.UserAccountViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserAccountFragment: Fragment() {
    private lateinit var rootView: View
    private lateinit var binding: FragmentUserAccountBinding
    private val viewModel by viewModels<UserAccountViewModel>()

    val auth: FirebaseAuth = FirebaseAuth.getInstance() // Inicializa FirebaseAuth
    val currentUser = auth.currentUser


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserAccountBinding.inflate(inflater)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageCloseUserAccount.setOnClickListener {
            findNavController().navigate(R.id.action_userAccountFragment_to_profileFragment)
        }

        binding.tvUpdatePassword.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Restablecer Contraseña")
            builder.setMessage("¿Está seguro de que deseas cambiar la Contraseña actual?\nRecibirás un correo electrónico para restablecerla.")


            // Agregar un botón "Sí" para confirmar el cierre de sesión
            builder.setPositiveButton("Sí") { dialogInterface, _ ->
                currentUser?.email?.let { it1 -> auth.sendPasswordResetEmail(it1) }
                Snackbar.make(requireView(), "Se ha enviado un correo electrónico a su cuenta para restablecer la contraseña.", Snackbar.LENGTH_LONG).show()
            }

            // Agregar un botón "No" para cancelar el cierre de sesión
            builder.setNegativeButton("No") { dialogInterface, _ ->
                // Cierra el cuadro de diálogo
                dialogInterface.dismiss()
            }

            // Mostrar el cuadro de diálogo
            val dialog: AlertDialog = builder.create()
            dialog.show()

        }

        rootView = view // Asignar la vista raíz del fragmento

        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showUserLoading()
                    }

                    is Resource.Success -> {
                        hideUserLoading()
                        showUserInformation(it.data!!)
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }

        // esto es para guardar la info cambiada

        lifecycleScope.launchWhenStarted {
            viewModel.updateInfo.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonSave.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.buttonSave.revertAnimation()
                        binding.buttonSave.setBackgroundResource(R.drawable.rounded_button_background)
                        if (rootView != null) {
                            Snackbar.make(rootView, "Cambio de nombre Exitoso", Snackbar.LENGTH_SHORT).show()
                        }
                        findNavController().navigate(R.id.action_userAccountFragment_to_profileFragment)

                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }

        binding.buttonSave.setOnClickListener{

            binding.apply{
                val nombreYapellido = edFirstName.text.toString().trim()
                viewModel.updateUser(nombreYapellido)
            }


        }



    }

// muestra la info del usuario
    private fun showUserInformation(data: Usuario) {
        binding.apply{
            edFirstName.setText(data.nombre)
            edEmail.setText(data.correo)
        }
    }

//muestra toda la info
    private fun hideUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.GONE
            edEmail.visibility = View.VISIBLE
            edFirstName.visibility = View.VISIBLE
            tvUpdatePassword.visibility = View.VISIBLE
            buttonSave.visibility = View.VISIBLE
            textView.visibility = View.VISIBLE
            textView2.visibility = View.VISIBLE
        }
    }

//esconde la info mientras la busca de la base
    private fun showUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.VISIBLE
            edEmail.visibility = View.INVISIBLE
            edFirstName.visibility = View.INVISIBLE
            tvUpdatePassword.visibility = View.INVISIBLE
            buttonSave.visibility = View.INVISIBLE
            textView.visibility = View.INVISIBLE
            textView2.visibility = View.INVISIBLE
        }
    }

}

