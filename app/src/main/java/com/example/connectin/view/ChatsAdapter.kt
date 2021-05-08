package com.example.connectin.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.connectin.R
import com.example.connectin.model.Chats
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class ChatsAdapter(var chatsList:List<Chats>):RecyclerView.Adapter<ChatsAdapter.ChatViewHolder>() {


    lateinit var mauth : FirebaseAuth
    lateinit var userDbRef : DatabaseReference

    class ChatViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        lateinit var receiverPic:ImageView
        lateinit var receiverText:TextView
        lateinit var senderText:TextView
        init {
            senderText=view.findViewById(R.id.sender_messageTV)
            receiverPic=view.findViewById(R.id.message_DP)
            receiverText=view.findViewById(R.id.receiver_messageTV)

        }



    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsAdapter.ChatViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.indv_message_layout, parent, false)
        return ChatViewHolder(v)

    }

    override fun onBindViewHolder(holder: ChatsAdapter.ChatViewHolder, position: Int) {
        mauth = FirebaseAuth.getInstance()

        var senderId=mauth.currentUser.uid
        var chats=chatsList[position]

        var userID=chats.from
        userDbRef=FirebaseDatabase.getInstance().reference.child("Users").child(userID!!)

        userDbRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    Picasso.get().load(snapshot.child("profileImage").getValue().toString()).into(holder.receiverPic)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        holder.receiverText.visibility=View.INVISIBLE
        holder.receiverPic.visibility=View.INVISIBLE

        if(userID.equals(senderId)){
            holder.senderText.setText(chats.message)
        }
        else{
            holder.senderText.visibility=View.INVISIBLE

            holder.receiverText.visibility=View.VISIBLE
            holder.receiverPic.visibility=View.VISIBLE
            holder.receiverText.setText(chats.message)


        }



    }

    override fun getItemCount(): Int {
        return  chatsList.size
    }

}