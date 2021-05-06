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
import com.example.connectin.presenter.FirebasePresenter
import com.example.connectin.presenter.JobApplicationPresenter
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_endorsement.*

class JobApplicationsFragment : Fragment(){

    lateinit var jobapplicationList : RecyclerView
    lateinit var currentUserID : String

    lateinit var reference : FirebasePresenter
    lateinit var applicationPresenter : JobApplicationPresenter

    lateinit var jobKey:String
    lateinit var jobTitle:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        jobKey= arguments?.getString("jobKey","").toString()
        jobTitle=arguments?.getString("jobTitle","").toString()
        //Toast.makeText(activity,"$jobKey$jobTitle",Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_endorsement,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initializing presenter reference
        reference = FirebasePresenter(view)
        currentUserID = reference.auth.currentUser.uid
        applicationPresenter = JobApplicationPresenter(view)

        fragmentText.setText("Job Applications")

        jobapplicationList = view.findViewById(R.id.userEndorseRV)
        jobapplicationList.setHasFixedSize(true)
        val layout = LinearLayoutManager(activity)
        layout.reverseLayout = true
        layout.stackFromEnd = true
        jobapplicationList.layoutManager = layout

        applicationPresenter.displayApplications(reference,jobKey, jobTitle, jobapplicationList)
    }
}