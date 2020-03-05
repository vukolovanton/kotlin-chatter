package com.example.chatter.activities.activities.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatter.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private var mCurrentUser: FirebaseUser? = null
    private lateinit var mStorageRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        title = "Settings";

        mCurrentUser = FirebaseAuth.getInstance().currentUser

        val userId = mCurrentUser!!.uid
        //Доступ к конкретному юзеру
        mDatabase = FirebaseDatabase.getInstance().reference.child("users").child(userId)
        mDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseErrorSnapshot: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //Получаем данные из бд
                var displayName = dataSnapshot.child("display_name").value
                var image = dataSnapshot.child("image").value
                var userStatus = dataSnapshot.child("status").value
                var thumbnail = dataSnapshot.child("thumb_image").value
                //Устанавливаем текст в поля
                settingsDisplayName.text = displayName.toString()
                settingsStatustextId.text = userStatus.toString()
            }
        })
        //Запускаем новый интент и передаем в него статус
        settingsChangeStatusId.setOnClickListener {
            val intent = Intent(this, StatusActivity::class.java)
            intent.putExtra("status", settingsStatustextId.text.toString().trim())
            startActivity(intent)
        }
    }
}
