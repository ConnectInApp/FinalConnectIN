package com.example.connectin.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.connectin.R
import com.example.connectin.presenter.FirebasePresenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class EditIndvInfo : Fragment() {

    lateinit var editName : EditText
    lateinit var editOccupation : EditText
    lateinit var editDOB : EditText
    lateinit var editGender : RadioGroup
    lateinit var editAbout : EditText
    lateinit var updateB : Button

    lateinit var mauth : FirebaseAuth
    //lateinit var editUserRef : DatabaseReference
    lateinit var currentUserId : String

    lateinit var reference : FirebasePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mauth = FirebaseAuth.getInstance()
        currentUserId = mauth.currentUser.uid
        //editUserRef = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.indv_edit_profile,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initializing presenter reference
        reference = FirebasePresenter(view)

        editName = view.findViewById(R.id.edit_IndvName_EV)
        editOccupation = view.findViewById(R.id.edit_IndvOccupation_EV)
        editDOB = view.findViewById(R.id.editIndvDOB_EV)
        editGender = view.findViewById(R.id.editGender_RG)
        editAbout = view.findViewById(R.id.editIndvAbout_EV)
        updateB = view.findViewById(R.id.update_IndvProfileB)

        var gender = ""
        editGender.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.editMale_R -> gender = "Male"
                R.id.editFemale_R -> gender = "Female"
                R.id.editOther_R -> gender = "Other"
            }
        }

        reference.userReference.child(currentUserId).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("username").value.toString()
                val occupation = snapshot.child("occupation").value.toString()
                val dob = snapshot.child("dateOfBirth").value.toString()
                if(snapshot.hasChild("about")){
                    val about = snapshot.child("about").value.toString()
                    editAbout.setText(about)
                }
                editName.setText(name)
                editOccupation.setText(occupation)
                editDOB.setText(dob)

                updateB.setOnClickListener {
                    reference.userReference.child(currentUserId).child("username").setValue(editName.text.toString())
                    reference.userReference.child(currentUserId).child("occupation").setValue(editOccupation.text.toString())
                    reference.userReference.child(currentUserId).child("dateOfBirth").setValue(editDOB.text.toString())
                    reference.userReference.child(currentUserId).child("gender").setValue(gender.toString())
                    reference.userReference.child(currentUserId).child("about").setValue(editAbout.text.toString())
                    val i = Intent(activity,NavigationActivity::class.java)
                    startActivity(i)
                    Toast.makeText(activity,"Profile Updated",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}