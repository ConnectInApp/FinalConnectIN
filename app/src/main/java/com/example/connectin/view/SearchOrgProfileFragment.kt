package com.example.connectin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.connectin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class SearchOrgProfileFragment:Fragment() {
    lateinit var userReference: DatabaseReference
    lateinit var followReference: DatabaseReference
    lateinit var mauth : FirebaseAuth

    lateinit var currentUserId : String
    lateinit var postKey : String

    lateinit var userProfileName : TextView
    lateinit var orgProfileInfo : TextView
    lateinit var orgPfp: ImageView
    lateinit var orgViewPost : Button
    lateinit var orgFollowB : Button

    lateinit var curr_state : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postKey=arguments?.getString("postKey")!!
        userReference = FirebaseDatabase.getInstance().reference.child("Users").child(postKey)
        followReference = FirebaseDatabase.getInstance().reference.child("Follows")
        mauth = FirebaseAuth.getInstance()
        currentUserId = mauth.currentUser.uid
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.org_user_profile, container, false)
        return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        curr_state = "notFollows"
        userProfileName = view.findViewById(R.id.orgName_TV)
        orgProfileInfo = view.findViewById(R.id.orgInfo_TV)
        orgPfp = view.findViewById(R.id.orgImg_IV)
        orgViewPost = view.findViewById(R.id.selfOrgViewPostB)
        orgFollowB = view.findViewById(R.id.orgFollowB)

        if(currentUserId.compareTo(postKey) ==0 )
        {
            orgFollowB.visibility = View.INVISIBLE
        }

        orgViewPost.setOnClickListener {
            viewOrgJobs()
        }

        if(!currentUserId.equals(postKey)) {
            orgViewPost.visibility = View.INVISIBLE
        }

        orgFollowB.setOnClickListener {
            orgFollowB.isEnabled = false

            if(curr_state.equals("notFollows"))
            {
                followOrg()
            }
            if(curr_state.equals("follows"))
            {
                unfollowOrg()
            }
        }

        userReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild("accountType")) {
                        val type = snapshot.child("accountType").getValue().toString()
                        if (type.compareTo("organisation") == 0) {
                            if (snapshot.hasChild("profileImage")) {
                                val img = snapshot.child("profileImage").value.toString()
                                Picasso.get().load(img).into(orgPfp)
                            }
                            //validation
                            if (snapshot.hasChild("username")) {
                                val name = snapshot.child("username").getValue().toString()
                                userProfileName.setText(name)
                            }
                            if (snapshot.hasChild("address") && snapshot.hasChild("website")) {
                                val address = snapshot.child("address").getValue().toString()
                                val website = snapshot.child("website").getValue().toString()
                                orgProfileInfo.setText("$address \n $website")
                            } else {
                                Toast.makeText(activity, "Profile name does not exists!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun unfollowOrg() {
        followReference.child(postKey).child(currentUserId).removeValue().addOnCompleteListener {
            if(it.isSuccessful) {
                followReference.child(currentUserId).child(postKey).removeValue()
                orgFollowB.isEnabled = true
                curr_state = "notFollows"
                orgFollowB.setText("Follow")
                orgViewPost.visibility = View.INVISIBLE
            }
        }
    }

    private fun followOrg() {
        followReference.child(postKey).child(currentUserId).child("follows").setValue("yes").addOnCompleteListener {
            if(it.isSuccessful) {
                followReference.child(postKey).child(currentUserId).child("uid").setValue(currentUserId)
                followReference.child(currentUserId).child(postKey).child("following").setValue("yes")
                followReference.child(currentUserId).child(postKey).child("uid").setValue(postKey)
                orgFollowB.isEnabled = true
                curr_state = "follows"
                orgFollowB.setText("Unfollow")
                orgViewPost.visibility = View.VISIBLE
            }
        }
    }

    private fun viewOrgJobs() {
        val frag = OrgViewJobs()
        val bundle = Bundle()
        bundle.putString("postKey",postKey)
        frag.arguments = bundle
        activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.parentL,frag)
                ?.addToBackStack(null)
                ?.commit()
    }


}