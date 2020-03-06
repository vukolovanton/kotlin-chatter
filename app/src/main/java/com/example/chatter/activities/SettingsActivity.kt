package com.example.chatter.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chatter.R
import com.example.chatter.activities.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private var mCurrentUser: FirebaseUser? = null
    private lateinit var mStorageRef: StorageReference
    private var GALLERY_ID: Int = 1

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
                //Получаем референс из бд
                val displayName = dataSnapshot.child("display_name").value
                val image = dataSnapshot.child("image").value
                val userStatus = dataSnapshot.child("status").value
                //Устанавливаем текст в поля
                settingsDisplayName.text = displayName.toString()
                settingsStatustextId.text = userStatus.toString()
                //Перезаписываем аватар пользователя
                if (!image!!.equals("default")) {
                    Picasso.get().load(image.toString()).into(usersProfileImg)
                }
            }
        })
        //Запускаем новый интент и передаем в него статус
        settingsChangeStatusId.setOnClickListener {
            val intent = Intent(this, StatusActivity::class.java)
            intent.putExtra("status", settingsStatustextId.text.toString().trim())
            startActivity(intent)
        }
        //Прослушиваем клик на кнопку "загрузить картинку"
        settingsChangeImageButtonId.setOnClickListener {
            //Интент лезет в галерею и сохраняет uri картинки
            val galleryIntent = Intent()
            galleryIntent.type = "image/*"
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(galleryIntent, "SELECT_IMAGE"), GALLERY_ID)
        }
    }

    //Если слазели в галерею удачно
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Ага, наш интент и всё ок
        if (requestCode == GALLERY_ID && resultCode == Activity.RESULT_OK) {
            //Сохраняем uri
            val image: Uri? = data!!.data
            //Кропаем
            CropImage.activity(image).setAspectRatio(1, 1).start(this)
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            //Кропнули удачно - записываем результат
            val result = CropImage.getActivityResult(data)

            if (resultCode == Activity.RESULT_OK) {
                //Записываем uri кропнутой картинки
                val resultUri = result.uri
                val userId = mCurrentUser!!.uid
//                var thumbFile = File(resultUri.path)
                //По id находим в бд юзера, который полез менять картинку
                val filePath = mStorageRef.child("chat_profile_images").child("$userId.jpg")
                filePath.putFile(resultUri).addOnSuccessListener {
                    filePath.downloadUrl
                        .addOnSuccessListener { uri ->
                            //Создаем копию объекта пользователя и перезаписываем его в бд, но уже с новой картинкой
                            val updateObj = HashMap<String, Any>()
                                updateObj["image"] = uri.toString()
                                mDatabase.updateChildren(updateObj)
                        }
                }
            }
        }
    }


}
