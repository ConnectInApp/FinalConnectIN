package com.example.connectin.view.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
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
import com.example.connectin.view.CommentsFragment
import com.example.connectin.view.EditDeletePostFragment
import com.example.connectin.view.Posts
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import kotlin.properties.Delegates

class HomeFragment : Fragment() {

    lateinit var postList : RecyclerView

    private lateinit var homeViewModel: HomeViewModel

    lateinit var userReference: DatabaseReference
    lateinit var postReference: DatabaseReference
    lateinit var likesReference: DatabaseReference
    lateinit var dislikesReference: DatabaseReference
    lateinit var mauth : FirebaseAuth

    lateinit var currentUserId : String
    var likeCheck = false
    var dislikeCheck = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userReference = FirebaseDatabase.getInstance().reference.child("Users")
        postReference = FirebaseDatabase.getInstance().reference.child("Posts")
        likesReference = FirebaseDatabase.getInstance().reference.child("Likes")
        dislikesReference = FirebaseDatabase.getInstance().reference.child("Dislikes")
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
        layout.reverseLayout = true
        layout.stackFromEnd = true
        postList.layoutManager = layout

        displayAllPost()
    }

    private fun displayAllPost() {

        val sortPostQuery = postReference.orderByChild("counter")

        val options = FirebaseRecyclerOptions.Builder<Posts>().setQuery(sortPostQuery,Posts::class.java).build()

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
                Picasso.get().load(model.profileImg).into(holder.imgView)

                holder.setLikeButtonStatus(postKey!!)
                holder.setDislikeButtonStatus(postKey!!)

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


                holder.likeButton.setOnClickListener {
                    likeCheck = true
                    likesReference.addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(likeCheck.equals(true))
                            {
                                if(snapshot.child(postKey!!).hasChild(currentUserId))
                                {
                                    likesReference.child(postKey).child(currentUserId).removeValue()
                                    likeCheck = false
                                } else {
                                    likesReference.child(postKey).child(currentUserId).setValue(true)
                                    dislikesReference.child(postKey).child(currentUserId).removeValue()
                                    likeCheck = false
                                }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
                }

                holder.dislikeButton.setOnClickListener {
                    dislikeCheck = true
                    dislikesReference.addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(dislikeCheck.equals(true))
                            {
                                if(snapshot.child(postKey!!).hasChild(currentUserId))
                                {
                                    dislikesReference.child(postKey).child(currentUserId).removeValue()
                                    dislikeCheck = false
                                } else {
                                    dislikesReference.child(postKey).child(currentUserId).setValue(true)
                                    likesReference.child(postKey).child(currentUserId).removeValue()
                                    dislikeCheck = false
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }

                    })
                }

                holder.commentButton.setOnClickListener {
                    val frag = CommentsFragment()
                    val bundle = Bundle()
                    bundle.putString("postKey",postKey.toString())

                    frag.arguments = bundle

                    activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.homeFragmentL,frag)
                            ?.addToBackStack(null)?.commit()
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

        val likeButton = itemView.findViewById<ImageButton>(R.id.postlikeButton)
        val likeText = itemView.findViewById<TextView>(R.id.postlikeText)
        val dislikeButton = itemView.findViewById<ImageButton>(R.id.postdislikeButton)
        val dislikeText = itemView.findViewById<TextView>(R.id.postdislikeText)
        val commentButton = itemView.findViewById<ImageButton>(R.id.postcommentButton)

        var likesCount by Delegates.notNull<Int>()
        var dislikesCount by Delegates.notNull<Int>()
        val likesRef : DatabaseReference = FirebaseDatabase.getInstance().reference.child("Likes")
        val dislikesRef : DatabaseReference = FirebaseDatabase.getInstance().reference.child("Dislikes")
        val currentUserID = FirebaseAuth.getInstance().currentUser.uid

        val cardView = itemView.findViewById<CardView>(R.id.alluserpostCard)

        fun setLikeButtonStatus(postKey: String){
            likesRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.child(postKey).hasChild(currentUserID))
                    {
                        likesCount = snapshot.child(postKey).childrenCount.toInt()
                        likeButton.setImageResource(R.drawable.liked)
                        likeText.setText(likesCount.toString() + " likes")
                    }else {
                        likesCount = snapshot.child(postKey).childrenCount.toInt()
                        likeButton.setImageResource(R.drawable.not_liked)
                        likeText.setText(likesCount.toString() + " likes")
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }

        fun setDislikeButtonStatus(postKey: String) {
            dislikesRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(postKey).hasChild(currentUserID))
                    {
                        dislikesCount = snapshot.child(postKey).childrenCount.toInt()
                        dislikeButton.setImageResource(R.drawable.disliked)
                        dislikeText.setText(dislikesCount.toString() + " dislikes")
                    }
                    else {
                        dislikesCount = snapshot.child(postKey).childrenCount.toInt()
                        dislikeButton.setImageResource(R.drawable.not_disliked)
                        dislikeText.setText(dislikesCount.toString() + " dislikes")
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }
}