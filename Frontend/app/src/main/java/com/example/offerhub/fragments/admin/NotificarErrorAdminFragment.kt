package com.example.offerhub.fragments.admin


import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.offerhub.Funciones
import com.example.offerhub.R
import com.example.offerhub.databinding.FragmentNotificarErrorAdminBinding
import com.example.offerhub.funciones.FuncionesPartners
import com.example.offerhub.util.Resource
import com.example.offerhub.viewmodel.NotificarErrorAdminViewModel
import com.example.offerhub.viewmodel.NotificarErrorViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificarErrorFragmentAdmin : Fragment() {
    private lateinit var binding: FragmentNotificarErrorAdminBinding
    private val viewModel by viewModels<NotificarErrorAdminViewModel>()
    val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificarErrorAdminBinding.inflate(inflater)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val reportesContainer = view.findViewById<LinearLayout>(R.id.reportLayout)

        coroutineScope.launch {
            val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
            progressBar.visibility = View.VISIBLE

            binding.reportLayout.visibility = View.GONE

            val funciones = FuncionesPartners()
            val quejas = funciones.obtenerReportes()

            quejas.forEach { queja ->
                val quejaTextView = TextView(requireContext())
                quejaTextView.text = queja

                // Define parámetros de diseño para cada TextView (opcional)
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                // Agrega el TextView al contenedor
                reportesContainer.addView(quejaTextView, layoutParams)
            }
            binding.reportLayout.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }

        /*
        coroutineScope.launch {
            var funciones = FuncionesPartners()
            val quejas = funciones.obtenerReportes()

            // Inicializa el contenido de edNotificacionDeError
            val edNotificacionDeError = binding.edNotificacionDeError

            // Itera sobre la lista de quejas y actualiza el contenido de edNotificacionDeError
            quejas.forEach { queja ->
                // Agrega un salto de línea si ya hay contenido en edNotificacionDeError
                if (!edNotificacionDeError.text.isNullOrBlank()) {
                    edNotificacionDeError.append("\n")
                }

                // Agrega la queja al contenido de edNotificacionDeError
                edNotificacionDeError.append(queja)
            }
        }

         */
    }



}

/*
class FavFragment: Fragment(R.layout.fragment_legals){
    @AndroidEntryPoint
    class ProfileFragment : Fragment() {
        private lateinit var binding: FragmentProfileBinding
        val viewModel by viewModels<ProfileViewModel>()
        private lateinit var rootView: View
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            binding = FragmentProfileBinding.inflate(inflater)
            return binding.root
        }

}

 */