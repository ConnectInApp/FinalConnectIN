package com.example.connectin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.connectin.R
import com.example.connectin.model.Endorsements
import com.example.connectin.model.UserJobsApplied
import com.example.connectin.presenter.FirebasePresenter
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_endorsement.*

class JobsAppliedFragment : Fragment() {

    /*lateinit var userReference : DatabaseReference
    lateinit var mauth : FirebaseAuth
    lateinit var jobsReference: DatabaseReference*/

    lateinit var reference : FirebasePresenter

    lateinit var currentUserID : String
    lateinit var jobsAppliedList : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //mauth = FirebaseAuth.getInstance()
        //currentUserID = mauth.currentUser.uid
        //jobsReference = FirebaseDatabase.getInstance().reference.child("Jobs")
        //userReference = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserID).child("jobsApplied")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_endorsement, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initializing presenter reference
        reference = FirebasePresenter(view)
        currentUserID = reference.auth.currentUser.uid

        fragmentText.setText("Applied Jobs")

        jobsAppliedList = view.findViewById(R.id.userEndorseRV)
        jobsAppliedList.setHasFixedSize(true)
        val layout = LinearLayoutManager(activity)
        layout.reverseLayout = true
        layout.stackFromEnd = true
        jobsAppliedList.layoutManager = layout

        displayAllJobs()
    }

    private fun displayAllJobs() {
        val options = FirebaseRecyclerOptions.Builder<UserJobsApplied>()
                .setQuery(reference.userReference.child(currentUserID).child("jobsApplied")
                        , UserJobsApplied::class.java).build()

        val firebaseRecyclerAdapter : FirebaseRecyclerAdapter<UserJobsApplied, JobsViewHolder> =
                object : FirebaseRecyclerAdapter<UserJobsApplied, JobsViewHolder>(options) {
                    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): JobsViewHolder {
                        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.org_jobs_layout,parent,false)
                        val viewHolder = JobsViewHolder(view)
                        return viewHolder
                    }

                    override fun onBindViewHolder(holder: JobsViewHolder, position: Int, model: UserJobsApplied)
                    {
                        val userID = getRef(position).key
                        reference.userReference.child(currentUserID).child("jobsApplied")
                                .child(userID!!).addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val username = snapshot.child("username").value.toString()
                                val desc = snapshot.child("description").value.toString()
                                val title = snapshot.child("title").value.toString()
                                val salary = snapshot.child("salary").value.toString()
                                holder.orgName.setText(username)
                                holder.orgTitle.setText(title)
                                holder.orgDesc.setText(desc)
                                holder.orgSal.setText(salary)
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }

                }

        jobsAppliedList.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()
    }

    inner class JobsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orgName = itemView.findViewById<TextView>(R.id.orgCompanyName)
        val orgTitle = itemView.findViewById<TextView>(R.id.orgJobTitle)
        val orgDesc = itemView.findViewById<TextView>(R.id.orgJobDesc)
        val orgSal = itemView.findViewById<TextView>(R.id.orgJobSalary)
    }
}