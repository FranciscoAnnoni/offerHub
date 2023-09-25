package com.example.offerhub.funciones

import UserViewModel
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.firebase.messaging.FirebaseMessaging
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import com.example.offerhub.Funciones
import com.example.offerhub.LeerId
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.example.offerhub.activities.PromoNotiDetailActivity
import com.example.offerhub.viewmodel.UserViewModelCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmaNotificacion:BroadcastReceiver(){

    companion object {
        const val NOTIFICATION_ID = 1
    }

    override fun onReceive(context: Context ,p1: Intent?) {
        val mensaje = p1?.getStringExtra("comercio")
        val promo = p1?.getStringExtra("promocion")
        val instancia = Funciones()
        var userViewModel :UserViewModel=UserViewModel()
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        fun comparar(it: Promocion): Boolean {
            return it.id == promo
        }
        userViewModel.reintegros.removeIf { it->comparar(it) }
        UserViewModelCache().guardarUserViewModel(userViewModel)
        coroutineScope.launch {
            if (promo != null) {
                instancia.elimiarPromocionDeReintegro(
                    userViewModel.id.toString(),
                    promo
                )
            }
        }

        createSimpleNotification(context, mensaje,promo)

    }

    fun createSimpleNotification(contexto:Context, comercio:String?,promo:String?)
    {

        val intent = Intent(contexto, PromoNotiDetailActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val prefs = contexto.getSharedPreferences("NotiReintegro", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("promocion", promo)
        editor.apply()




        val flag = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val pendingIntent:PendingIntent = PendingIntent.getActivity(contexto, 0, intent, flag)
        val smallIcon = IconCompat.createWithBitmap(BitmapFactory.decodeResource(contexto.resources, R.drawable.offerhub_logo))
        var noti = NotificationCompat.Builder(contexto, CanalNoti.MY_CHANNEL_ID)
            .setSmallIcon(smallIcon)
            .setContentTitle("Reintegro en ${comercio}")
            .setContentText("Revisá tu reintegro")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Es el momento de chequear tu reintegro de la promocion que usaste en ${comercio}") //agregar que promo es
            )
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val manager = contexto.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, noti)
    }

    fun notificacion(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                // Manejar el error
                return@addOnCompleteListener
            }

            // Obtener el token
            val token = task.result
            println("token del dispositivo: ${token}")
            // Haz lo que necesites con el token
        }
    }
}


class CanalNoti{
    companion object {
        const val MY_CHANNEL_ID = "myChannel"
    }

    fun createChannel(contexto:Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                MY_CHANNEL_ID,
                "MySuperChannel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "AHORRA YA"
            }

            val notificationManager: NotificationManager =
                contexto.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }
}
