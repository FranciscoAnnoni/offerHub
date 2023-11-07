package com.example.offerhub.fragments.admin

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class QuejasAdapter(
    private val quejas: MutableList<String>,
    private val context: Context,
    private val rootView: View
    ) : RecyclerView.Adapter<QuejasAdapter.QuejaViewHolder>() {

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
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Eliminar Error")
            builder.setMessage("¿Está seguro de que desea eliminar el error?\n Esta accion es permanente y no hay forma de revertirla")


            // Agregar un botón "Sí" para confirmar el cierre de sesión
            builder.setPositiveButton("Sí") { dialogInterface, _ ->
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
                Snackbar.make(rootView, "Se ha eliminado correctamente el error", Snackbar.LENGTH_LONG).show()

            }

            // Agregar un botón "No" para cancelar el cierre de sesión
            builder.setNegativeButton("No") { dialogInterface, _ ->
                // Cierra el cuadro de diálogo
                dialogInterface.dismiss()
            }

            // Mostrar el cuadro de diálogo
            val dialog: AlertDialog = builder.create()
            dialog.show()




        }
    }


    override fun getItemCount(): Int {
        return quejas.size
    }
}
