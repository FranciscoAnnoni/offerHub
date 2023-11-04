package com.example.offerhub.fragments.partners

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import com.example.offerhub.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class VerMotivoFragment : BottomSheetDialogFragment() {
    private val args by navArgs<VerMotivoFragmentArgs>()

    companion object {
        fun newInstance(motivo: String): VerMotivoFragment {
            val fragment = VerMotivoFragment()
            val args = Bundle()
            args.putString("motivo", motivo)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ver_motivo, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
        val motivo = arguments?.getString("motivo")
        val tvMotivo =  view.findViewById<TextView>(R.id.tvMotivo)
        tvMotivo.text=if(motivo=="null") "No Especificado" else motivo
        val imageClose =  view.findViewById<ImageView>(R.id.imageClose)
        imageClose.setOnClickListener {
            dismiss()
        }



    }
}