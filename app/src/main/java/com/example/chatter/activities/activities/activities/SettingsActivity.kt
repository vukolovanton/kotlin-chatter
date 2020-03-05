package com.example.chatter.activities.activities.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.chatter.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.coroutines.launch
import java.io.File

class SettingsActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private var mCurrentUser: FirebaseUser? = null
    private lateinit var mStorageRef: StorageReference
    var GALLERY_ID: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        title = "Settings";

        mCurrentUser = FirebaseAuth.getInstance().currentUser
        mStorageRef = FirebaseStorage.getInstance().reference

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

        settingsChangeImageButtonId.setOnClickListener {
            var galleryIntent = Intent()
            galleryIntent.type = "image/*"
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(galleryIntent, "SELECT_IMAGE"), GALLERY_ID)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_ID && resultCode == Activity.RESULT_OK) {
            var image: Uri? = data!!.data
            CropImage.activity(image).setAspectRatio(1, 1).start(this)
        }

        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)

            if (resultCode === Activity.RESULT_OK) {
                //Оригинальный файл
                val resultUri = result.uri

                var userId = mCurrentUser!!.uid
                var thumbFile = File(resultUri.path)

//                lifecycleScope.launch {
//                    var compressedImageFile  = Compressor.compress(applicationContext, thumbFile) {
//                        resolution(200, 200)
//                        quality(80)
//                        format(Bitmap.CompressFormat.WEBP)
//                    }
//                }

                var filePath = mStorageRef!!.child("chat_profile_images").child("$userId.jpg")
                filePath.putFile(resultUri)

            }
        }
    }
}
