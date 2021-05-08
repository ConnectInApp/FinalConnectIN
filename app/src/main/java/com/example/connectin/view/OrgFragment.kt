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
import com.example.connectin.presenter.FirebasePresenter
import com.example.connectin.presenter.OrgPresenter

class OrgFragment : Fragment(), OrgPresenter.View {

    lateinit var presenter: OrgPresenter
    lateinit var reference : FirebasePresenter

    lateinit var currentUserId : String

    lateinit var username : EditText
    lateinit var address: EditText
    lateinit var website : EditText
    lateinit var orgRegister : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        //initializing presenter reference
        reference = FirebasePresenter(view)
        presenter = OrgPresenter(view)

        initialiseValues()

        orgRegister.setOnClickListener {
            registerOrg()
        }
    }

    override fun registerOrg() {
        initialiseValues()
        var name = username.text
        var address = address.text
        var website = website.text
        if(name.isEmpty()) Toast.makeText(activity,"Please enter valid name",Toast.LENGTH_LONG).show()
        if(address.isEmpty()) Toast.makeText(activity,"Please enter address",Toast.LENGTH_LONG).show()
        if(website.isEmpty()) Toast.makeText(activity,"Please enter your website",Toast.LENGTH_LONG).show()
        else {
            val hm = HashMap<String,Any>()
            hm["username"] = name.toString()
            hm["address"] = address.toString()
            hm["website"] = website.toString()
            hm["accountType"] = "organisation"
            reference.userReference.child(reference.currentUserId).updateChildren(hm).addOnCompleteListener {
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

    override fun initialiseValues() {
        username = view?.findViewById(R.id.orgName_EV)!!
        address = view?.findViewById(R.id.orgAddress_EV)!!
        website = view?.findViewById(R.id.orgWebsite)!!
        orgRegister = view?.findViewById(R.id.orgRegisterB)!!
    }
}