package com.example.chatter.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.example.chatter.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private var pb: ProgressBar ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        pb = findViewById(R.id.loginProgressBar)
        
        loginButtonId.setOnClickListener {
            val email = loginEmailE.text.toString().trim()
            val password = loginPasswordE.text.toString().trim()
            if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {


                loginUser(email, password)
            } else {
                Toast.makeText(this, "Sorry, login failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        pb?.visibility = View.VISIBLE
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                task ->
                if (task.isSuccessful) {
                    var userName = email.split("@")[0]

                    var dashboardIntent = Intent(this, DashboardActivity::class.java)
                    dashboardIntent.putExtra("name", userName)
                    startActivity(dashboardIntent)
                    finish()
                } else {
                    Toast.makeText(this, "Login failed", Toast.LENGTH_LONG).show()
                }
            }
    }
}
