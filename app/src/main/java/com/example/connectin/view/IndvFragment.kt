package com.example.connectin.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.connectin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase


class IndvFragment :Fragment() {
    lateinit var auth: FirebaseAuth
    lateinit var registerB:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.individual_registration_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val indvEmail_EV=view.findViewById<EditText>(R.id.indvEmail_EV)
        val indvPassword_EV=view.findViewById<EditText>(R.id.indvPassword_EV)
        val indvName_EV=view.findViewById<EditText>(R.id.indvName_EV)
        val indvDOB_EV=view.findViewById<EditText>(R.id.indvDOB_EV)
        val indvGender_EV=view.findViewById<RadioButton>(R.id.indvGender_EV)


        var indvEmail=indvEmail_EV.text.toString()
        var indvPassword=indvPassword_EV.text.toString()
        var indvName=indvName_EV.text.toString()
        var indvDOB=indvDOB_EV.text.toString()
        var indvGender=indvGender_EV.isChecked
        registerB=activity?.findViewById(R.id.registerB)!!
        registerB.setOnClickListener {
            if(indvEmail.isEmpty()){
                Toast.makeText(activity,"Please enter valid mail",Toast.LENGTH_LONG).show()
            }
            if(indvPassword.length<8){
                Toast.makeText(activity,"Please enter valid password",Toast.LENGTH_LONG).show()
            }
            if(indvName.isEmpty()){
                Toast.makeText(activity,"Please enter valid name",Toast.LENGTH_LONG).show()
            }
            if(indvDOB.isEmpty()){
                Toast.makeText(activity,"Please enter valid date of birth",Toast.LENGTH_LONG).show()
            }
            if(!indvGender){
                Toast.makeText(activity,"Please enter gender",Toast.LENGTH_LONG).show()
            }

            auth.createUserWithEmailAndPassword(indvEmail,indvPassword).addOnCompleteListener(activity!!){ task->
                if(task.isSuccessful){
                    auth.currentUser?.sendEmailVerification()?.addOnCompleteListener{ task->
                        if(task.isSuccessful){
                            Toast.makeText(activity,"Email sent",Toast.LENGTH_LONG).show()
                        }

                    }
                }


            }


        }
        super.onViewCreated(view, savedInstanceState)
    }

}
