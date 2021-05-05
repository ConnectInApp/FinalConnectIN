package com.example.connectin.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.connectin.R
import com.example.connectin.model.Endorsements
import com.example.connectin.presenter.FirebasePresenter
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class EndorsementFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var endorsementList : RecyclerView

    lateinit var reference : FirebasePresenter

    //lateinit var endorsementReference : DatabaseReference
    //lateinit var userReference : DatabaseReference
    lateinit var mauth : FirebaseAuth
    lateinit var currentUserID : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        mauth = FirebaseAuth.getInstance()
        currentUserID = mauth.currentUser.uid
        //endorsementReference = FirebaseDatabase.getInstance().reference.child("Endorsements").child(currentUserID)
        //userReference = FirebaseDatabase.getInstance().reference.child("Users")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_endorsement, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initializing presenter reference
        reference = FirebasePresenter(view)

        endorsementList = view.findViewById(R.id.userEndorseRV)
        endorsementList.setHasFixedSize(true)
        val layout = LinearLayoutManager(activity)
        layout.reverseLayout = true
        layout.stackFromEnd = true
        endorsementList.layoutManager = layout

        displayEndorsements()
    }

    private fun displayEndorsements() {

        val options = FirebaseRecyclerOptions.Builder<Endorsements>()
                .setQuery(reference.endorsementReference.child(currentUserID)
                        ,Endorsements::class.java).build()

        val firebaseRecyclerAdapter : FirebaseRecyclerAdapter<Endorsements,EndorsementViewHolder> =
            object : FirebaseRecyclerAdapter<Endorsements,EndorsementViewHolder>(options) {

            override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): EndorsementViewHolder {
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