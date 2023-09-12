package com.example.offerhub.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.offerhub.Funciones
import com.example.offerhub.InterfaceSinc
import com.example.offerhub.R
import com.example.offerhub.Usuario
import com.example.offerhub.databinding.FragmentHomeBinding
import com.example.offerhub.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch


class ProfileFragment: Fragment(R.layout.fragment_profile){
    private lateinit var binding: FragmentProfileBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            val instancia = Funciones()
            val usuario: Usuario? = instancia.traerUsuarioActual()
            val nombre = usuario?.nombre ?: "Usuario Desconocido" // Valor predeterminado si usuario es nulo


            // Actualizar la vista con el nombre de usuario
            binding.nombreUsuario.text = "Hola, $nombre"
            view.findViewById<TextView>(R.id.nombreUsuario).text = "Hola, ${nombre}"
        }
    }

}