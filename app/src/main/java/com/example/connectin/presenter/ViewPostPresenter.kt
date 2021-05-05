package com.example.connectin.presenter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.connectin.R
import com.example.connectin.model.Posts
import com.example.connectin.view.CommentsFragment
import com.example.connectin.view.EditDeletePostFragment
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlin.properties.Delegates

class ViewPostPresenter(val view:View) {

    /*var likeCheck = false
    var dislikeCheck = false

    private fun displayAllPost(reference:FirebasePresenter,UserId:String,currentUserId:String,commentLayout:Int,postList:RecyclerView,activity:Context) {

        val indvQuery = reference.postReference.orderByChild("uid").startAt(UserId).endAt(UserId + "\uf8ff")

        val options = FirebaseRecyclerOptions.Builder<Posts>().setQuery(indvQuery, Posts::class.java).build()

        val firebaseRecyclerAdapter : FirebaseRecyclerAdapter<Posts, PostsViewHolder> = object : FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.all_user_post_layout,parent,false)
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


                        activity.supportFragmentManager?.beginTransaction()
                                .replace(R.id.indvSelfProfileL,frag)
                                .addToBackStack(null)
                                .commit()
                    }
                }

                holder.likeButton.setOnClickListener {
                    likeCheck = true
                    reference.likesReference.addValueEventListener(object : ValueEventListener {
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
                    reference.dislikesReference.addValueEventListener(object : ValueEventListener {
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
            likesRef.addValueEventListener(object : ValueEventListener {
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
            dislikesRef.addValueEventListener(object : ValueEventListener {
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
    }*/
}