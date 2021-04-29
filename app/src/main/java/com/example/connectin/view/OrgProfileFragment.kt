package com.example.connectin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.connectin.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class OrgProfileFragment : Fragment() {


    lateinit var mauth : FirebaseAuth
    lateinit var userReference: DatabaseReference

    lateinit var currentUserId : String

    var userProfileName : TextView? = null
    var orgProfileInfo : TextView? = null
    lateinit var createJobB : FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mauth = FirebaseAuth.getInstance()
        currentUserId = mauth.currentUser.uid
        userReference = FirebaseDatabase.getInstance().reference.child("Users")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.org_self_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createJobB = view.findViewById(R.id.selfOrgCreatePost_FAB)
        createJobB.setOnClickListener {
            Toast.makeText(activity,"Working",Toast.LENGTH_SHORT).show()
            val frag = OrgCreateJobFragment()
            activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.orgSelfProfileL,frag)
                    ?.addToBackStack(null)
                    ?.commit()
        }

        userReference.child(currentUserId).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    if(snapshot.hasChild("accountType")) {
                        val type = snapshot.child("accountType").getValue().toString()
                        if (type.compareTo("organisation") == 0) {
                            userProfileName = view.findViewById(R.id.selfOrgName_TV)
                            orgProfileInfo = view.findViewById(R.id.selfOrgInfo_TV)
                            //validation
                            if (snapshot.hasChild("username")) {
                                val name = snapshot.child("username").getValue().toString()
                                userProfileName?.setText(name)
                            }
                            if (snapshot.hasChild("address") && snapshot.hasChild("website")) {
                                val address = snapshot.child("address").getValue().toString()
                                val website = snapshot.child("website").getValue().toString()
                                orgProfileInfo?.setText("$address \n $website")
                            } else {
                                Toast.makeText(activity, "Profile name does not exists!", Toast.LENGTH_SHORT).show()
                            }
                        }
                        /*else {
                            if(snapshot.hasChild("username")) {
                                val name = snapshot.child("username").getValue().toString()
                                userProfileName?.setText(name)
                            }
                        }*/
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}