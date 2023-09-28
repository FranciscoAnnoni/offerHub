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
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.offerhub.Comercio
import com.example.offerhub.InterfaceSinc
import com.example.offerhub.KelineApplication
import com.example.offerhub.LecturaBD
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.example.offerhub.data.Categoria
import com.example.offerhub.databinding.ActivityPartnersShoppingBinding
import com.example.offerhub.databinding.ActivityShoppingBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ShoppingPartnersActivity : AppCompatActivity() {
    private lateinit var navControllerPartrners: NavController
    val binding by lazy {
        ActivityPartnersShoppingBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root) // Asegúrate de que este sea el nombre de tu archivo de diseño de actividad.
        navControllerPartrners = findNavController(R.id.mainAppPartnersFragment)
        binding.bottomNavigation.setupWithNavController(navControllerPartrners)
        //setContentView(R.layout.fragment_search) // Asegúrate de que este sea el nombre de tu archivo de diseño de actividad.

        // Crear un adaptador para el ListView.

        // Establecer el adaptador en el ListView.


    }


}