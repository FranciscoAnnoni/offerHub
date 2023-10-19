package com.example.offerhub.fragments.partners

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.offerhub.databinding.FragmentCargarPromocionPartnersBinding


class CargarPromocionPartnersFragment: Fragment()  {

    private lateinit var binding: FragmentCargarPromocionPartnersBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCargarPromocionPartnersBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}