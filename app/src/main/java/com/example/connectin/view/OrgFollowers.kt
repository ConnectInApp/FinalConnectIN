package com.example.connectin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.connectin.R
import com.example.connectin.model.Follow
import com.example.connectin.presenter.FirebasePresenter
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_endorsement.*

class OrgFollowers : Fragment() {

    lateinit var followersList : RecyclerView

    lateinit var reference : FirebasePresenter

    /*lateinit var userReference : DatabaseReference
    lateinit var followersReference: DatabaseReference
    lateinit var mauth : FirebaseAuth
    lateinit var currentUserID : String*/

    lateinit var titleText : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        titleText= arguments?.getString("text","").toString()

        /*mauth = FirebaseAuth.getInstance()
        currentUserID = mauth.currentUser.uid
        userReference = FirebaseDatabase.getInstance().reference.child("Users")
        followersReference = FirebaseDatabase.getInstance().reference.child("Follows").child(currentUserID)*/
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_endorsement,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initializing presenter reference
        reference = FirebasePresenter(view)

        if(titleText.equals("Following")) {
            fragmentText.setText(titleText)
        } else {
            fragmentText.setText("Followers")
        }

        followersList = view.findViewById(R.id.userEndorseRV)
        followersList.setHasFixedSize(true)
        val layout = LinearLayoutManager(activity)
        layout.reverseLayout = true
        layout.stackFromEnd = true
        followersList.layoutManager = layout

        displayFollowers()
    }

    private fun displayFollowers() {
        val options = FirebaseRecyclerOptions.Builder<Follow>()
                .setQuery(reference.followersReference.child(reference.currentUserId)
                        , Follow::class.java).build()

        val firebaseRecyclerAdapter : FirebaseRecyclerAdapter<Follow, FollowersViewHolder> =
                object : FirebaseRecyclerAdapter<Follow,FollowersViewHolder>(options) {

                    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): FollowersViewHolder {
                        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.all_connection_layout,parent,false)
                        val viewHolder = FollowersViewHolder(view)
                        return viewHolder
                    }

                    override fun onBindViewHolder(holder: FollowersViewHolder, position: Int, model: Follow)
                    {
                        val followerID = getRef(position).key
                        reference.userReference.child(model.uid).addValueEventListener(object : ValueEventListener{
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