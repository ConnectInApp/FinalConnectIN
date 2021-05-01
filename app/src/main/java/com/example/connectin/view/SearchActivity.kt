package com.example.connectin.view

import android.content.Context
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.connectin.R
import com.example.connectin.view.ui.home.HomeFragment
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {

     lateinit var  dbref:DatabaseReference
     lateinit var usersArrayList:ArrayList<Users>
     lateinit var adapter: SearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        rView.layoutManager= LinearLayoutManager(this)
        dbref=FirebaseDatabase.getInstance().getReference("Users")

        usersArrayList= arrayListOf<Users>()

        searchView.setOnSearchClickListener {
            rView.visibility=View.VISIBLE
            //getUserData()
            getFromFire("")
        }


        searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener{

            override fun onQueryTextSubmit(query: String?): Boolean {
//                    searchView.clearFocus()
//                    var s=adapter!!.filter.filter(query)
                query?.toLowerCase()
                getFromFire(query!!)
                    return true
            }

            override fun onQueryTextChange(filterString: String?): Boolean {

//                adapter!!.filter.filter(filterString)
                filterString?.toLowerCase()
                getFromFire(filterString!!)

                return true
            }

        })


    }
//     fun getUserData() {
//         usersArrayList.clear()
//
//        dbref=FirebaseDatabase.getInstance().getReference("Users")
//        dbref.addValueEventListener(object:ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if(snapshot.exists()){
//                    for(userSnapshot in snapshot.children){
//                        val user=userSnapshot.getValue(Users::class.java)
//                        usersArrayList.add(user!!)
//                    }
//                    adapter=SearchAdapter(usersArrayList,usersArrayList){username,accountType,id->
//                        Toast.makeText(this@SearchActivity,"$username\n$id",Toast.LENGTH_LONG).show()
//                        if(accountType.equals("individual")) {
//                            val Indvfrag = SearchIndvProfileFragment()
//                            val bundle = Bundle()
//                            bundle.putString("postKey", id.toString())
//
//                            Indvfrag.arguments = bundle
//
//                            supportFragmentManager?.beginTransaction()
//                                    ?.replace(R.id.selfProfileLayout, Indvfrag)
//                                    ?.addToBackStack(null)
//                                    ?.commit()
//                        }
//                        else{
//                            val Orgfrag = SearchOrgProfileFragment()
//                            val bundle = Bundle()
//                            bundle.putString("postKey", id.toString())
//
//                            Orgfrag.arguments = bundle
//
//                            supportFragmentManager?.beginTransaction()
//                                    ?.replace(R.id.parentL, Orgfrag)
//                                    ?.addToBackStack(null)
//                                    ?.commit()
//
//                        }
//                    }
//                    rView.adapter=adapter
//                    adapter.startListening()
//
//
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@SearchActivity,"$error",Toast.LENGTH_LONG).show()
//            }
//
//        })

    private fun getFromFire(string: String){
        var query=dbref.orderByChild("username").startAt(string).endAt(string+"\uf8ff")

        val options = FirebaseRecyclerOptions.Builder<Users>().setQuery(query,Users::class.java).build()

        val firebaseRecyclerAdapter : FirebaseRecyclerAdapter<Users, MyViewHolder> = object : FirebaseRecyclerAdapter<Users, MyViewHolder>(options){

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val v = inflater.inflate(R.layout.search_list, parent, false)

                return MyViewHolder(v)
            }


            override fun onBindViewHolder(holder: SearchActivity.MyViewHolder, position: Int, model: Users) {
                holder.nameT.text = model.username
                val postKey = getRef(position).key!!
                holder.itemView.setOnClickListener {
                    if (model.accountType.equals("individual")) {
                        val Indvfrag = SearchIndvProfileFragment()
                        val bundle = Bundle()
                        bundle.putString("postKey", postKey)

                        Indvfrag.arguments = bundle

                        supportFragmentManager?.beginTransaction()
                                ?.replace(R.id.parentL, Indvfrag)
                                ?.addToBackStack(null)
                                ?.commit()
                    } else {
                        val Orgfrag = SearchOrgProfileFragment()
                        val bundle = Bundle()
                        bundle.putString("postKey", postKey)

                        Orgfrag.arguments = bundle

                        supportFragmentManager?.beginTransaction()
                                ?.replace(R.id.parentL, Orgfrag)
                                ?.addToBackStack(null)
                                ?.commit()

                    }
                }
            }
        }
        firebaseRecyclerAdapter.startListening()
        rView.adapter=firebaseRecyclerAdapter


    }
    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val nameT: TextView
        init {
            nameT = view.findViewById(R.id.nameT)

        }
    }
}

