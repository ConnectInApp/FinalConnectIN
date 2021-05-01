package com.example.connectin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.connectin.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlin.properties.Delegates

class IndvViewPosts : Fragment(){

    lateinit var postList : RecyclerView

    lateinit var userReference: DatabaseReference
    lateinit var postReference: DatabaseReference
    lateinit var mauth : FirebaseAuth
    lateinit var currentUserId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mauth = FirebaseAuth.getInstance()
        currentUserId = mauth.currentUser.uid
        userReference = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId)
        postReference = FirebaseDatabase.getInstance().reference.child("Posts")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.indv_view_post,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postList = view.findViewById(R.id.indvViewPostRV)
        postList.setHasFixedSize(true)
        val layout = LinearLayoutManager(view.context)
        layout.reverseLayout = false
        layout.stackFromEnd = false
        postList.layoutManager = layout

        displayAllPost()
    }

    private fun displayAllPost() {

        val options = FirebaseRecyclerOptions.Builder<Posts>().setQuery(postReference,Posts::class.java).build()

        val firebaseRecyclerAdapter : FirebaseRecyclerAdapter<Posts, PostsViewHolder> = object : FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
                val view:View = LayoutInflater.from(parent.context).inflate(R.layout.all_user_post_layout,parent,false)
                val viewHolder = PostsViewHolder(view)

                return viewHolder
            }

            override fun onBindViewHolder(holder: PostsViewHolder, position: Int, model: Posts) {

                val postKey = getRef(position).key

                holder.usernameT.setText(model.username)
                holder.dateT.setText(model.date)
                holder.timeT.setText(model.time)
                holder.contentT.setText(model.content)
                Picasso.get().load(model.profileImg).into(holder.imgView)

                val uid = model.uid
                if(currentUserId.compareTo(uid) == 0)
                {
                    holder.cardView.setOnClickListener {
                        //Toast.makeText(activity,"Editable and deletable!!: $postKey",Toast.LENGTH_LONG).show()
                        val frag = EditDeletePostFragment()
                        val bundle = Bundle()
                        bundle.putString("postKey",postKey.toString())

                        frag.arguments = bundle

                        activity?.supportFragmentManager?.beginTransaction()
                                ?.replace(R.id.homeFragmentL,frag)
                                ?.addToBackStack(null)
                                ?.commit()
                    }
                }
            }

        }

        postList.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()
    }

    inner class PostsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val usernameT = itemView.findViewById<TextView>(R.id.usernamepostT)
        val dateT = itemView.findViewById<TextView>(R.id.userpostDateT)
        val timeT = itemView.findViewById<TextView>(R.id.userpostTimeT)
        val contentT = itemView.findViewById<TextView>(R.id.userpostContentT)
        val imgView = itemView.findViewById<ImageView>(R.id.userpostImgIV)

        val cardView = itemView.findViewById<CardView>(R.id.alluserpostCard)
    }
}