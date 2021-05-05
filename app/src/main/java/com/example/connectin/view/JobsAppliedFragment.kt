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
import com.example.connectin.presenter.JobApplicationPresenter
import com.example.connectin.presenter.JobAppliedPresenter
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_endorsement.*

class JobsAppliedFragment : Fragment() {

    lateinit var reference : FirebasePresenter
    lateinit var appliedReference : JobAppliedPresenter

    lateinit var currentUserID : String
    lateinit var jobsAppliedList : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_endorsement, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initializing presenter reference
        reference = FirebasePresenter(view)
        currentUserID = reference.auth.currentUser.uid
        appliedReference = JobAppliedPresenter(view)

        fragmentText.setText("Applied Jobs")

        jobsAppliedList = view.findViewById(R.id.userEndorseRV)
        jobsAppliedList.setHasFixedSize(true)
        val layout = LinearLayoutManager(activity)
        layout.reverseLayout = true
        layout.stackFromEnd = true
        jobsAppliedList.layoutManager = layout

        appliedReference.displayAllJobs(reference, currentUserID, jobsAppliedList)
    }
}