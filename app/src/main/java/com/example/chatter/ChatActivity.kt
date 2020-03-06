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

    interface MyCallback {
        fun onCallback(value: String)
    }
    fun readData(myCallback: MyCallback) {
        mFirebaseDatabaseRef = FirebaseDatabase.getInstance().reference.child("users").child(currentUserId!!)
        mFirebaseDatabaseRef
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val image = dataSnapshot.child("image").value.toString()
                    Log.d("TEST2", "Inside $myImg")
                    myCallback.onCallback(image)
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        mFirebaseUser = FirebaseAuth.getInstance().currentUser
        currentUserId = mFirebaseUser!!.uid
        userId = intent.extras?.getString("userId")
        userImg = intent.extras?.getString("profile")
        toUser = userId
//        Log.d("USERS", "USERS $toUser" )
//        Log.d("USERS", "USERSWWW $currentUserId" )


        mLinearLayoutManager = LinearLayoutManager(this)
        mLinearLayoutManager!!.stackFromEnd = true

        readData(object : MyCallback {
            override fun onCallback(value: String) {
                val myImg = value
                Log.d("TEST2", "FUCKING $myImg")
                listenForMessages(userImg!!, myImg)
            }
        })

        recyclerviewChatLog.adapter = adapter

        sendButton.setOnClickListener{
                preformSendMessage(userId)
        }
    }
    private fun listenForMessages(userImg: String, myImg: String) {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser
//        Log.d("USERS", fromId)
//        Log.d("USERS", toId)
        val mReference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        mReference.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                val chatMessage = p0.getValue(ChatMessage::class.java)
                if (chatMessage != null) {
                    //Если сообщение от нас
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        adapter.add(ChatFromItem(chatMessage.text!!, myImg))
                        //Или не от нас, вставояем в разные адаптеры
                    } else {
                        adapter.add(ChatToItem(chatMessage.text!!, userImg))
                    }
                }
            }
            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })
    }

    private fun preformSendMessage(intentToId: String?) {
        val text = messageEdit.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val toId = intentToId

//        val mReference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val mReference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(mReference.key, text, fromId, toId, System.currentTimeMillis()/1000)
        mReference.setValue(chatMessage).addOnSuccessListener {
            messageEdit.text.clear()
            recyclerviewChatLog.scrollToPosition(adapter.itemCount - 1)
        }
        toReference.setValue(chatMessage)
    }

}

class ChatFromItem(val text: String, private val myAvatar: String): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.itemView.chatFromText.text = text
        Picasso.get().load(myAvatar).into(viewHolder.itemView.chatFromImage)
    }
}




class ChatToItem(val text: String, private val toAvatar: String): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.itemView.chatToText.text = text
        Picasso.get().load(toAvatar).into(viewHolder.itemView.chatToImage)
    }
}


