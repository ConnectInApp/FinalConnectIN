package com.example.connectin.view

import android.content.Intent
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class SearchIndvProfileFragment:Fragment() {
    lateinit var userReference: DatabaseReference
    lateinit var userProfileImgRef : StorageReference
    lateinit var connectionReqRef : DatabaseReference
    lateinit var conntectionReference : DatabaseReference
    lateinit var endorseReference: DatabaseReference
    lateinit var mauth : FirebaseAuth

    lateinit var currentUserId : String

    lateinit var nameE : TextView
    lateinit var occupationE : TextView
    lateinit var aboutE : TextView
    lateinit var userPfp : ImageView
    lateinit var sendConnectionB : Button
    lateinit var endorseConnectionB : Button
    lateinit var chatConnectionB : Button
    lateinit var viewPostConnectionB : Button
    lateinit var declineConnectionB : Button

    lateinit var currentDate : String

    lateinit var curr_state : String

    lateinit var postKey : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        postKey=arguments?.getString("postKey")!!
        userReference = FirebaseDatabase.getInstance().reference.child("Users")
        userProfileImgRef = FirebaseStorage.getInstance().getReference().child("profileImgs")
        connectionReqRef = FirebaseDatabase.getInstance().reference.child("ConnectionRequests")
        conntectionReference = FirebaseDatabase.getInstance().reference.child("Connections")
        endorseReference = FirebaseDatabase.getInstance().reference.child("Endorsements")
        mauth = FirebaseAuth.getInstance()
        currentUserId = mauth.currentUser.uid
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.indv_user_profile, container, false)
        return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialisation(view)

        declineConnectionB.visibility = View.GONE
        aboutE.visibility = View.INVISIBLE
        occupationE.visibility = View.INVISIBLE
        viewPostConnectionB.visibility = View.INVISIBLE
        endorseConnectionB.visibility = View.INVISIBLE

        if(currentUserId.compareTo(postKey) == 0) {
            sendConnectionB.visibility = View.INVISIBLE
            endorseConnectionB.visibility = View.INVISIBLE
            chatConnectionB.visibility = View.INVISIBLE
            viewPostConnectionB.visibility = View.INVISIBLE
            declineConnectionB.visibility = View.GONE
        }
        else {
            sendConnectionB.setOnClickListener{

                sendConnectionB.isEnabled = false
                if(curr_state.equals("notConnected"))
                {
                    sendRequest()
                }
                if(curr_state.equals("request_sent"))
                {
                    cancelRequest()
                }
                if(curr_state.equals("request_received"))
                {
                    acceptRequest()
                }
                if(curr_state.equals("connected"))
                {
                    unconnect()
                }
            }

            viewPostConnectionB.setOnClickListener {
                viewUserPosts()
            }

            chatConnectionB.setOnClickListener {

            }

            endorseConnectionB.setOnClickListener {
                endorseUser()
            }
        }



        userReference.child("$postKey").addValueEventListener(object : ValueEventListener {
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
                            connectButtonText()
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })


    }

    private fun endorseUser() {
        Toast.makeText(activity,"Endorsed", Toast.LENGTH_SHORT).show()
        userReference.child(currentUserId).addValueEventListener(object: ValueEventListener{
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
                    hm["uid"] = currentUserId

                    endorseReference.child(postKey).child(currentUserId).updateChildren(hm).addOnCompleteListener {
                        if(it.isSuccessful)
                        {
                            Toast.makeText(activity,"User endorsed!!",Toast.LENGTH_SHORT).show()
                            val i = Intent(activity,NavigationActivity::class.java)
                            startActivity(i)
                            activity?.finish()
                        } else {
                            Toast.makeText(activity,"Error: ${it.exception?.message}",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun viewUserPosts() {
        val frag = IndvViewPosts()
        val bundle = Bundle()
        bundle.putString("postKey",postKey)
        frag.arguments = bundle
        activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.parentL,frag)
                ?.addToBackStack(null)
                ?.commit()
    }

    private fun unconnect() {
        conntectionReference.child(currentUserId).child(postKey).removeValue().addOnCompleteListener {
            if(it.isSuccessful) {
                conntectionReference.child(postKey).child(currentUserId).removeValue().addOnCompleteListener {
                    if(it.isSuccessful) {
                        sendConnectionB.isEnabled = true
                        curr_state = "notConnected"
                        sendConnectionB.setText("Connect")

                        declineConnectionB.visibility = View.GONE
                        aboutE.visibility = View.INVISIBLE
                        occupationE.visibility = View.INVISIBLE
                        viewPostConnectionB.visibility = View.INVISIBLE
                        endorseConnectionB.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    private fun acceptRequest() {
        var calendar = Calendar.getInstance()
        val current = SimpleDateFormat("dd-MM-yyyy")
        currentDate = current.format(calendar.time)

        conntectionReference.child(currentUserId).child(postKey).child("date").setValue(currentDate).addOnCompleteListener {
            if(it.isSuccessful)
            {
                conntectionReference.child(postKey).child(currentUserId).child("date").setValue(currentDate).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        connectionReqRef.child(currentUserId).child(postKey).removeValue().addOnCompleteListener {
                            if(it.isSuccessful) {
                                connectionReqRef.child(postKey).child(currentUserId).removeValue().addOnCompleteListener {
                                    if(it.isSuccessful) {
                                        sendConnectionB.isEnabled = true
                                        curr_state = "connected"
                                        sendConnectionB.setText("Unconnect")

                                        declineConnectionB.visibility = View.GONE
                                        aboutE.visibility = View.VISIBLE
                                        occupationE.visibility = View.VISIBLE
                                        viewPostConnectionB.visibility = View.VISIBLE
                                        endorseConnectionB.visibility = View.VISIBLE
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun cancelRequest() {
        connectionReqRef.child(currentUserId).child(postKey).removeValue().addOnCompleteListener {
            if(it.isSuccessful) {
                connectionReqRef.child(postKey).child(currentUserId).removeValue().addOnCompleteListener {
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

    private fun connectButtonText() {
        connectionReqRef.child(currentUserId).addListenerForSingleValueEvent(object : ValueEventListener{
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
                            cancelRequest()
                        }
                    }
                }
                else {
                    conntectionReference.child(currentUserId).addListenerForSingleValueEvent(object : ValueEventListener{
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
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun sendRequest() {

        connectionReqRef.child(currentUserId).child(postKey).child("request_type").setValue("request_sent").addOnCompleteListener {
            if(it.isSuccessful) {
                connectionReqRef.child(postKey).child(currentUserId).child("request_type").setValue("request_received").addOnCompleteListener {
                    if(it.isSuccessful) {
                        sendConnectionB.isEnabled = true
                        curr_state = "request_sent"
                        sendConnectionB.setText("cancel request")
                    }
                }
            }
        }
    }

    private fun initialisation(view: View) {
        nameE = view.findViewById(R.id.userName_TV)
        occupationE = view.findViewById(R.id.userOccupation_TV)
        aboutE = view.findViewById(R.id.userInfo_TV)
        userPfp = view.findViewById(R.id.userimg_IV)
        sendConnectionB = view.findViewById(R.id.sendConnectionReqB)
        endorseConnectionB = view.findViewById(R.id.userEndorsementB)
        chatConnectionB = view.findViewById(R.id.sendMessageB)
        viewPostConnectionB = view.findViewById(R.id.userViewPostsB)
        declineConnectionB = view.findViewById(R.id.declineConnectionReqB)

        curr_state = "notConnected"
    }
}