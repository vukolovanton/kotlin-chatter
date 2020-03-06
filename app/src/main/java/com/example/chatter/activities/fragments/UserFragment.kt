package com.example.chatter.activities.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import com.xwray.groupie.GroupieViewHolder
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatter.ChatActivity
import com.example.chatter.ProfileActivity

import com.example.chatter.R
import com.example.chatter.activities.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.firestore.auth.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.users_row.view.*

/**
 * A simple [Fragment] subclass.
 */
class UserFragment : Fragment() {
    var mUserDatabase: DatabaseReference? = null
    private var mCurrentUser: FirebaseUser? = null
    var tempId: String? = null
      lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        //Фетчим юзеров

        mCurrentUser = FirebaseAuth.getInstance().currentUser
        tempId = mCurrentUser?.uid.toString()
        fetchUsers(tempId!!)
    }

    private fun fetchUsers(probablyMyid: String) {
        mAuth = FirebaseAuth.getInstance()
        mUserDatabase = FirebaseDatabase.getInstance().reference.child("users")
        mUserDatabase!!.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                //Устанавлиаем этот охуенный адаптер
                val adapter = GroupAdapter<GroupieViewHolder>()
                //В цикле проходимся по каждому элементу
                p0.children.forEach{
                    //Инициализируем класс
                    val user = it.getValue(Users::class.java)
                    val userId = it.key
                    //Отправляем класс в адаптер
                    if (user != null) {
                        adapter.add(UserItem(user, context!!, userId.toString(), probablyMyid))
                    }

                }
                var linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                friendRecyclerViewId.setHasFixedSize(true)
                friendRecyclerViewId.adapter = adapter
                friendRecyclerViewId.layoutManager = linearLayoutManager
            }
        })
    }

    class UserItem(private val users: Users, val context: Context, private var myId: String, private var someId: String): Item<GroupieViewHolder>() {

        override fun bind(groupieViewHolder: GroupieViewHolder, position: Int) {
            groupieViewHolder.itemView.userName.text = users.display_name
            groupieViewHolder.itemView.userStatus.text = users.status
            users.userId = myId

            if (users.userId == someId){
                groupieViewHolder.itemView.itsMe.text = "It's me!"
            }

            Picasso.get().load(users.image).placeholder(R.drawable.profile_img).into(groupieViewHolder.itemView.usersProfileImg)
            groupieViewHolder.itemView.setOnClickListener {

                var options = arrayOf("Open Profile", "Send Message")
                var builder = AlertDialog.Builder(context)
                builder.setTitle("Select Options")
                builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
                    var userName = groupieViewHolder.itemView.userName
                    var userStat = groupieViewHolder.itemView.userStatus
                    var profilePic = groupieViewHolder.itemView.usersProfileImg

                    if (which == 0) {
                        //Открываем профиль
                        var profileIntent = Intent(context, ProfileActivity::class.java)
                        profileIntent.putExtra("userId", users.userId)
                        context.startActivity(profileIntent)
                    } else {
                        //Открываем чат
                        var chatIntent = Intent(context, ChatActivity::class.java)
                        chatIntent.putExtra("userId", users.userId)
                        chatIntent.putExtra("name", users.display_name)
                        chatIntent.putExtra("status", users.status)
                        chatIntent.putExtra("profile", users.image)
                        context.startActivity(chatIntent)
                    }
                })
                builder.show()
            }
        }

        override fun getLayout(): Int {
            return R.layout.users_row
        }
    }

}
