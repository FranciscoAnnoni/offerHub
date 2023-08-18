package com.example.offerhub.usuario

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.textfield.TextInputEditText
import android.widget.Toast
import android.text.TextUtils

import android.view.View

import com.google.firebase.auth.FirebaseAuth
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.example.offerhub.MainActivity
import com.example.offerhub.R


class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var buttonReg: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var textView: TextView


    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar)


        editTextEmail = findViewById(R.id.email)
        editTextPassword = findViewById(R.id.password)
        buttonReg = findViewById(R.id.btn_register)

        auth = FirebaseAuth.getInstance()
        progressBar = findViewById(R.id.progressBar)

        textView = findViewById(R.id.loginNow)


        textView.setOnClickListener {
            intent = Intent(this@Register, Login::class.java)
            startActivity(intent)
            finish()
        }


        buttonReg.setOnClickListener {
            progressBar.visibility = View.VISIBLE

            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this@Register, "Enter Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this@Register, "Enter password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    progressBar.visibility = View.GONE
                    if (task.isSuccessful) {

                        Toast.makeText(
                            baseContext,
                            "Authentication Account Created.",
                            Toast.LENGTH_SHORT,
                        ).show()

                        intent = Intent(applicationContext, Login::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()

                    }
                }

            // Your code here when the button is clicked
        }

        // Now you can use the editTextEmail, editTextPassword, and buttonReg variables as needed.
    }
}




