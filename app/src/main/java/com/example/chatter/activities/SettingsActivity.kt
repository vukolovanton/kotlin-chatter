package com.example.chatter.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.example.chatter.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_settings.*
import java.io.ByteArrayOutputStream
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

                if (!image!!.equals("default")) {
                    Picasso.get().load(image.toString()).into(settingsProfileImg)
                }
            }
        })
        //Запускаем новый интент и передаем в него статус
        settingsChangeStatusId.setOnClickListener {
            val intent = Intent(this, StatusActivity::class.java)
            intent.putExtra("status", settingsStatustextId.text.toString().trim())
            startActivity(intent)
        }

        settingsChangeImageButtonId.setOnClickListener {
            val galleryIntent = Intent()
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



//                val bitmap =
//                    MediaStore.Images.Media.getBitmap(this.contentResolver, resultUri)
//                var byteArray = ByteArrayOutputStream()
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray)
//                var thumbByteArray: ByteArray
//                thumbByteArray = byteArray.toByteArray()

                var filePath = mStorageRef.child("chat_profile_images").child("$userId.jpg")
                //Путь для маленьких картинок
//                var thumbFilePath = mStorageRef!!.child("chat_profile_images").child("thumbs").child(userId + ".jpg")

//                var uploadTask = thumbFilePath.putBytes(thumbByteArray)


                filePath.putFile(resultUri).addOnSuccessListener {
                    filePath.downloadUrl
                        .addOnSuccessListener { uri ->
                            var updateObj = HashMap<String, Any>()
                                updateObj["image"] = uri.toString()
//                                updateObj["thumb_image"] = thumbUrl

                                mDatabase.updateChildren(updateObj)
                        }
                }

//                filePath.putFile(resultUri).addOnCompleteListener{
//                    task ->
//                    if (task.isSuccessful) {
//                        var downloadUrl = task.result.d
//
//                        var uploadTask: UploadTask = thumbFilePath.putBytes(thumbByteArray)
//
//                        uploadTask.addOnCompleteListener{
//                            secondTask ->
//                            var thumbUrl = task.result.toString()
//                            if (secondTask.isSuccessful) {
//                                var updateObj = HashMap<String, Any>()
//                                updateObj["image"] = downloadUrl
//                                updateObj["thumb_image"] = thumbUrl
//
//                                mDatabase.updateChildren(updateObj)
//                            }
//                        }
//                    }
//                }





            }
        }
    }
}
