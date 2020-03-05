package com.example.chatter.activities.activities.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.chatter.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create_account.*


class CreateAccountActivity : AppCompatActivity() {
    //Переменные для аутентификации и бд
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        mAuth = FirebaseAuth.getInstance()

        //Прослушиваем клик на кнопку в активности лоигна
        accountCreateAccountButton.setOnClickListener {
            //Получаем текст из полей
            var email = accountEmailE.text.toString().trim()
            var password = accountPasswordE.text.toString().trim()
            var displayName = accountDisplayNameE.text.toString().trim()

            if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password) || !TextUtils.isEmpty(displayName)) {
                createAccount(email, password, displayName)
            } else {
                Toast.makeText(this, "Please fill out the fields", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun createAccount(email: String, password: String, displayName: String) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this){
                task ->
            if (task.isSuccessful) {
                //sign-in
                var currentUser = mAuth.currentUser
                var userId = currentUser!!.uid

                mDatabase = FirebaseDatabase.getInstance().reference.child("users").child(userId)

                var userObject = HashMap<String, String>()
                userObject["display_name"] = displayName
                userObject["status"] = "Hello there"
                userObject["image"] = "default"
                userObject["thumb_image"] = "default"

                Log.d("AUTH", "createUserWithEmail:success")
                //Пуляем юзера в бд
                mDatabase.setValue(userObject).addOnCompleteListener {
                    task ->
                    if (task.isSuccessful) {
                        var dashboardIntent = Intent(this, DashboardActivity::class.java)
                        dashboardIntent.putExtra("name", displayName)
                        startActivity(dashboardIntent)
                        finish()
                    } else {
                        Toast.makeText(this, "Login failed", Toast.LENGTH_LONG).show()
                    }
                }

            } else {
                Log.w("AUTH", "createUserWithEmail:failure", task.exception)
            }

        }
    }
}
