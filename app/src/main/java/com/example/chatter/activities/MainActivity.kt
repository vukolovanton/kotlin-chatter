package com.example.chatter.activities

import com.example.chatter.activities.activities.activities.CreateAccountActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chatter.R
import com.example.chatter.activities.activities.activities.DashboardActivity
import com.example.chatter.activities.activities.activities.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    var user: FirebaseUser? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        //Начинаем прослушивать текущего юзера
        mAuthListener = FirebaseAuth.AuthStateListener {
            firebaseAuth ->
            //Получаем юзера
            user = firebaseAuth.currentUser
            //Проверяем, если он залогинен
            if (user != null) {
                //Сразу в дашборд
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Not sign in", Toast.LENGTH_LONG).show()
            }
        }

        createAccountButton.setOnClickListener{
            startActivity(Intent(this, CreateAccountActivity::class.java))
        }

        loginButton.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(mAuthListener!!)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener!!)
        }
    }

}
