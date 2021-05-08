package com.example.connectin.presenter

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.connectin.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class SearchProfilePresenter(val view:android.view.View) {

    /*lateinit var nameE : TextView
    lateinit var occupationE : TextView
    lateinit var aboutE : TextView
    lateinit var userPfp : ImageView
    lateinit var sendConnectionB : Button
    lateinit var endorseConnectionB : Button
    lateinit var chatConnectionB : Button
    lateinit var viewPostConnectionB : Button
    lateinit var declineConnectionB : Button
    lateinit var blockUserB : Button

    lateinit var curr_state : String
    lateinit var block_state : String

    fun initialiseIndvValues(){
        nameE = view.findViewById(R.id.userName_TV)
        occupationE = view.findViewById(R.id.userOccupation_TV)
        aboutE = view.findViewById(R.id.userInfo_TV)
        userPfp = view.findViewById(R.id.userimg_IV)
        sendConnectionB = view.findViewById(R.id.sendConnectionReqB)
        endorseConnectionB = view.findViewById(R.id.userEndorsementB)
        chatConnectionB = view.findViewById(R.id.sendMessageB)
        viewPostConnectionB = view.findViewById(R.id.userViewPostsB)
        declineConnectionB = view.findViewById(R.id.declineConnectionReqB)
        blockUserB = view.findViewById(R.id.blockUserB)

        curr_state = "notConnected"
        block_state = "unblocked"
    }

    fun populateIndvProfile(reference:FirebasePresenter,postKey:String,activity:Context){
        //initialiseIndvValues()
        reference.userReference.child("$postKey").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild("accountType")) {
                        val type = snapshot.child("accountType").getValue().toString()
                        if (type.compareTo("individual") == 0) {
                            if (snapshot.hasChild("profileImage")) {
                                val img = snapshot.child("profileImage").value.toString()
                                Picasso.get().load(img).into(userPfp)
                            }
                            if (snapshot.hasChild("username")) {
                                val name = snapshot.child("username").getValue().toString()
                                nameE.setText(name)
                            }
                            if (snapshot.hasChild("dateOfBirth") && snapshot.hasChild("gender")) {
                                val dob = snapshot.child("dateOfBirth").getValue().toString()
                                val gender = snapshot.child("gender").getValue().toString()
                                aboutE.setText("Date of Birth: $dob \n Gender: $gender")
                            }
                            if (snapshot.hasChild("occupation")) {
                                val occ = snapshot.child("occupation").getValue().toString()
                                occupationE.setText(occ)
                            } else {
                                Toast.makeText(
                                        activity,
                                        "Profile name does not exists!",
                                        Toast.LENGTH_SHORT
                                ).show()
                            }
                            connectButtonText(reference,postKey)
                            endorseButtonText(reference,postKey)
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun unconnect(reference: FirebasePresenter,postKey: String) {
        reference.connectionReference.child(reference.currentUserId).child(postKey).removeValue().addOnCompleteListener {
            if(it.isSuccessful) {
                reference.connectionReference.child(postKey).child(reference.currentUserId).removeValue().addOnCompleteListener {
                    if(it.isSuccessful) {
                        sendConnectionB.isEnabled = true
                        curr_state = "notConnected"
                        sendConnectionB.setText("Connect")

                        declineConnectionB.visibility = View.GONE
                        aboutE.visibility = View.INVISIBLE
                        occupationE.visibility = View.INVISIBLE
                        viewPostConnectionB.visibility = View.INVISIBLE
                        chatConnectionB.visibility = View.INVISIBLE
                        endorseConnectionB.visibility = View.INVISIBLE
                        blockUserB.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    fun acceptRequest(reference: FirebasePresenter,postKey: String) {
        var calendar = Calendar.getInstance()
        val current = SimpleDateFormat("dd-MM-yyyy")
        val currentDate = current.format(calendar.time)

        reference.connectionReference.child(reference.currentUserId).child(postKey).child("date").setValue(currentDate).addOnCompleteListener {
            if(it.isSuccessful)
            {
                reference.connectionReference.child(postKey).child(reference.currentUserId).child("date").setValue(currentDate).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        reference.connectionReqRef.child(reference.currentUserId).child(postKey).removeValue().addOnCompleteListener {
                            if(it.isSuccessful) {
                                reference.connectionReqRef.child(postKey).child(reference.currentUserId).removeValue().addOnCompleteListener {
                                    if(it.isSuccessful) {
                                        sendConnectionB.isEnabled = true
                                        curr_state = "connected"
                                        sendConnectionB.setText("Unconnect")

                                        declineConnectionB.visibility = View.GONE
                                        aboutE.visibility = View.VISIBLE
                                        occupationE.visibility = View.VISIBLE
                                        viewPostConnectionB.visibility = View.VISIBLE
                                        endorseConnectionB.visibility = View.VISIBLE
                                        chatConnectionB.visibility = View.VISIBLE
                                        blockUserB.visibility = View.VISIBLE

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun sendRequest(reference: FirebasePresenter,postKey: String) {

        reference.connectionReqRef.child(reference.currentUserId).child(postKey).child("request_type").setValue("request_sent").addOnCompleteListener {
            if(it.isSuccessful) {
                reference.connectionReqRef.child(postKey).child(reference.currentUserId).child("request_type").setValue("request_received").addOnCompleteListener {
                    if(it.isSuccessful) {
                        sendConnectionB.isEnabled = true
                        curr_state = "request_sent"
                        sendConnectionB.setText("cancel request")
                    }
                }
            }
        }
    }

    fun cancelRequest(reference: FirebasePresenter,postKey: String) {
        reference.connectionReqRef.child(reference.currentUserId).child(postKey).removeValue().addOnCompleteListener {
            if(it.isSuccessful) {
                reference.connectionReqRef.child(postKey).child(reference.currentUserId).removeValue().addOnCompleteListener {
                    if(it.isSuccessful) {
                        sendConnectionB.isEnabled = true
                        curr_state = "notConnected"
                        sendConnectionB.setText("Connect")
                        declineConnectionB.visibility = View.GONE
                    }
                }
            }
        }
    }

    fun endorseUser(reference: FirebasePresenter,postKey: String,activity: Context) {
        Toast.makeText(activity,"Endorsed", Toast.LENGTH_SHORT).show()
        reference.userReference.child(reference.currentUserId).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    val name = snapshot.child("username").value.toString()
                    val occupation = snapshot.child("occupation").value.toString()
                    val profileImg = snapshot.child("profileImage").value.toString()
                    val hm = HashMap<String,Any>()
                    hm["username"] = name
                    hm["occupation"] = occupation
                    hm["profileImg"] = profileImg
                    hm["uid"] = reference.currentUserId

                    reference.endorsementReference.child(postKey).child(reference.currentUserId).updateChildren(hm).addOnCompleteListener {
                        if(it.isSuccessful)
                        {
                            Toast.makeText(activity,"User endorsed!!",Toast.LENGTH_SHORT).show()
                            *//*val i = Intent(activity,NavigationActivity::class.java)
                            startActivity(i)
                            activity?.finish()*//*
                        } else {
                            Toast.makeText(activity,"Error: ${it.exception?.message}",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}

        })
    }

    fun unblockUser(reference: FirebasePresenter,postKey: String) {
        reference.blockReference.child(reference.currentUserId).child(postKey).removeValue().addOnCompleteListener {
            if(it.isSuccessful)
            {
                reference.blockReference.child(postKey).child(reference.currentUserId).removeValue().addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        blockUserB.isEnabled = true
                        block_state = "unblocked"
                        blockUserB.setText("Block")

                        aboutE.visibility = View.VISIBLE
                        occupationE.visibility = View.VISIBLE
                        viewPostConnectionB.visibility = View.VISIBLE
                        chatConnectionB.visibility = View.VISIBLE
                        endorseConnectionB.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    fun blockUser(reference: FirebasePresenter,postKey: String) {
        reference.blockReference.child(reference.currentUserId).child(postKey).child("blocked").setValue("yes").addOnCompleteListener {
            if(it.isSuccessful)
            {
                reference.blockReference.child(postKey).child(reference.currentUserId).child("blocked").setValue("yes").addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        blockUserB.isEnabled = true
                        block_state = "blocked"
                        blockUserB.setText("Unblock")

                        aboutE.visibility = View.INVISIBLE
                        occupationE.visibility = View.INVISIBLE
                        viewPostConnectionB.visibility = View.INVISIBLE
                        chatConnectionB.visibility = View.INVISIBLE
                        endorseConnectionB.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    fun connectButtonText(reference: FirebasePresenter,postKey: String) {
        reference.connectionReqRef.child(reference.currentUserId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild(postKey))
                {
                    val request_type = snapshot.child(postKey).child("request_type").value.toString()
                    if(request_type.equals("request_sent")) {
                        curr_state = "request_sent"
                        sendConnectionB.setText("cancel request")
                        declineConnectionB.visibility = View.GONE
                    }
                    else if(request_type.equals("request_received"))
                    {
                        curr_state = "request_received"
                        sendConnectionB.setText("Accept Request")
                        declineConnectionB.visibility = View.VISIBLE

                        declineConnectionB.setOnClickListener {
                            cancelRequest(reference, postKey)
                        }
                    }
                }
                else {
                    reference.connectionReference.child(reference.currentUserId).addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.hasChild(postKey))
                            {
                                curr_state = "connected"
                                sendConnectionB.setText("Unconnect")

                                declineConnectionB.visibility = View.GONE
                                aboutE.visibility = View.VISIBLE
                                occupationE.visibility = View.VISIBLE
                                viewPostConnectionB.visibility = View.VISIBLE
                                endorseConnectionB.visibility = View.VISIBLE
                                chatConnectionB.visibility = View.VISIBLE
                                blockUserB.visibility = View.VISIBLE
                                blockButtonText(reference, postKey)
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {}

                    })
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun blockButtonText(reference: FirebasePresenter,postKey: String) {
        reference.blockReference.child(reference.currentUserId).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild(postKey))
                {
                    val status = snapshot.child(postKey).child("blocked").value.toString()
                    if(status.equals("yes")) {
                        block_state = "blocked"
                        blockUserB.setText("Unblock")

                        aboutE.visibility = View.INVISIBLE
                        occupationE.visibility = View.INVISIBLE
                        viewPostConnectionB.visibility = View.INVISIBLE
                        chatConnectionB.visibility = View.INVISIBLE
                        endorseConnectionB.visibility = View.INVISIBLE
                    }
                }
                else
                {
                    block_state = "unblocked"
                    blockUserB.setText("Block")

                    aboutE.visibility = View.VISIBLE
                    occupationE.visibility = View.VISIBLE
                    viewPostConnectionB.visibility = View.VISIBLE
                    chatConnectionB.visibility = View.VISIBLE
                    endorseConnectionB.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    fun endorseButtonText(reference: FirebasePresenter,postKey: String) {
        reference.endorsementReference.child(postKey).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild(reference.currentUserId))
                {
                    endorseConnectionB.setText("Endorsed")
                }
                else
                {
                    endorseConnectionB.setText("Endorse")
                }
            }
            override fun onCancelled(error: DatabaseError) {}

        })
    }*/
}