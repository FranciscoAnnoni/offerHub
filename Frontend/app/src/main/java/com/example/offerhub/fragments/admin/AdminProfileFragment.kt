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
import com.example.offerhub.databinding.FragmentProfileAdminBinding
import com.example.offerhub.databinding.FragmentProfilePartnersBinding
import com.example.offerhub.util.Resource
import com.example.offerhub.util.showBottomNavigationVieAdmin
import com.example.offerhub.viewmodel.AdminProfileViewModel

import dagger.hilt.android.AndroidEntryPoint
import io.grpc.android.BuildConfig
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AdminProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileAdminBinding
    val viewModel by viewModels<AdminProfileViewModel>()

    private lateinit var rootView: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileAdminBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rootView = view


        binding.linearLogOut.setOnClickListener{
            val builder = AlertDialog.Builder(requireContext(), R.style.RoundedCornersDialog)
            builder.setTitle("Cerrar sesión")
            builder.setMessage("¿Está seguro de que desea cerrar sesión?")

            // Agregar un botón "Sí" para confirmar el cierre de sesión
            builder.setPositiveButton("Sí") { dialogInterface, _ ->
                // Realiza el cierre de sesión aquí
                viewModel.logout()

                // Redirige a la pantalla de inicio de sesión
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

        binding.tvVersion.text = "Version ${BuildConfig.VERSION_CODE}"

        lifecycleScope.launchWhenStarted {

        }


    }

    override fun onResume() {
        super.onResume()

        showBottomNavigationVieAdmin()
    }



}