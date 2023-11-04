package com.example.offerhub.fragments.admin


import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.offerhub.R
import com.example.offerhub.databinding.FragmentNotificarErrorAdminBinding
import com.example.offerhub.util.Resource
import com.example.offerhub.viewmodel.NotificarErrorViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificarErrorFragmentAdmin : Fragment() {
    private lateinit var binding: FragmentNotificarErrorAdminBinding
    private val viewModel by viewModels<NotificarErrorViewModel>()

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