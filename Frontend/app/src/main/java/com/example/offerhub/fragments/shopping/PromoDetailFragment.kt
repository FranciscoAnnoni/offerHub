package com.example.offerhub.fragments.shopping

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.offerhub.R
import com.example.offerhub.databinding.FragmentPromoDetailBinding
import com.example.offerhub.funciones.removeAccents
import com.example.offerhub.viewmodel.PromoDetailViewModel

class PromoDetailFragment: Fragment(R.layout.fragment_promo_detail){
    private val args by navArgs<PromoDetailFragmentArgs>()
    private lateinit var binding: FragmentPromoDetailBinding
    private val viewModel by viewModels<PromoDetailViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // hideBottomNavigationView()
        binding = FragmentPromoDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val promocion = args.promocion
        binding.imageClose.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.apply {
            promoTitulo.text = promocion.titulo
            promoDesc.text = promocion.tyc
            for (dia in promocion.dias!!) {
                val nombreDia= removeAccents(dia)
                val circleId = resources.getIdentifier("circle$nombreDia", "id", requireContext().packageName)
                val circleView = view.findViewById<TextView>(circleId)
                circleView.setBackgroundResource(R.drawable.circle_foreground)
                circleView.setTextColor(Color.parseColor("#FFFFFF"))
            }
            }
            //tvProductPrice.text = "$ ${promocion.price}"
            //tvProductDescription.text = promocion.description

        }

    }