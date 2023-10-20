package com.example.offerhub.fragments.admin

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.offerhub.R
import com.example.offerhub.activities.LoginRegisterActivity
import com.example.offerhub.data.UserPartner
import com.example.offerhub.databinding.FragmentAdminUserAccountBinding
import com.example.offerhub.databinding.FragmentPartnerUserAccountBinding
import com.example.offerhub.util.Resource
import com.example.offerhub.viewmodel.AdminProfileViewModel
import com.example.offerhub.viewmodel.PartnersUserAccountViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AdminUserAccountFragment: Fragment() {
    private lateinit var rootView: View
    private lateinit var binding: FragmentAdminUserAccountBinding
    private val viewModel by viewModels<AdminProfileViewModel>()

    val auth: FirebaseAuth = FirebaseAuth.getInstance() // Inicializa FirebaseAuth
    val currentUser = auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminUserAccountBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageCloseUserAccount.setOnClickListener {
            findNavController().navigate(R.id.action_adminUserAccountFragment_to_adminProfileFragment)
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

