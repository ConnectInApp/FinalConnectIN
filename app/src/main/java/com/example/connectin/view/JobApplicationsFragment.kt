package com.example.connectin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.connectin.R
import com.example.connectin.model.Endorsements
import com.example.connectin.model.JobApplications
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_endorsement.*

class JobApplicationsFragment : Fragment(){


    lateinit var jobapplicationList : RecyclerView

    lateinit var userReference : DatabaseReference
    lateinit var jobsReference: DatabaseReference
    lateinit var mauth : FirebaseAuth
    lateinit var currentUserID : String

    lateinit var jobKey:String
    lateinit var jobTitle:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        jobKey= arguments?.getString("jobKey","").toString()
        jobTitle=arguments?.getString("jobTitle","").toString()

        Toast.makeText(activity,"$jobKey$jobTitle",Toast.LENGTH_SHORT).show()

        mauth = FirebaseAuth.getInstance()
        currentUserID = mauth.currentUser.uid
        userReference = FirebaseDatabase.getInstance().reference.child("Users")
        jobsReference = FirebaseDatabase.getInstance().reference.child("Jobs").child("$jobKey$jobTitle").child("applications")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_endorsement,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentText.setText("Job Applications")

        jobapplicationList = view.findViewById(R.id.userEndorseRV)
        jobapplicationList.setHasFixedSize(true)
        val layout = LinearLayoutManager(activity)
        layout.reverseLayout = true
        layout.stackFromEnd = true
        jobapplicationList.layoutManager = layout

        displayApplications()
    }

    private fun displayApplications() {
        val options = FirebaseRecyclerOptions.Builder<JobApplications>().setQuery(jobsReference, JobApplications::class.java).build()

        val firebaseRecyclerAdapter : FirebaseRecyclerAdapter<JobApplications, JobApplicationViewHolder> =
                object : FirebaseRecyclerAdapter<JobApplications, JobApplicationViewHolder>(options) {

                    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): JobApplicationViewHolder {
                        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.all_connection_layout,parent,false)
                        val viewHolder = JobApplicationViewHolder(view)
                        return viewHolder
                    }

                    override fun onBindViewHolder(holder: JobApplicationViewHolder, position: Int, model: JobApplications)
                    {
                        val applicationID = getRef(position).key
                        jobsReference.child(applicationID!!).addValueEventListener(object:ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val username = snapshot.child("username").value.toString()
                                val email = snapshot.child("email").value.toString()
                                val img = snapshot.child("profileImg").value.toString()
                                holder.usernameT.setText(username)
                                holder.emailT.setText(email)
                                Picasso.get().load(img).into(holder.imgV)

                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })

                    }

                }

        jobapplicationList.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()
    }

    inner class JobApplicationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameT = itemView.findViewById<TextView>(R.id.connectionName)
        val emailT = itemView.findViewById<TextView>(R.id.connectionOccupation)
        val imgV = itemView.findViewById<ImageView>(R.id.connectionIV)
    }
}