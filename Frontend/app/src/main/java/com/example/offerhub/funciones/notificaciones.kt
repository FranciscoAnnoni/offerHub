package com.example.offerhub.funciones

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.offerhub.fragments.shopping.PromoDetailFragment
import com.google.firebase.messaging.FirebaseMessaging
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService

class AlarmaNotificacion:BroadcastReceiver(){

    companion object {
        const val NOTIFICATION_ID = 1
    }

    override fun onReceive(context: Context, p1: Intent?) {
        createSimpleNotification(context)
    }



    fun createSimpleNotification(contexto:Context) {
        val intent = Intent(contexto, PromoDetailFragment::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val flag = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val pendingIntent:PendingIntent = PendingIntent.getActivity(contexto, 0, intent, flag)

        var noti = NotificationCompat.Builder(contexto, CanalNoti.MY_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_delete)
            .setContentTitle("Reintegro vencido")
            .setContentText("Es tiempo de que revises tu reintegro")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Tiempo de que revises el reintegro de la promocion") //agregar que promo es
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
