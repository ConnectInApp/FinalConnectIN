package com.example.connectin.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.connectin.R
import com.example.connectin.presenter.EditProfileInfoPresenter
import com.example.connectin.presenter.FirebasePresenter
import com.google.firebase.database.*

class EditOrgInfo : Fragment(),EditProfileInfoPresenter.View {

    lateinit var name : EditText
    lateinit var address : EditText
    lateinit var website : EditText
    lateinit var about : EditText
    lateinit var updateB : Button

    lateinit var reference : FirebasePresenter
    lateinit var editPresenter : EditProfileInfoPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.org_edit_profile,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initializing presenter references
        reference = FirebasePresenter(view)
        editPresenter = EditProfileInfoPresenter(view)

        editPresenter.initialiseOrgValues()
        editPresenter.editOrgInfo(reference,requireActivity())

    }

    override fun initialiseValues() {
        name = view?.findViewById(R.id.edit_OrgName_EV)!!
        address = view?.findViewById(R.id.edit_OrgAddress_EV)!!
        website = view?.findViewById(R.id.edit_OrgWebsite_EV)!!
        about = view?.findViewById(R.id.edit_OrgAbout_EV)!!
        updateB = view?.findViewById(R.id.update_OrgProfileB)!!
    }
}