package com.example.connectin.presenter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.connectin.R
import com.example.connectin.model.Endorsements
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class EndorsementPresenter(val view: View) {

    fun displayEndorsements(reference:FirebasePresenter,endorsementList:RecyclerView) {

        val options = FirebaseRecyclerOptions.Builder<Endorsements>()
                .setQuery(reference.endorsementReference.child(reference.currentUserId)
                        , Endorsements::class.java).build()

        val firebaseRecyclerAdapter : FirebaseRecyclerAdapter<Endorsements, EndorsementViewHolder> =
                object : FirebaseRecyclerAdapter<Endorsements, EndorsementViewHolder>(options) {

                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EndorsementViewHolder {
                        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.all_connection_layout,parent,false)
                        val viewHolder = EndorsementViewHolder(view)
                        return viewHolder
                    }

                    override fun onBindViewHolder(holder: EndorsementViewHolder,position: Int,model: Endorsements)
                    {
                        val userID = getRef(position).key
                        reference.userReference.child(userID!!).addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val username = snapshot.child("username").value.toString()
                                val profileImg = snapshot.child("profileImage").value.toString()
                                val occupation = snapshot.child("occupation").value.toString()
                                holder.usernameT.setText(username)
                                holder.occupationT.setText(occupation)
                                Picasso.get().load(profileImg).into(holder.imgV)
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }

                }

        endorsementList.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()
    }

    inner class EndorsementViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val usernameT = itemView.findViewById<TextView>(R.id.connectionName)
        val occupationT = itemView.findViewById<TextView>(R.id.connectionOccupation)
        val imgV = itemView.findViewById<ImageView>(R.id.connectionIV)
        val cardView = itemView.findViewById<CardView>(R.id.connectionCV)
    }
}