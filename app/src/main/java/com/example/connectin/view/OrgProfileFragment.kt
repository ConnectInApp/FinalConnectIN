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
import com.example.connectin.presenter.FirebasePresenter
import com.example.connectin.presenter.IndvProfilePresenter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

class OrgProfileFragment : Fragment() {

    lateinit var reference : FirebasePresenter
    lateinit var profileReference: IndvProfilePresenter

    /*var userProfileName : TextView? = null
    var orgProfileInfo : TextView? = null*/
    lateinit var uploadB : Button
    lateinit var orgPfp : ImageView
    lateinit var createJobB : FloatingActionButton
    lateinit var editInfoB : Button
    lateinit var viewJobs : Button
    lateinit var viewFollowers : TextView

    var galleryPick : Int = 0

    var imgUri: Uri = Uri.parse("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        //initializing presenter reference
        reference = FirebasePresenter(view)
        profileReference = IndvProfilePresenter(view)

        /*userProfileName = view.findViewById(R.id.selfOrgName_TV)
        orgProfileInfo = view.findViewById(R.id.selfOrgInfo_TV)*/
        createJobB = view.findViewById(R.id.selfOrgCreatePost_FAB)
        uploadB = view.findViewById(R.id.orguploadB)
        //orgPfp = view.findViewById(R.id.selfOrgImg_IV)
        editInfoB = view.findViewById(R.id.selfOrgEditInfoB)
        viewJobs = view.findViewById(R.id.orgViewPostsB5)
        viewFollowers = view.findViewById(R.id.orgFollowers)

        viewFollowers.setOnClickListener {
            val frag = OrgFollowers()
            activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.orgSelfProfileL,frag)
                    ?.addToBackStack(null)
                    ?.commit()
        }

        viewJobs.setOnClickListener {
            val frag = OrgViewJobs()
            val bundle = Bundle()
            bundle.putString("postKey",reference.currentUserId)
            bundle.putString("profile","orgprofile")
            frag.arguments = bundle
            activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.orgSelfProfileL,frag)
                    ?.addToBackStack(null)
                    ?.commit()
        }

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

        editInfoB.setOnClickListener {
            val frag = EditOrgInfo()
            activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.orgSelfProfileL,frag)
                    ?.addToBackStack(null)
                    ?.commit()
        }

        profileReference.populateOrgProfile(reference,requireActivity())
        /*reference.userReference.child(reference.currentUserId).addValueEventListener(object: ValueEventListener {
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
        })*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == galleryPick && resultCode == Activity.RESULT_OK && data != null) {
            imgUri = data.data!!

            //userPfp.setImageURI(imgUri)
            profileReference.uploadtoStorage(reference,reference.currentUserId,imgUri,requireActivity(),orgPfp)

        } else {
            Toast.makeText(activity, "Error occured", Toast.LENGTH_SHORT).show()
        }
    }

    /*private fun uploadtoStorage() {
        val resultUri = imgUri

        val path = reference.userProfileImgRef.child("${reference.currentUserId}.jpg")

        path.putFile(resultUri).addOnCompleteListener {

            if (it.isSuccessful) {
                Toast.makeText(activity, "Profile image stored to database!!",
                    Toast.LENGTH_SHORT
                ).show()
                path.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    reference.userReference.child(reference.currentUserId).child("profileImage").setValue(downloadUrl)
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
        orgPfp.setImageURI(imgUri)
    }*/
}