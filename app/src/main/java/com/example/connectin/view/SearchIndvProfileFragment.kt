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
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class SearchIndvProfileFragment:Fragment() {
    lateinit var userReference: DatabaseReference
    lateinit var userProfileImgRef : StorageReference

    lateinit var currentUserId : String

    lateinit var nameE : TextView
    lateinit var occupationE : TextView
    lateinit var aboutE : TextView
    lateinit var userPfp : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        var postKey=arguments?.getString("postKey")!!
        userReference = FirebaseDatabase.getInstance().reference.child("Users").child("$postKey")
        userProfileImgRef = FirebaseStorage.getInstance().getReference().child("profileImgs")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.indv_user_profile, container, false)
        return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nameE = view.findViewById(R.id.userName_TV)
        occupationE = view.findViewById(R.id.userOccupation_TV)
        aboutE = view.findViewById(R.id.userInfo_TV)

        userPfp = view.findViewById(R.id.userimg_IV)
        userReference.addValueEventListener(object : ValueEventListener {
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