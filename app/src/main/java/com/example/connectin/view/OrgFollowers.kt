package com.example.connectin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.connectin.R
import com.example.connectin.model.Follow
import com.example.connectin.presenter.FirebasePresenter
import com.example.connectin.presenter.OrgFollowersPresenter
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_endorsement.*

class OrgFollowers : Fragment() {

    lateinit var followersList : RecyclerView

    lateinit var reference : FirebasePresenter
    lateinit var followersPresenter: OrgFollowersPresenter

    lateinit var titleText : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        titleText= arguments?.getString("text","").toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_endorsement,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initializing presenter reference
        reference = FirebasePresenter(view)
        followersPresenter = OrgFollowersPresenter(view)

        if(titleText.equals("Following")) {
            fragmentText.setText(titleText)
        } else {
            fragmentText.setText("Followers")
        }

        followersList = view.findViewById(R.id.userEndorseRV)
        followersList.setHasFixedSize(true)
        val layout = LinearLayoutManager(activity)
        layout.reverseLayout = true
        layout.stackFromEnd = true
        followersList.layoutManager = layout

        followersPresenter.displayFollowers(reference, followersList)
    }
}