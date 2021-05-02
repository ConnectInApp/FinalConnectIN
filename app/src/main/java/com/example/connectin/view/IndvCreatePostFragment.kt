package com.example.connectin.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.connectin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class  IndvCreatePostFragment : Fragment() {

    //lateinit var postTitle : EditText
    lateinit var postContent : EditText
    lateinit var postB : Button

    lateinit var userReference: DatabaseReference
    lateinit var postReference: DatabaseReference
    lateinit var mauth : FirebaseAuth
    lateinit var currentUserId : String

    lateinit var postDate : String
    lateinit var postTime : String
    lateinit var postName : String

    var countPost : Long = 0

    lateinit var name : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userReference = FirebaseDatabase.getInstance().reference.child("Users")
        postReference = FirebaseDatabase.getInstance().reference.child("Posts")
        mauth = FirebaseAuth.getInstance()
        currentUserId = mauth.currentUser.uid

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.indv_create_post,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //postTitle = view.findViewById(R.id.entertitle_EV)
        postContent = view.findViewById(R.id.newpostContent_EV)
        postB = view.findViewById(R.id.editpostButton)

        var calendar = Calendar.getInstance()
        val currentDate = SimpleDateFormat("dd-MM-yyyy")
        postDate = currentDate.format(calendar.time)

        calendar = Calendar.getInstance()
        val currentTime = SimpleDateFormat("HH:mm")
        postTime = currentTime.format(calendar.time)

        postName = "$currentUserId$postDate$postTime"

        postB.setOnClickListener {
            validatingPostInfo()

            postReference.addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists())
                    {
                        countPost = snapshot.childrenCount
                    } else {
                        countPost = 0
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

            userReference.child(currentUserId).addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        name = snapshot.child("username").value.toString()
                        val profileImg = snapshot.child("profileImage").value.toString()
                        val postMap = HashMap<String,Any>()
                        postMap["uid"] = currentUserId
                        postMap["date"] = postDate
                        postMap["time"] = postTime
                        postMap["content"] = postContent.text.toString()
                        postMap["username"] = name
                        postMap["profileImg"] = profileImg
                        postMap["counter"] = countPost

                        postReference.child(postName).updateChildren(postMap).addOnCompleteListener {
                            if(it.isSuccessful)
                            {
                                Toast.makeText(activity,"Post created",Toast.LENGTH_SHORT).show()
                                val i = Intent(activity,NavigationActivity::class.java)
                                startActivity(i)
                                activity?.finish()
                            } else {
                                Toast.makeText(activity,"Error while creating post",Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    private fun validatingPostInfo() {
        //val title = postTitle.text.toString()
        val content = postContent.text.toString()
        //if(title.isEmpty()) Toast.makeText(activity,"Please mention title",Toast.LENGTH_SHORT).show()
        if(content.isEmpty()) Toast.makeText(activity,"Please add content",Toast.LENGTH_SHORT).show()
    }
}