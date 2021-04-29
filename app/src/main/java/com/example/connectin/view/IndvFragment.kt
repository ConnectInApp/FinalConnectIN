package com.example.connectin.view

import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.connectin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


class IndvFragment :Fragment() {

    lateinit var auth: FirebaseAuth
    lateinit var userReference : DatabaseReference

    lateinit var currentUserId : String

    lateinit var registerB:Button
    lateinit var username : EditText
    lateinit var dob : EditText
    lateinit var gender : RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        currentUserId = auth.currentUser.uid
        userReference = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.individual_registration_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        username = view.findViewById(R.id.indvName_EV)
        dob =view.findViewById(R.id.indvDOB_EV)
        gender =view.findViewById(R.id.indvGender_EV)
        registerB= view.findViewById(R.id.indvRegisterB)

        var indvName = username.text
        var indvDOB = dob.text
        var indvGender = ""

        gender.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId == R.id.maleRadio)
                indvGender = "Male"
            if(checkedId == R.id.femaleRadio)
                indvGender = "Female"
        }

        registerB.setOnClickListener {
            if(indvName.isEmpty()) Toast.makeText(activity,"Please enter valid name",Toast.LENGTH_LONG).show()
            if(indvDOB.isEmpty()) Toast.makeText(activity,"Please enter valid date of birth",Toast.LENGTH_LONG).show()
            if(indvGender.isEmpty()) Toast.makeText(activity,"Please select a gender",Toast.LENGTH_LONG).show()
            else {
                val hm = HashMap<String,Any>()
                hm["username"] = indvName.toString()
                hm["dateOfBirth"] = indvDOB.toString()
                hm["gender"] = indvGender.toString()
                hm["accountType"] = "individual"
                userReference.updateChildren(hm).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        Toast.makeText(activity,"Your account is successfully created",Toast.LENGTH_SHORT).show()
                        val i = Intent(activity,NavigationActivity::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(i)
                        activity?.finish()
                    } else Toast.makeText(activity,"Error: ${it.exception?.message}",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /*auth.createUserWithEmailAndPassword(indvEmail,indvPassword).addOnCompleteListener(activity!!){ task->
                if(task.isSuccessful){
                    auth.currentUser?.sendEmailVerification()?.addOnCompleteListener{ task->
                        if(task.isSuccessful){
                            Toast.makeText(activity,"Email sent",Toast.LENGTH_LONG).show()
                        }

                    }
                }


            }*/

}
