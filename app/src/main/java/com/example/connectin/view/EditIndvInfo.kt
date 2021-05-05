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
import com.example.connectin.presenter.EditProfileInfoPresenter
import com.example.connectin.presenter.FirebasePresenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class EditIndvInfo : Fragment() {

    lateinit var reference : FirebasePresenter
    lateinit var editPresenter : EditProfileInfoPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.indv_edit_profile,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initializing presenter reference
        reference = FirebasePresenter(view)
        editPresenter = EditProfileInfoPresenter(view)

        editPresenter.initialiseIndvValues()
        editPresenter.editIndvInfo(reference,requireActivity())
    }
}