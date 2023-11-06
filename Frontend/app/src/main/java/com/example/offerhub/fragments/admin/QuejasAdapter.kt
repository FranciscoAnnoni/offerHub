package com.example.offerhub.fragments.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class QuejasAdapter(private val quejas: MutableList<String>) : RecyclerView.Adapter<QuejasAdapter.QuejaViewHolder>() {

    class QuejaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textoQueja: TextView = itemView.findViewById(R.id.textoQueja)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }

    fun removeQueja(queja: String) {
        quejas.remove(queja)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuejaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_queja, parent, false)
        return QuejaViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: QuejaViewHolder, position: Int) {
        val queja = quejas[position]
        holder.textoQueja.text = queja
        holder.deleteButton.setOnClickListener {
            val databaseReference = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com").reference.child("Reportes")
            databaseReference.orderByValue().equalTo(queja).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (childSnapshot in dataSnapshot.children) {
                        val key = childSnapshot.key
                        databaseReference.child(key!!).removeValue()
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
            removeQueja(queja)
            notifyDataSetChanged()
        }
    }


    override fun getItemCount(): Int {
        return quejas.size
    }
}
