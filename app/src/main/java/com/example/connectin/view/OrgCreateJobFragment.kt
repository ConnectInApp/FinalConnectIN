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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class OrgCreateJobFragment : Fragment() {

    lateinit var jobTitle : EditText
    lateinit var jobDesc : EditText
    lateinit var jobSalary : EditText
    lateinit var postJobB : Button

    lateinit var userReference: DatabaseReference
    lateinit var jobReference: DatabaseReference
    lateinit var mauth : FirebaseAuth
    lateinit var currentUserId : String

    lateinit var jobName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userReference = FirebaseDatabase.getInstance().reference.child("Users")
        jobReference = FirebaseDatabase.getInstance().reference.child("Jobs")
        mauth = FirebaseAuth.getInstance()
        currentUserId = mauth.currentUser.uid
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.org_create_post,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        jobTitle = view.findViewById(R.id.jobTitle_EV)
        jobDesc = view.findViewById(R.id.jobDescription_EV)
        jobSalary = view.findViewById(R.id.salary_EV)
        postJobB = view.findViewById(R.id.postJobB)



        postJobB.setOnClickListener {
            userReference.child(currentUserId).addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {

                        val name = snapshot.child("username").value.toString()
                        val jobMap = HashMap<String,Any>()
                        jobMap["uid"] = currentUserId
                        jobMap["title"] = jobTitle.text.toString()
                        jobMap["description"] = jobDesc.text.toString()
                        jobMap["salary"] = jobSalary.text.toString()
                        jobMap["username"] = name

                        jobName = "$currentUserId${jobTitle.text.toString()}"
                        jobReference.child(jobName).updateChildren(jobMap).addOnCompleteListener {
                            if(it.isSuccessful)
                            {
                                Toast.makeText(activity,"Job created",Toast.LENGTH_SHORT).show()
                                val i = Intent(activity,NavigationActivity::class.java)
                                startActivity(i)
                                activity?.finish()
                            } else {
                                Toast.makeText(activity,"Error while creating job",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }
    }
}