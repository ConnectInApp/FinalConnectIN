package com.example.connectin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.connectin.R
import com.example.connectin.model.Jobs
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class OrgViewJobs : Fragment() {

    lateinit var jobList : RecyclerView

    lateinit var userReference: DatabaseReference
    lateinit var jobReference: DatabaseReference
    lateinit var mauth : FirebaseAuth

    var postKey : String? = null
    var currentUserId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mauth = FirebaseAuth.getInstance()
        currentUserId = mauth.currentUser.uid
        postKey=arguments?.getString("postKey","")
        Toast.makeText(activity,"$postKey",Toast.LENGTH_SHORT).show()
        jobReference = FirebaseDatabase.getInstance().reference.child("Jobs")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.indv_view_post,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        jobList = view.findViewById(R.id.indvViewPostRV)
        jobList.setHasFixedSize(true)
        val layout = LinearLayoutManager(view.context)
        layout.reverseLayout = false
        layout.stackFromEnd = false
        jobList.layoutManager = layout

        displayAllJobs()
    }

    private fun displayAllJobs() {

        val orgQuery = jobReference.orderByChild("uid").startAt(postKey).endAt(postKey + "\uf8ff")

        val options = FirebaseRecyclerOptions.Builder<Jobs>().setQuery(orgQuery,Jobs::class.java).build()

        val firebaseRecyclerAdapter : FirebaseRecyclerAdapter<Jobs, JobsViewHolder> = object : FirebaseRecyclerAdapter<Jobs, JobsViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobsViewHolder {
                val view:View = LayoutInflater.from(parent.context).inflate(R.layout.org_jobs_layout,parent,false)
                val viewHolder = JobsViewHolder(view)

                return viewHolder
            }

            override fun onBindViewHolder(holder: JobsViewHolder, position: Int, model: Jobs) {
                val jobKey = getRef(position).key

                holder.title.append(model.title)
                holder.desc.setText(model.description)
                holder.salary.append(model.salary)
            }
        }
        jobList.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()
    }

    inner class JobsViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {

        val title = itemView.findViewById<TextView>(R.id.orgJobTitle)
        val desc = itemView.findViewById<TextView>(R.id.orgJobDesc)
        val salary = itemView.findViewById<TextView>(R.id.orgJobSalary)
        val cardView = itemView.findViewById<CardView>(R.id.orgJobCV)
    }

}