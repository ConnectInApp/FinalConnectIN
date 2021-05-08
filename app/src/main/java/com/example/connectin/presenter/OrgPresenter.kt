package com.example.connectin.presenter;

import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import com.example.connectin.R

class OrgPresenter(val view: android.view.View) {

    lateinit var reference : FirebasePresenter

    lateinit var registerB:Button
    lateinit var usernameIndv : EditText
    lateinit var dob : EditText
    lateinit var gender : RadioGroup
    lateinit var occupation : EditText

    fun initialiseIndvValues(){
        usernameIndv = view.findViewById(R.id.indvName_EV)
        dob =view.findViewById(R.id.indvDOB_EV)
        gender =view.findViewById(R.id.indvGender_EV)
        registerB= view.findViewById(R.id.indvRegisterB)
        occupation = view.findViewById(R.id.indvOccupation_EV)
    }

    lateinit var username : EditText
    lateinit var address: EditText
    lateinit var website : EditText
    lateinit var orgRegister : Button

    fun initialiseOrgValues(view: android.view.View) {
        username = view.findViewById(R.id.orgName_EV)
        address = view.findViewById(R.id.orgAddress_EV)
        website = view.findViewById(R.id.orgWebsite)
        orgRegister = view.findViewById(R.id.orgRegisterB)
    }

    interface View {
        fun registerOrg()
        fun initialiseValues()
    }

}