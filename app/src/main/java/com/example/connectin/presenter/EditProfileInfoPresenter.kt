package com.example.connectin.presenter

import android.content.Context
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import com.example.connectin.R
import com.example.connectin.view.NavigationActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class EditProfileInfoPresenter(val view: android.view.View) {

    interface View{
        fun initialiseValues()
    }

    lateinit var editName : EditText
    lateinit var editOccupation : EditText
    lateinit var editDOB : EditText
    lateinit var editGender : RadioGroup
    lateinit var editAbout : EditText
    lateinit var updateIndvB : Button

    fun editIndvInfo(reference:FirebasePresenter,activity:Context){
        var gender = ""
        editGender.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.editMale_R -> gender = "Male"
                R.id.editFemale_R -> gender = "Female"
                R.id.editOther_R -> gender = "Other"
            }
        }

        reference.userReference.child(reference.currentUserId).addValueEventListener(object : ValueEventListener{
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

                updateIndvB.setOnClickListener {
                    reference.userReference.child(reference.currentUserId).child("username").setValue(editName.text.toString())
                    reference.userReference.child(reference.currentUserId).child("occupation").setValue(editOccupation.text.toString())
                    reference.userReference.child(reference.currentUserId).child("dateOfBirth").setValue(editDOB.text.toString())
                    reference.userReference.child(reference.currentUserId).child("gender").setValue(gender.toString())
                    reference.userReference.child(reference.currentUserId).child("about").setValue(editAbout.text.toString())
                    val i = Intent(activity,NavigationActivity::class.java)
                    activity.startActivity(i)
                    Toast.makeText(activity,"Profile Updated",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun initialiseIndvValues(){
        editName = view.findViewById(R.id.edit_IndvName_EV)
        editOccupation = view.findViewById(R.id.edit_IndvOccupation_EV)
        editDOB = view.findViewById(R.id.editIndvDOB_EV)
        editGender = view.findViewById(R.id.editGender_RG)
        editAbout = view.findViewById(R.id.editIndvAbout_EV)
        updateIndvB = view.findViewById(R.id.update_IndvProfileB)
    }

    lateinit var name : EditText
    lateinit var address : EditText
    lateinit var website : EditText
    lateinit var about : EditText
    lateinit var updateOrgB : Button

    fun editOrgInfo(reference:FirebasePresenter,activity:Context){
        reference.userReference.child(reference.currentUserId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val oldname = snapshot.child("username").value.toString()
                val oldaddress = snapshot.child("address").value.toString()
                val oldwebsite = snapshot.child("website").value.toString()
                if(snapshot.hasChild("about")){
                    val oldabout = snapshot.child("about").value.toString()
                    about.setText(oldabout)
                }
                name.setText(oldname)
                address.setText(oldaddress)
                website.setText(oldwebsite)

                updateOrgB.setOnClickListener {
                    reference.userReference.child(reference.currentUserId).child("username").setValue(name.text.toString())
                    reference.userReference.child(reference.currentUserId).child("address").setValue(address.text.toString())
                    reference.userReference.child(reference.currentUserId).child("website").setValue(website.text.toString())
                    reference.userReference.child(reference.currentUserId).child("about").setValue(about.text.toString())
                    val i = Intent(activity, NavigationActivity::class.java)
                    activity.startActivity(i)
                    Toast.makeText(activity,"Profile Updated", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun initialiseOrgValues() {
        name = view?.findViewById(R.id.edit_OrgName_EV)!!
        address = view?.findViewById(R.id.edit_OrgAddress_EV)!!
        website = view?.findViewById(R.id.edit_OrgWebsite_EV)!!
        about = view?.findViewById(R.id.edit_OrgAbout_EV)!!
        updateOrgB = view?.findViewById(R.id.update_OrgProfileB)!!
    }
}