package com.example.offerhub.fragments.partners

import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.google.firebase.database.FirebaseDatabase

class PromotionsAdapterPartners(private val context: HomePartnersFragment, private val promociones: MutableList<Promocion>, private val editable: Boolean, private val homePartnersFragment: HomePartnersFragment) :
    RecyclerView.Adapter<PromotionsAdapterPartners.PromotionViewHolder>() {

    inner class PromotionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val promotionTitle: TextView = itemView.findViewById(R.id.promotionTitle)
        val promotionEstado: ImageView = itemView.findViewById(R.id.promotionEstado)
        val estadoText: TextView = itemView.findViewById(R.id.estadoText)
        val previewButton: ImageButton = itemView.findViewById(R.id.previewButton)
        val editButton: ImageButton = itemView.findViewById(R.id.editButton)
        val btnVerMotivo: CircularProgressButton = itemView.findViewById(R.id.btnVerMotivo)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }
    fun removePromo(promo: Promocion) {
        promociones.remove(promo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromotionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_promocion_partner, parent, false)
        return PromotionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PromotionViewHolder, position: Int) {
        val currentPromotion = promociones[position]
        if (currentPromotion.estado!!.lowercase()=="aprobado"){
            holder.previewButton.visibility = View.VISIBLE
        } else {
            holder.previewButton.visibility = View.GONE
        }

        if(currentPromotion.estado!!.lowercase()=="rechazado"){
            holder.estadoText.text = "Motivo"
            holder.promotionEstado.visibility=View.GONE
            holder.btnVerMotivo.visibility=View.VISIBLE
        } else {
            holder.promotionEstado.setImageResource(currentPromotion.obtenerEstadoIcono())
            holder.promotionEstado.setColorFilter(ContextCompat.getColor(context.requireContext(), currentPromotion.obtenerEstadoColor()), PorterDuff.Mode.SRC_IN)

            holder.btnVerMotivo.visibility=View.GONE
        }

        var tituloConComercio=currentPromotion.titulo!!.split(":")
        var titulo = tituloConComercio.getOrNull(1)?.trim()
        holder.promotionTitle.text = if(titulo!=null) titulo else currentPromotion.titulo

        currentPromotion.estado?.let { Log.d("estado", it) }
        // Configurar acciones para los botones de editar y eliminar
        holder.editButton.setOnClickListener {
            homePartnersFragment.editPromocion(currentPromotion)
        }

        holder.deleteButton.setOnClickListener {
            val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
            val promocionRef =
                currentPromotion.id?.let { it1 -> database.getReference("/Promocion").child(it1) }

            if (promocionRef != null) {
                promocionRef.removeValue()
            }
            removePromo(currentPromotion)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return promociones.size
    }
}
