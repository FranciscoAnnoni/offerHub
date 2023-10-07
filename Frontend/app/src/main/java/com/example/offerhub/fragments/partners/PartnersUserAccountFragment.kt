package com.example.offerhub.fragments.partners

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
import com.example.offerhub.databinding.FragmentPartnerUserAccountBinding
import com.example.offerhub.util.Resource
import com.example.offerhub.viewmodel.PartnersUserAccountViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class PartnersUserAccountFragment: Fragment() {
    private lateinit var rootView: View
    private lateinit var binding: FragmentPartnerUserAccountBinding
    private val viewModel by viewModels<PartnersUserAccountViewModel>()

    val auth: FirebaseAuth = FirebaseAuth.getInstance() // Inicializa FirebaseAuth
    val currentUser = auth.currentUser


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPartnerUserAccountBinding.inflate(inflater)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageCloseUserAccount.setOnClickListener {
            findNavController().navigate(R.id.action_partnersUserAccountFragment_to_partnersProfileFragment)
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
            viewModel.userPartner.collectLatest {
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


        binding.buttonDelete.setOnClickListener{

            binding.apply{
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Eliminar Cuenta Permanentemenete")
                builder.setMessage("¿Está seguro de que desea eliminar la cuenta actual?\n Estos cambios son permanentes y no habra manera de recuperar la cuenta")


                // Agregar un botón "Sí" para confirmar el cierre de sesión
                builder.setPositiveButton("Sí") { dialogInterface, _ ->

                    viewModel.deleteUser()
                    Snackbar.make(requireView(), "Se ha eliminado la cuenta correctamente", Snackbar.LENGTH_LONG).show()
                    val intent = Intent(requireActivity(), LoginRegisterActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()

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


        }

    }

// muestra la info del usuario Partner
    private fun showUserInformation(data: UserPartner) {
        binding.apply{
            edFirstName.setText(data.nombreDeEmpresa)
            edCuil.setText(data.cuil)
            edEmail.setText(data.email)

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
            buttonDelete.visibility = View.VISIBLE
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
            buttonDelete.visibility = View.INVISIBLE
        }
    }

}

