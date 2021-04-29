package com.example.connectin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.connectin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.*

class selfProfile : Fragment() {

    lateinit var mauth : FirebaseAuth
    lateinit var userReference: DatabaseReference

    lateinit var currentUserId : String
    var flag : Int? = 0

    var userProfileName : TextView? = null
    var orgProfileInfo : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mauth = FirebaseAuth.getInstance()
        currentUserId = mauth.currentUser.uid
        userReference = FirebaseDatabase.getInstance().reference.child("Users")


        GlobalScope.launch(Dispatchers.Default) {
            async {
                userReference.child(currentUserId).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val accountType = snapshot.child("accountType").getValue().toString()
                            Toast.makeText(activity, "$accountType", Toast.LENGTH_LONG).show()
                            if (accountType.equals("individual", true) == true) {
                                //decision =
                                flag = 1
                                //Toast.makeText(activity,"Indv: $flag",Toast.LENGTH_LONG).show()
                            } else {
                                //decision = inflater.inflate(R.layout.org_self_profile,container,false)
                                flag = 0
                                //Toast.makeText(activity,"Org",Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var decision : View = inflater.inflate(R.layout.indv_self_profile,container,false)
        var layout = R.layout.indv_self_profile

        GlobalScope.launch(Dispatchers.Default) {
            activity?.runOnUiThread {
                Toast.makeText(activity,"$flag",Toast.LENGTH_LONG).show()
            }
        }

        if (flag?.compareTo(1) == 0) {
            layout = R.layout.indv_self_profile
        } else if (flag?.compareTo(0) == 0) {
            layout = R.layout.org_self_profile
        }

        return inflater.inflate(layout,container,false)
        //return inflater.inflate(R.layout.org_self_profile,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        userReference.child(currentUserId).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    if(snapshot.hasChild("accountType")) {
                        val type = snapshot.child("accountType").getValue().toString()
                        if (type.compareTo("organisation") == 0) {
                            userProfileName = view.findViewById(R.id.selfOrgName_TV)
                            orgProfileInfo = view.findViewById(R.id.selfOrgInfo_TV)
                            //validation
                            if (snapshot.hasChild("username")) {
                                val name = snapshot.child("username").getValue().toString()
                                userProfileName?.setText(name)
                            }
                            if (snapshot.hasChild("address") && snapshot.hasChild("website")) {
                                val address = snapshot.child("address").getValue().toString()
                                val website = snapshot.child("website").getValue().toString()
                                orgProfileInfo?.setText("$address \n $website")
                            } else {
                                Toast.makeText(activity, "Profile name does not exists!", Toast.LENGTH_SHORT).show()
                            }
                        }
                        /*else {
                            if(snapshot.hasChild("username")) {
                                val name = snapshot.child("username").getValue().toString()
                                userProfileName?.setText(name)
                            }
                        }*/
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}