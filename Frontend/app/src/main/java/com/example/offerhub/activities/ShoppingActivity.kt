package com.example.offerhub.activities
import CategoryGridAdapter
import UserViewModel
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ListView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.offerhub.Comercio
import com.example.offerhub.InterfaceSinc
import com.example.offerhub.KelineApplication
import com.example.offerhub.LecturaBD
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.example.offerhub.data.Categoria
import com.example.offerhub.databinding.ActivityShoppingBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {
    var listadoDePromosDisp: List<Promocion> = listOf()
    val binding by lazy {
        ActivityShoppingBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root) // Asegúrate de que este sea el nombre de tu archivo de diseño de actividad.
        val navController = findNavController(R.id.mainAppFragment)
        binding.bottomNavigation.setupWithNavController(navController)
        //setContentView(R.layout.fragment_search) // Asegúrate de que este sea el nombre de tu archivo de diseño de actividad.


        // Crear un adaptador para el ListView.


        // Establecer el adaptador en el ListView.


    }
}