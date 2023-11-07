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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        coroutineScope.launch {
            val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
            progressBar.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE

            val funciones = FuncionesPartners()
            val quejas = funciones.obtenerReportes()

            val quejasAdapter = QuejasAdapter(quejas as MutableList<String>, requireContext(), requireView())
            recyclerView.adapter = quejasAdapter
            progressBar.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }

    }

}
