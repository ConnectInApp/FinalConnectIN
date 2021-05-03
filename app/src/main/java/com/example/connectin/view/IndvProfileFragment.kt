package com.example.connectin.view

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.icu.number.NumberFormatter.with
import android.icu.number.NumberRangeFormatter.with
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.connectin.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import java.net.URI

class IndvProfileFragment : Fragment() {

    lateinit var mauth : FirebaseAuth
    lateinit var userReference: DatabaseReference
    lateinit var userProfileImgRef : StorageReference

    lateinit var currentUserId : String

    lateinit var nameE : TextView
    lateinit var occupationE : TextView
    lateinit var aboutE : TextView
    lateinit var uploadB : Button
    lateinit var userPfp : ImageView
    lateinit var createPostB : FloatingActionButton
    lateinit var editInfo : Button
    lateinit var viewPosts : Button
    lateinit var viewConnections : TextView
    lateinit var viewEndorsements : TextView
    lateinit var viewJobsApplied : TextView
    lateinit var viewFollowing : TextView

    var galleryPick : Int = 0

    var imgUri:Uri = Uri.parse("")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mauth = FirebaseAuth.getInstance()
        currentUserId = mauth.currentUser.uid
        userReference = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId)
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
        createPostB = view.findViewById(R.id.selfCreatePostB)
        editInfo = view.findViewById(R.id.selfEdit_IV)
        viewPosts = view.findViewById(R.id.selfViewPostsB)
        viewConnections = view.findViewById(R.id.selfConnection_TV)
        viewEndorsements = view.findViewById(R.id.selfEndorsement_TV)
        viewJobsApplied = view.findViewById(R.id.selfJobsApplied_TV)
        viewFollowing = view.findViewById(R.id.selfFollowing)

        viewFollowing.setOnClickListener {
            val frag = OrgFollowers()
            val bundle = Bundle()
            bundle.putString("text","Following")
            frag.arguments = bundle
            activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.indvSelfProfileL,frag)
                    ?.addToBackStack(null)
                    ?.commit()
        }

        viewConnections.setOnClickListener {
            val frag = ConnectionsFragment()
            activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.indvSelfProfileL,frag)
                    ?.addToBackStack(null)
                    ?.commit()
        }

        createPostB.setOnClickListener {
            //Toast.makeText(activity,"Working",Toast.LENGTH_SHORT).show()
            val frag = IndvCreatePostFragment()
            activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.indvSelfProfileL, frag)
                    ?.addToBackStack(null)
                    ?.commit()
        }

        editInfo.setOnClickListener {
            val frag = EditIndvInfo()
            activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.indvSelfProfileL,frag)
                    ?.addToBackStack(null)
                    ?.commit()
        }

        uploadB.setOnClickListener {
            //uploadtoStorage()
            val gallery : Intent = Intent()
            gallery.setAction(Intent.ACTION_GET_CONTENT)
            gallery.setType("image/*")
            startActivityForResult(gallery,galleryPick)
        }

        viewPosts.setOnClickListener {
            val frag = IndvViewPosts()
            activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.indvSelfProfileL,frag)
                    ?.addToBackStack(null)
                    ?.commit()
        }

        viewEndorsements.setOnClickListener {
            val frag = EndorsementFragment()
            activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.indvSelfProfileL,frag)
                    ?.addToBackStack(null)
                    ?.commit()
        }

        viewJobsApplied.setOnClickListener {
            val frag = JobsAppliedFragment()
            activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.indvSelfProfileL,frag)
                    ?.addToBackStack(null)
                    ?.commit()
        }

        userReference.addValueEventListener(object : ValueEventListener {
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

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun uploadtoStorage() {
        val resultUri = imgUri

        val path = userProfileImgRef.child("$currentUserId.jpg")

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
                //val downLoadUrl = it.result?.downloadUrl.toString()
            } else Toast.makeText(
                activity,
                "Error: ${it.exception?.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == galleryPick && resultCode == RESULT_OK && data != null) {
            imgUri = data.data!!

            //userPfp.setImageURI(imgUri)
            uploadtoStorage()
            userPfp.setImageURI(imgUri)
//            activity?.let {
//                CropImage.activity(imgUri)
//                    .setGuidelines(CropImageView.Guidelines.ON)
//                    .setAspectRatio(1, 1).start(it)
//            }
        }

        /*if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

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

         else {
            Toast.makeText(activity, "Error occured", Toast.LENGTH_SHORT).show()
        }
    }



}