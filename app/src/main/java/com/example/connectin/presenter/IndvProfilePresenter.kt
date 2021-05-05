package com.example.connectin.presenter

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.connectin.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class IndvProfilePresenter(val view: android.view.View) {

    lateinit var nameE : TextView
    lateinit var occupationE : TextView
    lateinit var aboutE : TextView
    lateinit var userPfp : ImageView

    var userProfileName : TextView? = null
    var orgProfileInfo : TextView? = null
    lateinit var orgPfp : ImageView

    fun initialiseIndvValues(){
        nameE = view.findViewById(R.id.selfName_EV)
        occupationE = view.findViewById(R.id.selfOccupation_EV)
        aboutE = view.findViewById(R.id.selfAbout_EV)
        userPfp = view.findViewById(R.id.selfImg_IV)
    }

    fun populateIndvProfile(reference:FirebasePresenter,currentUserId:String,activity: Context){
        initialiseIndvValues()
        reference.userReference.child(currentUserId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild("accountType")) {
                        val type = snapshot.child("accountType").getValue().toString()
                        if (type.compareTo("individual") == 0) {
                            if(snapshot.hasChild("profileImage")) {
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
                                if(snapshot.hasChild("about"))
                                {
                                    val about = snapshot.child("about").getValue().toString()
                                    aboutE.setText("$about \n Date of Birth: $dob \n Gender: $gender")
                                } else {
                                    aboutE.setText("Date of Birth: $dob \n Gender: $gender")
                                }
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
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun initialiseOrgValues(){
        userProfileName = view.findViewById(R.id.selfOrgName_TV)
        orgProfileInfo = view.findViewById(R.id.selfOrgInfo_TV)
        orgPfp = view.findViewById(R.id.selfOrgImg_IV)
    }

    fun populateOrgProfile(reference: FirebasePresenter,activity: Context){
        initialiseOrgValues()
        reference.userReference.child(reference.currentUserId).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    if(snapshot.hasChild("accountType")) {
                        val type = snapshot.child("accountType").getValue().toString()
                        if (type.compareTo("organisation") == 0) {
                            if(snapshot.hasChild("profileImage")) {
                                val img = snapshot.child("profileImage").value.toString()
                                Picasso.get().load(img).into(orgPfp)
                            }
                            //validation
                            if (snapshot.hasChild("username")) {
                                val name = snapshot.child("username").getValue().toString()
                                userProfileName?.setText(name)
                            }
                            if (snapshot.hasChild("address") && snapshot.hasChild("website")) {
                                val address = snapshot.child("address").getValue().toString()
                                val website = snapshot.child("website").getValue().toString()
                                if(snapshot.hasChild("about")) {
                                    val about = snapshot.child("about").value.toString()
                                    orgProfileInfo?.setText("$about \n $address \n $website")
                                }else{
                                    orgProfileInfo?.setText("$address \n $website")
                                }

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

    fun uploadtoStorage(reference: FirebasePresenter, currentUserId: String, imgUri: Uri, activity: Context,imgV:ImageView) {
        val resultUri = imgUri

        val path = reference.userProfileImgRef.child("$currentUserId.jpg")

        path.putFile(resultUri).addOnCompleteListener {

            if (it.isSuccessful) {
                Toast.makeText(activity, "Profile image stored to database!!",
                        Toast.LENGTH_SHORT
                ).show()
                path.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    reference.userReference.child(currentUserId).child("profileImage").setValue(downloadUrl)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(
                                            activity,
                                            "Image stored to firebase database",
                                            Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                }
            } else Toast.makeText(
                    activity,
                    "Error: ${it.exception?.message}",
                    Toast.LENGTH_SHORT
            ).show()
        }
        imgV.setImageURI(imgUri)
    }

}