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
import com.example.connectin.presenter.OrgPresenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.business_registration_layout.*

class OrgFragment : Fragment(), OrgPresenter.View {

    lateinit var presenter: OrgPresenter
    lateinit var mauth : FirebaseAuth
    lateinit var userReference: DatabaseReference

    lateinit var currentUserId : String

    lateinit var username : EditText
    lateinit var address: EditText
    lateinit var website : EditText
    lateinit var orgRegister : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = OrgPresenter(this)

        mauth = FirebaseAuth.getInstance()
        currentUserId = mauth.currentUser.uid
        userReference = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.business_registration_layout,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        username = view.findViewById(R.id.orgName_EV)
        address = view.findViewById(R.id.orgAddress_EV)
        website = view.findViewById(R.id.orgWebsite)
        orgRegister = view.findViewById(R.id.orgRegisterB)

        var name = username.text
        var address = address.text
        var website = website.text


        orgRegister.setOnClickListener {
            if(name.isEmpty()) Toast.makeText(activity,"Please enter valid name",Toast.LENGTH_LONG).show()
            if(address.isEmpty()) Toast.makeText(activity,"Please enter address",Toast.LENGTH_LONG).show()
            if(website.isEmpty()) Toast.makeText(activity,"Please enter your website",Toast.LENGTH_LONG).show()
            else {
                val hm = HashMap<String,Any>()
                hm["username"] = name.toString()
                hm["address"] = address.toString()
                hm["website"] = website.toString()
                hm["accountType"] = "organisation"
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

    override fun registerOrg() {

    }

}