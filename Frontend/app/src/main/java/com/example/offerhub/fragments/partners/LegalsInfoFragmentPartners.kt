package com.example.offerhub.fragments.partners


import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.offerhub.databinding.FragmentLegalsBinding
import com.example.offerhub.util.hideBottomNavigationView
import com.example.offerhub.util.hideBottomNavigationViewPartners
import com.example.offerhub.util.showBottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LegalsInfoFragmentPartners : Fragment() {
    private lateinit var binding: FragmentLegalsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLegalsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.privacidad.paintFlags = Paint.FAKE_BOLD_TEXT_FLAG

        binding.recopilacionDeDatos.paintFlags = Paint.UNDERLINE_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
        binding.datosPersonales.paintFlags = Paint.UNDERLINE_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG


        binding.imageCloseLegals.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    override fun onResume() {
        super.onResume()
        hideBottomNavigationViewPartners()
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