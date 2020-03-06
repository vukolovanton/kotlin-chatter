package com.example.chatter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_settings.*

class ProfileActivity : AppCompatActivity() {
    var mCurrentUser: FirebaseUser? = null
    var mUserDatabase: DatabaseReference? = null
    var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        title = "Profile"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if (intent.extras != null) {
            userId = intent.extras!!.get("userId").toString()
            Log.d("PROFILE", "ЛУЧШЕ БАТИ ТОЛЬКО $userId")

            mCurrentUser = FirebaseAuth.getInstance().currentUser
            mUserDatabase = FirebaseDatabase.getInstance().reference.child("users").child(userId!!)

            setupProfile()
        }
    }

    private fun setupProfile() {
        mUserDatabase!!.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var displayName = dataSnapshot.child("display_name").value.toString()
                var status = dataSnapshot.child("status").value.toString()
                var image = dataSnapshot.child("image").value.toString()

                profileName.text = displayName
                profileStatus.text = status

                Picasso.get().load(image).placeholder(R.drawable.profile_img).into(profilePicture)

            }

        })
    }
}
