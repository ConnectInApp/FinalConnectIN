package com.example.connectin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.connectin.R
import com.example.connectin.model.Comments
import com.example.connectin.presenter.CommentsPresenter
import com.example.connectin.presenter.FirebasePresenter
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CommentsFragment : Fragment(){

    lateinit var reference: FirebasePresenter
    lateinit var commentPresenter : CommentsPresenter

    lateinit var comments : RecyclerView
    lateinit var commentInput : EditText
    lateinit var commentButton : Button

    var postKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle : Bundle? = this.arguments
        postKey = bundle?.getString("postKey").toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_comments,container,false)
    }

    override fun onStart() {
        super.onStart()

        //set up comment firebaserecycler view
        val options = FirebaseRecyclerOptions.Builder<Comments>()
                .setQuery(reference.postReference.child(postKey).child("Comments")
                        , Comments::class.java).build()

        val firebaseRecyclerAdapter : FirebaseRecyclerAdapter<Comments,CommentsViewHolder> = object : FirebaseRecyclerAdapter<Comments,CommentsViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
                val view : View = LayoutInflater.from(parent.context).inflate(R.layout.all_comments_layout,parent,false)
                val viewHolder = CommentsViewHolder(view)
                return viewHolder
            }
            override fun onBindViewHolder(holder: CommentsViewHolder, position: Int, model: Comments) {
                holder.usernameT.setText(model.username)
                holder.commentT.setText(model.comment)
                holder.dateT.setText(model.date)
                holder.timeT.setText(model.time)
            }
        }
        comments.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initializing presenter reference
        reference = FirebasePresenter(view)
        commentPresenter = CommentsPresenter(view)

        //initialise comment recycler view
        comments = view?.findViewById(R.id.display_comments_RV)!!
        comments.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        comments.layoutManager = layoutManager

        commentInput = view.findViewById(R.id.comment_input_EV)
        commentButton = view.findViewById(R.id.post_commentB)

        commentButton.setOnClickListener {
            //add comment
            commentPresenter.commentClick(commentInput,requireActivity(),reference.currentUserId,postKey,reference)
        }
    }

    inner class CommentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameT = itemView.findViewById<TextView>(R.id.comment_username_TV)
        val dateT = itemView.findViewById<TextView>(R.id.comment_date_TV)
        val timeT = itemView.findViewById<TextView>(R.id.comment_time_TV)
        val commentT = itemView.findViewById<TextView>(R.id.comment_text_TV)
    }
}