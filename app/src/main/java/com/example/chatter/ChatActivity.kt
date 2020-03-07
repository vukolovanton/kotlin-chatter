package com.example.chatter


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatter.activities.models.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*


 class ChatActivity : AppCompatActivity() {
    private var userId: String? = null
    private var userImg: String? = null
    private var myImg: String? = null
    private var currentUserId: String? = null
    private lateinit var mFirebaseDatabaseRef: DatabaseReference
    private var mFirebaseUser: FirebaseUser? = null
    private var mLinearLayoutManager: LinearLayoutManager? = null
     private var toUser: String? = null

    val adapter = GroupAdapter<GroupieViewHolder>()

     //Callback-функция, чтобы асинхронно получить юзера из бд и использовать его в основном классе
     //Лучше было использовать companion object, но пока так
    interface MyCallback {
        fun onCallback(value: String)
    }
    private fun readData(myCallback: MyCallback) {
        mFirebaseDatabaseRef = FirebaseDatabase.getInstance().reference.child("users").child(currentUserId!!)
        mFirebaseDatabaseRef
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val image = dataSnapshot.child("image").value.toString()
                    myCallback.onCallback(image)
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        mFirebaseUser = FirebaseAuth.getInstance().currentUser
        currentUserId = mFirebaseUser!!.uid
        userId = intent.extras?.getString("userId")
        userImg = intent.extras?.getString("profile")
//        toUser = userId
        mLinearLayoutManager = LinearLayoutManager(this)
        mLinearLayoutManager!!.stackFromEnd = true
        //Получаем картинку текущего юзера из колбека
        readData(object : MyCallback {
            override fun onCallback(value: String) {
                val myImg = value
                //Фетчим список сообщений
                listenForMessages(userImg!!, myImg)
            }
        })
        //Устанавливаем наш основной адаптер recyclerView
        recyclerviewChatLog.adapter = adapter

        sendButton.setOnClickListener{
                //Отправляем сообщение
                preformSendMessage(userId)
        }
    }
     //Сообщения
    private fun listenForMessages(userImg: String, myImg: String) {

        val fromId = FirebaseAuth.getInstance().uid
        val toId = userId
        //Куда будем записывать сообщения
        val mReference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        mReference.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                //Помещаем дату с сервера в объект сообщения
                val chatMessage = p0.getValue(ChatMessage::class.java)
                if (chatMessage != null) {
                    //Устанавливаем адаптер
                    //Если сообщение от нас
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        adapter.add(ChatToItem(chatMessage.text!!, userImg))
                        recyclerviewChatLog.scrollToPosition(adapter.itemCount - 1)
                        //Или не от нас, вставояем в разные адаптеры
                    } else {

                        adapter.add(ChatFromItem(chatMessage.text!!, myImg))
                    }
                }
            }
            override fun onChildRemoved(p0: DataSnapshot) {}
        })
    }
    //Обрабатываем отправку сообщений
    private fun preformSendMessage(intentToId: String?) {
        val text = messageEdit.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val toId = intentToId

        if (text.trim() == "") {
            return
        }
        //Два пути, чтобы видеть отправленные и полученные сообщения
        val mReference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
        //Собираем сообщение из конструктора класса
        val chatMessage = ChatMessage(mReference.key, text, fromId, toId, System.currentTimeMillis()/1000)
        //Отправляем сообщение в бд
        mReference.setValue(chatMessage).addOnSuccessListener {
            //Очищаем поле ввода и скроллим вниз
            messageEdit.text.clear()
            recyclerviewChatLog.scrollToPosition(adapter.itemCount - 1)
        }
        toReference.setValue(chatMessage)
    }

     override fun onSupportNavigateUp(): Boolean {
         onBackPressed()
         return true
     }

}

class ChatFromItem(val text: String, private val myAvatar: String): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.chatFromText.text = text
        Picasso.get().load(myAvatar).placeholder(R.drawable.profile_img).into(viewHolder.itemView.chatFromImage)
    }
}

class ChatToItem(val text: String, private val toAvatar: String): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.chatToText.text = text
        Picasso.get().load(toAvatar).placeholder(R.drawable.profile_img).into(viewHolder.itemView.chatToImage)
    }
}
