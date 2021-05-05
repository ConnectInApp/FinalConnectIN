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
import com.example.connectin.presenter.CreatePostPresenter
import com.example.connectin.presenter.FirebasePresenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class  IndvCreatePostFragment : Fragment() {

    lateinit var reference : FirebasePresenter
    lateinit var createPresenter : CreatePostPresenter

    lateinit var postContent : EditText
    lateinit var postB : Button

    lateinit var postDate : String
    lateinit var postTime : String
    lateinit var postName : String

    lateinit var name : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.indv_create_post,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initializing presenter reference
        reference = FirebasePresenter(view)
        createPresenter = CreatePostPresenter(view)

        postContent = view.findViewById(R.id.newpostContent_EV)
        postB = view.findViewById(R.id.editpostButton)

        var calendar = Calendar.getInstance()
        val currentDate = SimpleDateFormat("dd-MM-yyyy")
        postDate = currentDate.format(calendar.time)

        calendar = Calendar.getInstance()
        val currentTime = SimpleDateFormat("HH:mm")
        postTime = currentTime.format(calendar.time)

        postName = "${reference.currentUserId}$postDate$postTime"

        postB.setOnClickListener {
            validatingPostInfo()
            createPresenter.createPost(reference,postDate,postTime,postName,postContent,requireActivity())
        }
    }

    private fun validatingPostInfo() {
        val content = postContent.text.toString()
        if(content.isEmpty()) Toast.makeText(activity,"Please add content",Toast.LENGTH_SHORT).show()
    }
}