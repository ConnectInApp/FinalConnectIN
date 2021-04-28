package com.example.connectin.view

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.connectin.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class IndvProfileFragment : Fragment() {

    lateinit var mauth : FirebaseAuth
    lateinit var userReference: DatabaseReference
    lateinit var userProfileImgRef : StorageReference

    lateinit var currentUserId : String

    lateinit var nameE : EditText
    lateinit var occupationE : EditText
    lateinit var aboutE : EditText
    lateinit var uploadB : Button
    lateinit var userPfp : ImageView

    var galleryPick : Int? = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mauth = FirebaseAuth.getInstance()
        currentUserId = mauth.currentUser.uid
        userReference = FirebaseDatabase.getInstance().reference.child("Users")
        userProfileImgRef = FirebaseStorage.getInstance().getReference().child("profileImgs")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.indv_self_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameE = view.findViewById(R.id.selfName_EV)
        occupationE = view.findViewById(R.id.selfOccupation_EV)
        aboutE = view.findViewById(R.id.selfAbout_EV)
        uploadB = view.findViewById(R.id.uploadB)
        userPfp = view.findViewById(R.id.selfImg_IV)

        userPfp.setOnClickListener {
            val gallery : Intent = Intent()
            gallery.setAction(Intent.ACTION_GET_CONTENT)
            gallery.setType("image/*")
            galleryPick?.let { it1 -> startActivityForResult(gallery, it1) }
        }

        userReference.child(currentUserId).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    if(snapshot.hasChild("accountType")){
                        val type = snapshot.child("accountType").getValue().toString()
                        if(type.compareTo("individual") == 0) {
                            if(snapshot.hasChild("username")) {
                                val name = snapshot.child("username").getValue().toString()
                                nameE.setText(name)
                            }
                            if(snapshot.hasChild("dateOfBirth") && snapshot.hasChild("gender")) {
                                val dob = snapshot.child("dateOfBirth").getValue().toString()
                                val gender = snapshot.child("gender").getValue().toString()
                                aboutE.setText("Date of Birth: $dob \n Gender: $gender")
                            }
                            if(snapshot.hasChild("occupation")){
                                val occ = snapshot.child("occupation").getValue().toString()
                                occupationE.setText(occ)
                            } else{
                                Toast.makeText(activity, "Profile name does not exists!", Toast.LENGTH_SHORT).show()
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

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == galleryPick && resultCode == RESULT_OK && data!=null)
        {
            val imgUri = data.data
            activity?.let { CropImage.activity(imgUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1).start(it) }
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            val result = CropImage.getActivityResult(data)

            if(resultCode == RESULT_OK) {
                val resultUri = result.uri

                val path = userProfileImgRef.child("$currentUserId.jpg")

                path.putFile(resultUri).addOnCompleteListener {

                    if(it.isSuccessful){
                        Toast.makeText(activity,"Profile image stored to database!!",Toast.LENGTH_SHORT).show()
                        val downLoadUrl = it.result?.storage?.downloadUrl.toString()
                        userReference.child("profileImage").setValue(downLoadUrl).addOnCompleteListener {
                            if (it.isSuccessful) {
                                val i =Intent(activity,NavigationActivity::class.java)
                                startActivity(i)
                                Toast.makeText(activity,"Image stored to firebase database",Toast.LENGTH_SHORT).show()
                            }
                        }


                    } else Toast.makeText(activity,"Error: ${it.exception?.message}",Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(activity,"Error occured",Toast.LENGTH_SHORT).show()
        }
    }*/
}