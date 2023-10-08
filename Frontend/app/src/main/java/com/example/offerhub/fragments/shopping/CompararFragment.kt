package com.example.offerhub.fragments.shopping

import UserViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.offerhub.Funciones
import com.example.offerhub.InterfaceSinc
import com.example.offerhub.LeerId
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.example.offerhub.viewmodel.UserViewModelSingleton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CompararFragment : BottomSheetDialogFragment() {
    private val args by navArgs<CompararFragmentArgs>()
    var userViewModel :UserViewModel=UserViewModel()

    companion object {
        fun newInstance(promocion1: Promocion, promocion2: Promocion): CompararFragment {
            val fragment = CompararFragment()
            val args = Bundle()
            args.putParcelable("promocion1", promocion1)
            args.putParcelable("promocion2", promocion2)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.comparacion_fragment, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel = UserViewModelSingleton.getUserViewModel()
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
        val promocion1 = arguments?.getParcelable<Promocion>("promocion1")
        val promocion2 = arguments?.getParcelable<Promocion>("promocion2")

        val imageClose =  view.findViewById<ImageView>(R.id.imageClose)
        imageClose.setOnClickListener {
            dismiss()
        }
        if (promocion1 != null && promocion2!=null){
        view.apply {
            view.findViewById<TextView>(R.id.titulo1).text= promocion1.titulo
            view.findViewById<TextView>(R.id.titulo2).text= promocion2.titulo
            view.findViewById<TextView>(R.id.hasta1).text = promocion1.vigenciaHasta.toString()
            view.findViewById<TextView>(R.id.hasta2).text = promocion2.vigenciaHasta.toString()
            var diasDisp1 = promocion1.dias?.joinToString("\n")
            var diasDisp2 = promocion2.dias?.joinToString("\n")
            if (diasDisp1==null){
                diasDisp1 = "Todos los dias"
            }
            if (diasDisp2==null){
                diasDisp2 = "Todos los dias"
            }
            view.findViewById<TextView>(R.id.dias1).text = diasDisp1
            view.findViewById<TextView>(R.id.dias2).text = diasDisp2
            var reint1 = view.findViewById<TextView>(R.id.reintegro1)
            var reint2 = view.findViewById<TextView>(R.id.reintegro2)
            var text1 =promocion1.obtenerDesc()
            var text2 =promocion2.obtenerDesc()
            if (promocion1.tipoPromocion == "Reintegro"){
                reint1.text = "Tope de " + promocion1.topeNro
            }else{
                reint1.visibility = View.GONE
            }
            if (promocion2.tipoPromocion == "Reintegro"){
                reint2.text = "Tope de " + promocion2.topeNro
            }else{
                reint2.visibility = View.GONE
            }
            if(promocion1.tipoPromocion=="Reintegro" || promocion1.tipoPromocion=="Descuento"){
                text1=text1+"%"
            }
            else if (promocion1.tipoPromocion=="Cuotas"){
                text1=text1+" cuotas sin interes"
            }
            if(promocion2.tipoPromocion=="Reintegro" || promocion2.tipoPromocion=="Descuento"){
                text2=text2+"%"
            }
            else if (promocion2.tipoPromocion=="Cuotas"){
                text2=text2+" cuotas sin interes"
            }
            view.findViewById<TextView>(R.id.promoBenef1).text = text1
            view.findViewById<TextView>(R.id.promoBenef2).text = text2
            val instancia = Funciones()
            var tarjetasComunes1 = instancia.tarjetasComunes(userViewModel.usuario, promocion1)
            var tarjetasComunes2 = instancia.tarjetasComunes(userViewModel.usuario, promocion1)
            var lectura = LeerId()
            val coroutineScope = CoroutineScope(Dispatchers.Main)
            var textoTarjetas1 = ""
            var textoTarjetas2 = ""
            coroutineScope.launch {
                try {
                    for (tarjeta1 in tarjetasComunes1){
                        var tarj1 = lectura.obtenerTarjetaPorId(tarjeta1)
                        var ent1 = tarj1?.entidad?.let { lectura.obtenerEntidadPorId(it) }
                        if (ent1 != null) {
                            textoTarjetas1 = textoTarjetas1 + ent1.nombre +" "
                        }
                        if (tarj1 != null) {
                            textoTarjetas1 = if(tarj1.segmento=="No posee"){
                                textoTarjetas1 + tarj1.procesadora+" " +tarj1.tipoTarjeta
                            }else{
                                textoTarjetas1+tarj1.procesadora+" " +tarj1.segmento+" "+tarj1.tipoTarjeta
                            }
                        }
                        textoTarjetas1 += "\n"
                    }
                    for (tarjeta2 in tarjetasComunes2){
                        var tarj2 = lectura.obtenerTarjetaPorId(tarjeta2)
                        var ent2 = tarj2?.entidad?.let { lectura.obtenerEntidadPorId(it) }
                        if (ent2 != null) {
                            textoTarjetas2 = textoTarjetas2 + ent2.nombre+" "
                        }
                        if (tarj2 != null) {
                            textoTarjetas2 = if(tarj2.segmento=="No posee"){
                                textoTarjetas2+tarj2.procesadora+" " +tarj2.tipoTarjeta
                            }else{
                                textoTarjetas2+tarj2.procesadora+" " +tarj2.segmento+" "+tarj2.tipoTarjeta
                            }
                        }
                        textoTarjetas2 += "\n"
                    }
                    view.findViewById<TextView>(R.id.tarje1).text = textoTarjetas1
                    view.findViewById<TextView>(R.id.tarje2).text = textoTarjetas2
                }
                catch (e: Exception) {
                    println("Error al obtener promociones: ${e.message}")
                }

            }

        }
        }

    }
}