package com.example.offerhub.util


import android.view.View
import androidx.fragment.app.Fragment
import com.example.offerhub.R
import com.example.offerhub.activities.ShoppingActivity
import com.example.offerhub.activities.ShoppingAdminActivity
import com.example.offerhub.activities.ShoppingPartnersActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hideBottomNavigationView(){
    val bottomNavigationView =
        (activity as ShoppingActivity).findViewById<BottomNavigationView>(
            com.example.offerhub.R.id.bottomNavigation
        )
    bottomNavigationView.visibility = android.view.View.GONE
}

fun Fragment.showBottomNavigationView(){
    val bottomNavigationView =
        (activity as ShoppingActivity).findViewById<BottomNavigationView>(
            com.example.offerhub.R.id.bottomNavigation
        )
    bottomNavigationView.visibility = android.view.View.VISIBLE
}

fun Fragment.hideBottomNavigationViewPartners(){
    val bottomNavigationView =
        (activity as ShoppingPartnersActivity).findViewById<BottomNavigationView>(
            com.example.offerhub.R.id.bottomNavigationPartners
        )
    bottomNavigationView.visibility = android.view.View.GONE
}

fun Fragment.showBottomNavigationViePartners(){
    val bottomNavigationView =
        (activity as ShoppingPartnersActivity).findViewById<BottomNavigationView>(
            com.example.offerhub.R.id.bottomNavigationPartners
        )
    bottomNavigationView.visibility = android.view.View.VISIBLE
}


fun Fragment.showBottomNavigationVieAdmin(){
    val bottomNavigationView =
        (activity as ShoppingAdminActivity).findViewById<BottomNavigationView>(
            com.example.offerhub.R.id.bottomNavigationAdmin
        )
    bottomNavigationView.visibility = android.view.View.VISIBLE
}



fun Fragment.hideBottomNavigationViewAdmin(){
    val bottomNavigationView =
        (activity as ShoppingAdminActivity).findViewById<BottomNavigationView>(
            com.example.offerhub.R.id.bottomNavigationAdmin
        )
    bottomNavigationView.visibility = android.view.View.GONE
}