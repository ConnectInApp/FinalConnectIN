package com.example.connectin.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.connectin.R
import com.example.connectin.model.Connections
import com.example.connectin.view.ui.home.HomeFragment
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ConnectionsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var connectionsList : RecyclerView

    lateinit var connectionReference: DatabaseReference
    lateinit var userReference: DatabaseReference
    lateinit var mauth : FirebaseAuth
    lateinit var currentUserID : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        mauth = FirebaseAuth.getInstance()
        currentUserID = mauth.currentUser.uid
        connectionReference = FirebaseDatabase.getInstance().reference.child("Connections").child(currentUserID)
        userReference = FirebaseDatabase.getInstance().reference.child("Users")
    }

    private fun displayConnections() {
        val options = FirebaseRecyclerOptions.Builder<Connections>().setQuery(connectionReference,Connections::class.java).build()

        val firebaseRecyclerAdapter : FirebaseRecyclerAdapter<Connections, ConnectionViewHolder> =
            object : FirebaseRecyclerAdapter<Connections, ConnectionViewHolder>(options){
                override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): ConnectionViewHolder {
                    val view:View = LayoutInflater.from(parent.context).inflate(R.layout.all_connection_layout,parent,false)
                    val viewHolder = ConnectionViewHolder(view)

                    return viewHolder
                }

                override fun onBindViewHolder(holder: ConnectionViewHolder,position: Int,model: Connections) {


                    val userID = getRef(position).key
                    userReference.child(userID!!).addValueEventListener(object :  ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val username = snapshot.child("username").value.toString()
                            val profileImg = snapshot.child("profileImage").value.toString()
                            val occupation = snapshot.child("occupation").value.toString()
                            holder.usernameT.setText(username)
                            holder.occupationT.setText(occupation)
                            Picasso.get().load(profileImg).into(holder.imgV)
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })

                    holder.cardView.setOnClickListener {
                        val frag = SearchIndvProfileFragment()
                        val bundle = Bundle()
                        bundle.putString("postKey",userID.toString())

                        frag.arguments = bundle

                        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.indvSelfProfileL,frag)?.addToBackStack(null)?.commit()
                    }
                }
            }
        connectionsList.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connections, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        connectionsList = activity?.findViewById(R.id.indvConnections)!!
        connectionsList.setHasFixedSize(true)
        val layout = LinearLayoutManager(activity)
        layout.reverseLayout = true
        layout.stackFromEnd = true
        connectionsList.layoutManager = layout

        displayConnections()
    }

    inner class ConnectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val usernameT = itemView.findViewById<TextView>(R.id.connectionName)
        val occupationT = itemView.findViewById<TextView>(R.id.connectionOccupation)
        val imgV = itemView.findViewById<ImageView>(R.id.connectionIV)
        val cardView = itemView.findViewById<CardView>(R.id.connectionCV)

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ConnectionsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}