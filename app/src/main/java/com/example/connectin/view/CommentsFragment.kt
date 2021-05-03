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
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class CommentsFragment : Fragment(){

    lateinit var comments : RecyclerView
    lateinit var commentInput : EditText
    lateinit var commentButton : Button

    var postKey = ""

    lateinit var currentUserId : String
    lateinit var userReference: DatabaseReference
    lateinit var postReference: DatabaseReference
    lateinit var mauth : FirebaseAuth
    var commentName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle : Bundle? = this.arguments
        postKey = bundle?.getString("postKey").toString()
        Toast.makeText(activity,"$postKey",Toast.LENGTH_SHORT).show()
        userReference = FirebaseDatabase.getInstance().reference.child("Users")
        postReference = FirebaseDatabase.getInstance().reference.child("Posts").child(postKey).child("Comments")
        mauth = FirebaseAuth.getInstance()
        currentUserId = mauth.currentUser.uid
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_comments,container,false)
    }

    override fun onStart() {
        super.onStart()
        val options = FirebaseRecyclerOptions.Builder<Comments>().setQuery(postReference,Comments::class.java).build()

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

        comments = view?.findViewById(R.id.display_comments_RV)!!
        comments.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        comments.layoutManager = layoutManager
        //displayAllComments()


        commentInput = view.findViewById(R.id.comment_input_EV)
        commentButton = view.findViewById(R.id.post_commentB)

        commentButton.setOnClickListener {
            userReference.child(currentUserId).addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    if(snapshot.exists()) {
                        val username = snapshot.child("username").value.toString()
                        commentValidation(username)

                        commentInput.setText("")
                    }
                }

                override fun onCancelled(error: DatabaseError) {}

            })
        }


    }

    private fun displayAllComments() {
        val options = FirebaseRecyclerOptions.Builder<Comments>().setQuery(postReference,Comments::class.java).build()

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

    private fun commentValidation(username: String) {
        val comment = commentInput.text.toString()
        if(comment.isEmpty()) {
            Toast.makeText(activity,"Empty comment",Toast.LENGTH_SHORT).show()
        } else {
            var calendar = Calendar.getInstance()
            val currentDate = SimpleDateFormat("dd-MM-yyyy")
            val commentDate = currentDate.format(calendar.time)

            calendar = Calendar.getInstance()
            val currentTime = SimpleDateFormat("HH:mm")
            val commentTime = currentTime.format(calendar.time)

            commentName = currentUserId + commentDate + commentTime
            val hm = HashMap<String,Any>()
            hm["uid"] = currentUserId
            hm["comment"] = comment
            hm["date"] = commentDate.toString()
            hm["time"] = commentTime.toString()
            hm["username"] = username

            postReference.child(commentName).updateChildren(hm).addOnCompleteListener {
                if(it.isSuccessful)
                {
                    Toast.makeText(activity,"Comment added!",Toast.LENGTH_SHORT).show()
                    commentInput.setText("")
                } else {
                    Toast.makeText(activity,"Error occured!",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    inner class CommentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val usernameT = itemView.findViewById<TextView>(R.id.comment_username_TV)
        val dateT = itemView.findViewById<TextView>(R.id.comment_date_TV)
        val timeT = itemView.findViewById<TextView>(R.id.comment_time_TV)
        val commentT = itemView.findViewById<TextView>(R.id.comment_text_TV)
    }
}