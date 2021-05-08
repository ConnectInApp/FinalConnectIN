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

class OrgCreateJobFragment : Fragment() {

    lateinit var jobTitle : EditText
    lateinit var jobDesc : EditText
    lateinit var jobSalary : EditText
    lateinit var postJobB : Button

    lateinit var reference : FirebasePresenter
    lateinit var jobName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.org_create_post,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initializing presenter reference
        reference = FirebasePresenter(view)

        jobTitle = view.findViewById(R.id.jobTitle_EV)
        jobDesc = view.findViewById(R.id.jobDescription_EV)
        jobSalary = view.findViewById(R.id.salary_EV)
        postJobB = view.findViewById(R.id.postJobB)

        postJobB.setOnClickListener {
            reference.userReference.child(reference.currentUserId).addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {

                        val name = snapshot.child("username").value.toString()
                        val jobMap = HashMap<String,Any>()
                        jobMap["uid"] = reference.currentUserId
                        jobMap["title"] = jobTitle.text.toString()
                        jobMap["description"] = jobDesc.text.toString()
                        jobMap["salary"] = jobSalary.text.toString()
                        jobMap["username"] = name

                        jobName = "${reference.currentUserId}${jobTitle.text.toString()}"
                        reference.jobsReference.child(jobName).updateChildren(jobMap).addOnCompleteListener {
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

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }
}