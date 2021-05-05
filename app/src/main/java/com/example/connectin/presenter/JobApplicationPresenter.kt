package com.example.connectin.presenter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.connectin.R
import com.example.connectin.model.JobApplications
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class JobApplicationPresenter(val view:View) {

    fun displayApplications(reference:FirebasePresenter,jobKey:String,jobTitle:String,jobapplicationList:RecyclerView) {
        val options = FirebaseRecyclerOptions.Builder<JobApplications>()
                .setQuery(reference.jobsReference.child("$jobKey$jobTitle")
                        .child("applications"), JobApplications::class.java).build()

        val firebaseRecyclerAdapter : FirebaseRecyclerAdapter<JobApplications, JobApplicationViewHolder> =
                object : FirebaseRecyclerAdapter<JobApplications, JobApplicationViewHolder>(options) {

                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobApplicationViewHolder {
                        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.all_connection_layout,parent,false)
                        val viewHolder = JobApplicationViewHolder(view)
                        return viewHolder
                    }

                    override fun onBindViewHolder(holder: JobApplicationViewHolder, position: Int, model: JobApplications)
                    {
                        val applicationID = getRef(position).key
                        reference.jobsReference.child("$jobKey$jobTitle")
                                .child("applications").child(applicationID!!)
                                .addValueEventListener(object: ValueEventListener {
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