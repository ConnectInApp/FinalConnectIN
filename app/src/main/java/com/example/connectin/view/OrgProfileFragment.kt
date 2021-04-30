package com.example.connectin.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class OrgProfileFragment : Fragment() {


    lateinit var mauth : FirebaseAuth
    lateinit var userReference: DatabaseReference
    lateinit var orgProfileImgRef : StorageReference

    lateinit var currentUserId : String

    var userProfileName : TextView? = null
    var orgProfileInfo : TextView? = null
    lateinit var uploadB : Button
    lateinit var orgPfp : ImageView
    lateinit var createJobB : FloatingActionButton

    var galleryPick : Int = 0

    var imgUri: Uri = Uri.parse("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mauth = FirebaseAuth.getInstance()
        currentUserId = mauth.currentUser.uid
        userReference = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId)
        orgProfileImgRef = FirebaseStorage.getInstance().getReference().child("profileImgs")

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

        userProfileName = view.findViewById(R.id.selfOrgName_TV)
        orgProfileInfo = view.findViewById(R.id.selfOrgInfo_TV)
        createJobB = view.findViewById(R.id.selfOrgCreatePost_FAB)
        uploadB = view.findViewById(R.id.orguploadB)
        orgPfp = view.findViewById(R.id.selfOrgImg_IV)

        createJobB.setOnClickListener {
            Toast.makeText(activity,"Working",Toast.LENGTH_SHORT).show()
            val frag = OrgCreateJobFragment()
            activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.orgSelfProfileL,frag)
                    ?.addToBackStack(null)
                    ?.commit()
        }

        uploadB.setOnClickListener {
            val gallery : Intent = Intent()
            gallery.setAction(Intent.ACTION_GET_CONTENT)
            gallery.setType("image/*")
            startActivityForResult(gallery,galleryPick)
        }

        userReference.addValueEventListener(object: ValueEventListener {
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
                                orgProfileInfo?.setText("$address \n $website")
                            } else {
                                Toast.makeText(activity, "Profile name does not exists!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == galleryPick && resultCode == Activity.RESULT_OK && data != null) {
            imgUri = data.data!!

            //userPfp.setImageURI(imgUri)
            uploadtoStorage()
            orgPfp.setImageURI(imgUri)
        } else {
            Toast.makeText(activity, "Error occured", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadtoStorage() {
        val resultUri = imgUri

        val path = orgProfileImgRef.child("$currentUserId.jpg")

        path.putFile(resultUri).addOnCompleteListener {

            if (it.isSuccessful) {
                Toast.makeText(activity, "Profile image stored to database!!",
                    Toast.LENGTH_SHORT
                ).show()
                path.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    userReference.child("profileImage").setValue(downloadUrl)
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
    }
}