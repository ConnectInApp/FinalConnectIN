package com.example.connectin.presenter;

import android.widget.Button
import android.widget.EditText
import com.example.connectin.R

class OrgPresenter(view: View) {

    lateinit var reference : FirebasePresenter

    lateinit var username : EditText
    lateinit var address: EditText
    lateinit var website : EditText
    lateinit var orgRegister : Button

    fun initialiseValues(view: android.view.View) {
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