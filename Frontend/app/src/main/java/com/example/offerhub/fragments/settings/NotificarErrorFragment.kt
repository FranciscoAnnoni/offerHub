package com.example.offerhub.fragments.settings


import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.offerhub.databinding.FragmentLegalsBinding
import com.example.offerhub.databinding.FragmentNotificarErrorBinding
import com.example.offerhub.util.hideBottomNavigationView
import com.example.offerhub.util.showBottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificarErrorFragment : Fragment() {
    private lateinit var binding: FragmentNotificarErrorBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificarErrorBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.privacidad.paintFlags = Paint.FAKE_BOLD_TEXT_FLAG


        // ver con facu actividad

        binding.imageCloseLegals.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    override fun onResume() {
        super.onResume()
        hideBottomNavigationView()
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