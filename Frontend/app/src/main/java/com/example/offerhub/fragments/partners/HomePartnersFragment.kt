package com.example.offerhub.fragments.partners

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.offerhub.R
import com.example.offerhub.databinding.FragmentHomePartnersBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomePartnersFragment : Fragment(R.layout.fragment_home_partners) {
    private lateinit var binding: FragmentHomePartnersBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomePartnersBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}

