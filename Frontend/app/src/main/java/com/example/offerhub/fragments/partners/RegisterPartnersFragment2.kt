package com.example.offerhub.fragments.partners

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.offerhub.R
import com.example.offerhub.data.UserPartner
import com.example.offerhub.databinding.FragmentRegisterPartners2Binding
import com.example.offerhub.util.RegisterValidation
import com.example.offerhub.util.Resource
import com.example.offerhub.viewmodel.RegisterPartnersViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import android.util.Base64
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.offerhub.Comercio
import com.example.offerhub.funciones.FuncionesPartners
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class RegisterPartnersFragment2: Fragment() {
    private lateinit var imageActivityResultLauncher: ActivityResultLauncher <Intent>

    private lateinit var binding: FragmentRegisterPartners2Binding

    private val viewModel by viewModels<RegisterPartnersViewModel>()

    private var imageUri: Uri? = null

    private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        imageActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                imageUri = it.data?.data
                Glide.with(this).load(imageUri).into(binding.imageUser)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterPartners2Binding.inflate(inflater)
        return binding.root
    }

    fun vectorToBitmap(vectorDrawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return bitmap
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rootView = view // Asignar la vista raíz del fragmento

        // Recupera el Spinner
        val spinner = binding.spinner

        // Define las opciones como una lista de cadenas de texto
        val options = listOf("Seleccione una Opcion", "Gastronomía", "Salud y Bienestar", "Viajes y Turismo", "Entretenimiento", "Indumentaria", "Supermercados", "Electrónica", "Librerías", "Mascotas", "Niños", "Otros" )

        // Crea un ArrayAdapter para el Spinner
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        spinner.adapter = adapter


        // ACA ES DONDE TENGO QUE VERLO CON FACU PARA QUE ESTOS VALORES SE LOS ASIGNE A UN USER= USER-PARTHENRS
        binding.apply {
            btnRegisterEmpresa.setOnClickListener {
                val userLogeado = FirebaseAuth.getInstance().currentUser
                val selectedOption = spinner.selectedItem as String

                // Obtén el ImageView
                val imageView = binding.imageUser

// Obtén el Drawable del ImageView
                val drawable = imageView.drawable
                val bitmap: Bitmap
// Convierte el Drawable a un Bitmap
                if (drawable is VectorDrawable) {
                    bitmap = vectorToBitmap(drawable)
                }else{
                    bitmap = (drawable as BitmapDrawable).bitmap
                }


// Convierte el Bitmap a una cadena base64
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()
                val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)
                val coroutineScope = CoroutineScope(Dispatchers.Main)
                val instancia = FuncionesPartners()
                var comercio = Comercio(null, edNombreRegisterDeEmpresa.text.toString().trim(),selectedOption,base64String,edCuilRegisterDeEmpresa.text.toString().trim(),null)
                coroutineScope.launch {
                    val idComercio = instancia.registrarComercio(comercio,null)
                    val user = UserPartner(
                        edNombreRegisterDeEmpresa.text.toString().trim(),
                        edCuilRegisterDeEmpresa.text.toString().trim(),
                        userLogeado?.email.toString().trim(),
                        idComercio,
                        )
                    viewModel.createAccountUserPartner(user, selectedOption)
                }
            }

        }

        binding.imageUser.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            imageActivityResultLauncher.launch(intent)
        }


        // VOLVER A LA PANTALLA DE ATRAS
        binding.atras.setOnClickListener {
            viewModel.deleteUser()
            //tengo que cerrar sesion, borrar el usuario y mandarlo al register de empresa de nuevo
            findNavController().navigate(R.id.registerPartnersFragment)
        }

        // ACA ES DONDE EL VIEW MODEL DEVUELVE EL USUARIO CREADO Y CONFIRMA QUE SE CREO CORRECTAMENTE
        lifecycleScope.launchWhenStarted {
            viewModel.registerUser.collect{
                when(it){
                    is Resource.Loading -> {
                        binding.btnRegisterEmpresa.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.btnRegisterEmpresa.revertAnimation()
                        Snackbar.make(rootView, "Registro de Empresa exitoso", Snackbar.LENGTH_SHORT).show()
                        viewModel.logout()
                        findNavController().navigate(R.id.loginPartnersFragment)
                    }
                    is Resource.Error -> {
                        binding.btnRegisterEmpresa.revertAnimation()
                        binding.btnRegisterEmpresa.setBackgroundResource(R.drawable.rounded_button_background)
                    }
                    else -> Unit
                }
            }
        }

        // ACA VALIDO QUE LA CONTRRASENIA Y EL MAIL SEAN CORRECTOS
        lifecycleScope.launchWhenStarted {
            viewModel.validationUser.collect {
                validation ->
                if (validation.cuil is RegisterValidation.Failed){
                    withContext(Dispatchers.Main) {
                        binding.edCuilRegisterDeEmpresa.apply {
                            requestFocus()
                            error = validation.cuil.message
                        }
                    }
                }

                if (validation.categoria is RegisterValidation.Failed){
                    withContext(Dispatchers.Main) {
                        binding.spinnerErrorText.apply {
                            requestFocus()
                            error = validation.categoria.message
                        }
                    }
                }
            }
        }

    }

}