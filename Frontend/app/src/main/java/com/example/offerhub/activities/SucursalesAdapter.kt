package com.example.offerhub.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.offerhub.R
import com.example.offerhub.Sucursal

class SucursalesAdapter(private val sucursales: List<Sucursal?>) : RecyclerView.Adapter<SucursalesAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sucursalTextView: TextView = itemView.findViewById(R.id.textSucursalName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_sucursal, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sucursal = sucursales[position]
        if (sucursal != null) {
            holder.sucursalTextView.text = sucursal.direccion
        }
    }

    override fun getItemCount(): Int {
        return sucursales.size
    }
}
