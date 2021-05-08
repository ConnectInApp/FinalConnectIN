package com.example.connectin.presenter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.connectin.R
import com.example.connectin.model.UserJobsApplied
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class JobAppliedPresenter(val view:android.view.View) {

    fun displayAllJobs(reference:FirebasePresenter,currentUserID:String,jobsAppliedList:RecyclerView) {
        val options = FirebaseRecyclerOptions.Builder<UserJobsApplied>()
                .setQuery(reference.userReference.child(currentUserID).child("jobsApplied")
                        , UserJobsApplied::class.java).build()

        val firebaseRecyclerAdapter : FirebaseRecyclerAdapter<UserJobsApplied, JobsViewHolder> =
                object : FirebaseRecyclerAdapter<UserJobsApplied, JobsViewHolder>(options) {
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobsViewHolder {
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