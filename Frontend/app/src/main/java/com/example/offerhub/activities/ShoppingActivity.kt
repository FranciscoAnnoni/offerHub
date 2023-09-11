package com.example.offerhub.activities
import CategoryGridAdapter
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.offerhub.Comercio
import com.example.offerhub.InterfaceSinc
import com.example.offerhub.LecturaBD
import com.example.offerhub.R
import com.example.offerhub.data.Categoria
import com.example.offerhub.databinding.ActivitySearchBinding
import com.example.offerhub.databinding.ActivityShoppingBinding
import com.example.offerhub.databinding.FragmentSearchBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShoppingActivity : AppCompatActivity() {

    val binding by lazy {
        FragmentSearchBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.fragment_search) // Asegúrate de que este sea el nombre de tu archivo de diseño de actividad.
        setContentView(binding.root) // Asegúrate de que este sea el nombre de tu archivo de diseño de actividad.
        var instancia = InterfaceSinc()
        val listView = findViewById<GridView>(R.id.gridView) // Reemplaza "listView" con el ID de tu ListView en el XML.
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        var datos: MutableList<String> = mutableListOf()
        // Llamar a la función que obtiene los datos.
        val job = coroutineScope.launch {
            try {
                //val datos: List<Comercio> =
                  //  instancia.leerBdClaseSinc("Comercio", "categoria", "Gastronomía")
                val datos = listOf(
                            Categoria(this@ShoppingActivity,"Gastronomía","cat_gastronomia"),
                            Categoria(this@ShoppingActivity,"Vehículos","cat_vehiculos"),
                            Categoria(this@ShoppingActivity,"Salud y Bienestar","cat_salud_y_bienestar"),
                            Categoria(this@ShoppingActivity,"Hogar","cat_hogar"),
                            Categoria(this@ShoppingActivity,"Viajes y Turismo","cat_viajes"),
                            Categoria(this@ShoppingActivity,"Entretenimiento","cat_entretenimiento"),
                            Categoria(this@ShoppingActivity,"Indumentaria","cat_indumentaria"),
                            Categoria(this@ShoppingActivity,"Supermercados","cat_supermercados"),
                            Categoria(this@ShoppingActivity,"Electrónica","cat_electronica"),
                            Categoria(this@ShoppingActivity,"Educación","cat_educacion"),
                            Categoria(this@ShoppingActivity,"Niños","cat_ninos"),
                            Categoria(this@ShoppingActivity,"Regalos","cat_regalos"),
                            Categoria(this@ShoppingActivity,"Bebidas","cat_bebidas"),
                            Categoria(this@ShoppingActivity,"Joyería","cat_joyeria"),
                            Categoria(this@ShoppingActivity,"Librerías","cat_librerias"),
                            Categoria(this@ShoppingActivity,"Mascotas","cat_mascotas"),
                            Categoria(this@ShoppingActivity,"Servicios","cat_servicios"),
                            Categoria(this@ShoppingActivity,"Otros","cat_otros")
                    // Agrega más elementos según sea necesario
                )
                val adapter =  CategoryGridAdapter(this@ShoppingActivity, datos)
                listView.adapter = adapter
            }
            catch (e: Exception) {
                println("Error al obtener promociones: ${e.message}")
            }
        }

        // Crear un adaptador para el ListView.


        // Establecer el adaptador en el ListView.
        //val navController = findNavController(R.id.mainAppFragment)
        //binding.bottomNavigation.setupWithNavController(navController)
    }
}