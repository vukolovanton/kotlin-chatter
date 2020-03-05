package com.example.chatter.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chatter.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_status.*

class StatusActivity : AppCompatActivity() {
    private lateinit var mDatabase: DatabaseReference
    private var mCurrentUser: FirebaseUser? = null
    var mStorageRef: StorageReference? = null

    var GALLERY_ID: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)

        title = "Status";

        //Проверяем, что в интенте что-то есть
        if (intent.extras != null) {
            //Показываем старый статус
            val oldStatus = intent.extras!!.get("status")
            statusUpdateEt.setText(oldStatus.toString())
        }
        //Если ничего в статусе и не было
        if (intent.extras!!.equals(null)) {
            statusUpdateEt.setText("Enter Your New Status")
        }
        //Начинаем апдейт по клику
        statusUpdateButton.setOnClickListener {
            //Получаем юзера
            mCurrentUser = FirebaseAuth.getInstance().currentUser
            val userId = mCurrentUser!!.uid
            //Заходим в юзера в бд
            mDatabase = FirebaseDatabase.getInstance().reference.child("users").child(userId)
            //Получаем текст из поля ввода
            val status = statusUpdateEt.text.toString().trim()
            //Устанавлиаем новое значение и проверяем всё ли хорошо
            mDatabase.child("status").setValue(status).addOnCompleteListener{
                task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Status Updated Successfully!", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
                else {
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
                }
            }
        }

//        settingsChangeImageButtonId.setOnClickListener {
//            var galleryIntent = Intent()
//            galleryIntent.type = "image/*"
//            galleryIntent.action = Intent.ACTION_GET_CONTENT
//            startActivityForResult(Intent.createChooser(galleryIntent, "SELECT_IMAGE"), GALLERY_ID)
//        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == GALLERY_ID && resultCode == Activity.RESULT_OK) {
//            var image: Uri? = data!!.data
//            CropImage.activity(image).setAspectRatio(1, 1).start(this)
//        }
//
//        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            val result = CropImage.getActivityResult(data)
//
//            if (resultCode === Activity.RESULT_OK) {
//                //Оригинальный файл
//                val resultUri = result.uri
//
//                var userId = mCurrentUser!!.uid
//                var thumbFile = File(resultUri.path)
//
//                lifecycleScope.launch {
//                    var compressedImageFile  = Compressor.compress(applicationContext, thumbFile) {
//                        resolution(200, 200)
//                        quality(80)
//                        format(Bitmap.CompressFormat.WEBP)
//                    }
//                }
//
//                var filePath = mStorageRef!!.child("chat_profile_images").child("$userId.jpg")
//                filePath.putFile(resultUri)
//
//            }
//        }
//    }
}
