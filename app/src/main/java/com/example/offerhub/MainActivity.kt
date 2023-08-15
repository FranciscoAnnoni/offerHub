package com.example.offerhub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.offerhub.ui.theme.OfferHubTheme
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var buttonLogOut: Button
    private lateinit var textView: TextView
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        buttonLogOut = findViewById(R.id.logOut)

        textView = findViewById(R.id.user_details)
        user = auth.currentUser!!
        if (user == null){
            intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
            finish()
        }
        else{
            textView.setText(user.email)
        }

        buttonLogOut.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
            finish()
        }



    }
}















@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OfferHubTheme {
        Greeting("Android")
    }
}
