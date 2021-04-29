package com.example.connectin.view.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.connectin.R
import com.example.connectin.view.EditDeletePostFragment
import com.example.connectin.view.Posts
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class HomeFragment : Fragment() {

    lateinit var postList : RecyclerView

    private lateinit var homeViewModel: HomeViewModel

    lateinit var userReference: DatabaseReference
    lateinit var postReference: DatabaseReference
    lateinit var mauth : FirebaseAuth
    lateinit var currentUserId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userReference = FirebaseDatabase.getInstance().reference.child("Users")
        postReference = FirebaseDatabase.getInstance().reference.child("Posts")
        mauth = FirebaseAuth.getInstance()
        currentUserId = mauth.currentUser.uid
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postList = view.findViewById(R.id.all_user_post_list)
        postList.setHasFixedSize(true)
        val layout = LinearLayoutManager(view.context)
        layout.reverseLayout = false
        layout.stackFromEnd = false
        postList.layoutManager = layout

        displayAllPost()
    }

    private fun displayAllPost() {

        val options = FirebaseRecyclerOptions.Builder<Posts>().setQuery(postReference,Posts::class.java).build()

        val firebaseRecyclerAdapter : FirebaseRecyclerAdapter<Posts, PostsViewHolder> = object : FirebaseRecyclerAdapter<Posts,PostsViewHolder>(options){
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

        val cardView = itemView.findViewById<CardView>(R.id.alluserpostCard)

    }
}