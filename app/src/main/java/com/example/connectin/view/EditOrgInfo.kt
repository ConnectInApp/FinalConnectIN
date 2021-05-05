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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class EditOrgInfo : Fragment() {

    lateinit var name : EditText
    lateinit var address : EditText
    lateinit var website : EditText
    lateinit var about : EditText
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
        return inflater.inflate(R.layout.org_edit_profile,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initializing presenter reference
        reference = FirebasePresenter(view)

        name = view.findViewById(R.id.edit_OrgName_EV)
        address = view.findViewById(R.id.edit_OrgAddress_EV)
        website = view.findViewById(R.id.edit_OrgWebsite_EV)
        about = view.findViewById(R.id.edit_OrgAbout_EV)
        updateB = view.findViewById(R.id.update_OrgProfileB)

        reference.userReference.child(currentUserId).addValueEventListener(object : ValueEventListener{
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

                updateB.setOnClickListener {
                    reference.userReference.child(currentUserId).child("username").setValue(name.text.toString())
                    reference.userReference.child(currentUserId).child("address").setValue(address.text.toString())
                    reference.userReference.child(currentUserId).child("website").setValue(website.text.toString())
                    reference.userReference.child(currentUserId).child("about").setValue(about.text.toString())
                    val i = Intent(activity,NavigationActivity::class.java)
                    startActivity(i)
                    Toast.makeText(activity,"Profile Updated",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

    }
}