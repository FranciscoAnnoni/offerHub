package com.example.offerhub.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.offerhub.R
import com.example.offerhub.databinding.FormularioCargadoTarjetasBinding
import com.example.offerhub.databinding.FragmentIntroductionBinding
import com.example.offerhub.databinding.FragmentUserAccountBinding
import com.example.offerhub.viewmodel.CargadoTarjetasViewModel
import com.example.offerhub.viewmodel.UserAccountViewModel
import com.google.firebase.auth.FirebaseAuth

class CargadoTarjetasFragment : Fragment() {
    private lateinit var binding: FormularioCargadoTarjetasBinding
    private val viewModel by viewModels<CargadoTarjetasViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FormularioCargadoTarjetasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        }


}


