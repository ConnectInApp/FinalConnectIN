package com.example.connectin.presenter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.connectin.R
import com.example.connectin.model.Follow
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class OrgFollowersPresenter(val view:View) {

    fun displayFollowers(reference:FirebasePresenter,followersList:RecyclerView) {
        val options = FirebaseRecyclerOptions.Builder<Follow>()
                .setQuery(reference.followersReference.child(reference.currentUserId)
                        , Follow::class.java).build()

        val firebaseRecyclerAdapter : FirebaseRecyclerAdapter<Follow, FollowersViewHolder> =
                object : FirebaseRecyclerAdapter<Follow, FollowersViewHolder>(options) {

                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowersViewHolder {
                        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.all_connection_layout,parent,false)
                        val viewHolder = FollowersViewHolder(view)
                        return viewHolder
                    }

                    override fun onBindViewHolder(holder: FollowersViewHolder, position: Int, model: Follow)
                    {
                        val followerID = getRef(position).key
                        reference.userReference.child(model.uid).addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val username = snapshot.child("username").value.toString()
                                val img = snapshot.child("profileImage").value.toString()
                                if(snapshot.child("occupation").exists()){
                                    val occupation = snapshot.child("occupation").value.toString()
                                    holder.occupationT.setText(occupation)
                                } else if(snapshot.child("website").exists()) {
                                    val website = snapshot.child("website").value.toString()
                                    holder.occupationT.setText(website)
                                }

                                holder.usernameT.setText(username)
                                Picasso.get().load(img).into(holder.imgV)
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                }

        followersList.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()
    }

    inner class FollowersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameT = itemView.findViewById<TextView>(R.id.connectionName)
        val occupationT = itemView.findViewById<TextView>(R.id.connectionOccupation)
        val imgV = itemView.findViewById<ImageView>(R.id.connectionIV)
    }
}