package com.example.connectin.view

import android.content.Context
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.connectin.R
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
        usersArrayList= arrayListOf<Users>()

        searchView.setOnSearchClickListener {
            getUserData()
        }


        searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener{

            override fun onQueryTextSubmit(query: String?): Boolean {
                    searchView.clearFocus()
                    adapter!!.filter.filter(query)
                    return true
            }

            override fun onQueryTextChange(filterString: String?): Boolean {

                adapter!!.filter.filter(filterString)
                return true
            }

        })


    }
     fun getUserData() {

        dbref=FirebaseDatabase.getInstance().getReference("Users")
        dbref.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(userSnapshot in snapshot.children){
                        val user=userSnapshot.getValue(Users::class.java)
                        usersArrayList.add(user!!)
                    }

                    adapter=SearchAdapter(usersArrayList,usersArrayList){
                        Toast.makeText(this@SearchActivity,"$it",Toast.LENGTH_LONG).show()
                    }
                    rView.adapter=adapter
                    adapter.notifyDataSetChanged()

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SearchActivity,"$error",Toast.LENGTH_LONG).show()
            }

        })
    }


}