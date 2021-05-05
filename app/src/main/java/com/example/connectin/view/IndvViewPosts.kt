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
import com.example.connectin.model.Posts
import com.example.connectin.presenter.FirebasePresenter
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlin.properties.Delegates

class IndvViewPosts : Fragment(){

    lateinit var postList : RecyclerView

    lateinit var reference : FirebasePresenter

    /*lateinit var userReference: DatabaseReference
    lateinit var postReference: DatabaseReference
    lateinit var likesReference: DatabaseReference
    lateinit var dislikesReference: DatabaseReference*/
    lateinit var mauth : FirebaseAuth
    lateinit var UserId : String

    var postKey : String? = null
    var currentUserId : String? = null
    var likeCheck = false
    var dislikeCheck = false
    var commentLayout : Int? = null
    lateinit var from : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mauth = FirebaseAuth.getInstance()
        currentUserId = mauth.currentUser.uid
        postKey=arguments?.getString("postKey","")
        /*userReference = FirebaseDatabase.getInstance().reference.child("Users")
        postReference = FirebaseDatabase.getInstance().reference.child("Posts")
        likesReference = FirebaseDatabase.getInstance().reference.child("Likes")
        dislikesReference = FirebaseDatabase.getInstance().reference.child("Dislikes")*/
        from = arguments?.getString("from","")!!
        if(postKey?.isNullOrBlank() == false)
        {
            UserId = postKey!!

        }
        else {
            UserId =  currentUserId!!

        }
        if(from.isNullOrEmpty()){
            commentLayout = R.id.parentL
        }else commentLayout = R.id.indvSelfProfileL


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.indv_view_post,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initializing presenter reference
        reference = FirebasePresenter(view)

        postList = view.findViewById(R.id.indvViewPostRV)
        postList.setHasFixedSize(true)
        val layout = LinearLayoutManager(view.context)
        layout.reverseLayout = false
        layout.stackFromEnd = false
        postList.layoutManager = layout

        displayAllPost()
    }

    private fun displayAllPost() {

        val indvQuery = reference.postReference.orderByChild("uid").startAt(UserId).endAt(UserId + "\uf8ff")

        val options = FirebaseRecyclerOptions.Builder<Posts>().setQuery(indvQuery, Posts::class.java).build()

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

                holder.setLikeButtonStatus(postKey!!)
                holder.setDislikeButtonStatus(postKey!!)

                val uid = model.uid
                if(currentUserId?.compareTo(uid) == 0)
                {
                    holder.cardView.setOnClickListener {
                        //Toast.makeText(activity,"Editable and deletable!!: $postKey",Toast.LENGTH_LONG).show()
                        val frag = EditDeletePostFragment()
                        val bundle = Bundle()
                        bundle.putString("postKey",postKey.toString())

                        frag.arguments = bundle

                        activity?.supportFragmentManager?.beginTransaction()
                                ?.replace(R.id.indvSelfProfileL,frag)
                                ?.addToBackStack(null)
                                ?.commit()
                    }
                }

                holder.likeButton.setOnClickListener {
                    likeCheck = true
                    reference.likesReference.addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(likeCheck.equals(true))
                            {
                                if(snapshot.child(postKey!!).hasChild(currentUserId!!))
                                {
                                    reference.likesReference.child(postKey).child(currentUserId!!).removeValue()
                                    likeCheck = false
                                } else {
                                    reference.likesReference.child(postKey).child(currentUserId!!).setValue(true)
                                    reference.dislikesReference.child(postKey).child(currentUserId!!).removeValue()
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
                    reference.dislikesReference.addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(dislikeCheck.equals(true))
                            {
                                if(snapshot.child(postKey!!).hasChild(currentUserId!!))
                                {
                                    reference.dislikesReference.child(postKey).child(currentUserId!!).removeValue()
                                    dislikeCheck = false
                                } else {
                                    reference.dislikesReference.child(postKey).child(currentUserId!!).setValue(true)
                                    reference.likesReference.child(postKey).child(currentUserId!!).removeValue()
                                    dislikeCheck = false
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }

                holder.commentButton.setOnClickListener {
                    val frag = CommentsFragment()
                    val bundle = Bundle()
                    bundle.putString("postKey",postKey.toString())

                    frag.arguments = bundle

                    activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(commentLayout!!,frag)
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

        val cardView = itemView.findViewById<CardView>(R.id.alluserpostCard)

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