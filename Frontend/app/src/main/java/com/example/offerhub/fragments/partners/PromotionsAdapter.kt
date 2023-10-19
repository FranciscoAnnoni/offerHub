package com.example.offerhub.fragments.partners

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.offerhub.Promocion
import com.example.offerhub.R

class PromotionsAdapterPartners(private val promociones: List<Promocion>) :
    RecyclerView.Adapter<PromotionsAdapterPartners.PromotionViewHolder>() {

    inner class PromotionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val promotionTitle: TextView = itemView.findViewById(R.id.promotionTitle)
        val promotionEstado: TextView = itemView.findViewById(R.id.promotionEstado)
        val editButton: ImageButton = itemView.findViewById(R.id.editButton)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromotionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_promocion_partner, parent, false)
        return PromotionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PromotionViewHolder, position: Int) {
        val currentPromotion = promociones[position]
        holder.promotionTitle.text = currentPromotion.titulo
        holder.promotionEstado.text=currentPromotion.estado
        currentPromotion.estado?.let { Log.d("estado", it) }
        // Configurar acciones para los botones de editar y eliminar
        holder.editButton.setOnClickListener {
            // Acción de editar aquí
            // Puedes abrir un diálogo o fragmento de edición
        }

        holder.deleteButton.setOnClickListener {
            // Acción de eliminar aquí
            // Puedes mostrar un diálogo de confirmación y luego eliminar la promoción
        }
    }

    override fun getItemCount(): Int {
        return promociones.size
    }
}
