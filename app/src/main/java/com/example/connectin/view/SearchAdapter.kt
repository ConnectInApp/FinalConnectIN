package com.example.connectin.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.connectin.R
import com.example.connectin.view.ui.home.HomeFragment
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase



//class SearchAdapter(var userList:ArrayList<Users>,var userListFilter:ArrayList<Users>,val listener:(String,String)->Unit) : RecyclerView.Adapter<SearchAdapter.ViewHolder>(),Filterable {
//
//    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
//        val nameT: TextView
//
//        init {
//            nameT = view.findViewById(R.id.nameT)
//            usersReference = FirebaseDatabase.getInstance().reference.child("Users")
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val inflater = LayoutInflater.from(parent.context)
//        val v = inflater.inflate(R.layout.search_list, parent, false)
//
//        return ViewHolder(v)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val currentitem = userList[position]
//
//
//
//        holder.nameT.text = currentitem.username
//        holder.itemView.setOnClickListener {
//            listener(currentitem.username!!, currentitem.accountType!!)
//        }
//
//    }
//
//    override fun getItemCount(): Int {
//        return userList.size
//    }
//
//    override fun getFilter(): Filter {
//        return object : Filter() {
//            override fun performFiltering(charsequence: CharSequence?): FilterResults {
//                val filterResults = FilterResults()
//                if (charsequence == null || charsequence.length < 0) {
//                    filterResults.count = userListFilter.size
//                    filterResults.values = userListFilter
//
//                } else {
//                    var searchChr = charsequence.toString().toLowerCase().trim()
//                    val Userdata = ArrayList<Users>()
//
//                    for (user in userListFilter) {
//                        if (user.username!!.contains(searchChr, true)) {
//                            Userdata.add(user)
//                        }
//                    }
//                    filterResults.count = Userdata.size
//                    filterResults.values = Userdata
//                }
//                return filterResults
//
//            }
//
//            override fun publishResults(constraint: CharSequence?, filterResults: FilterResults?) {
//
//                userList = if (filterResults == null || filterResults.values == null)
//                    ArrayList<Users>(userListFilter)
//                else
//                    filterResults.values as ArrayList<Users>
//
//                notifyDataSetChanged()
//            }
//        }
//
//    }
//}

class SearchAdapter(var userList:ArrayList<Users>,var userListFilter:ArrayList<Users>,val listener:(String,String,String)->Unit) :FirebaseRecyclerAdapter<Users, SearchAdapter.ViewHolder>(FirebaseRecyclerOptions.Builder<Users>().setQuery(FirebaseDatabase.getInstance().reference.child("Users"),Users::class.java).build()),Filterable{

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nameT: TextView
        init {
            nameT = view.findViewById(R.id.nameT)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.search_list, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Users) {
        val postKey = getRef(position).key!!
        //Log.d("Searchanushya","user id:$postKey")

        holder.nameT.text = model.username

        holder.itemView.setOnClickListener {

            listener(model.username!!, model.accountType!!,postKey)
        }

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charsequence: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (charsequence == null || charsequence.length < 0) {
                    filterResults.count = userListFilter.size
                    filterResults.values = userListFilter

                } else {
                    var searchChr = charsequence.toString().toLowerCase().trim()
                    val Userdata = ArrayList<Users>()

                    for (user in userListFilter) {
                        if (user.username!!.contains(searchChr, true)) {
                            Userdata.add(user)
                        }
                    }
                    filterResults.count = Userdata.size
                    filterResults.values = Userdata
                }
                return filterResults

            }
            override fun publishResults(constraint: CharSequence?, filterResults: FilterResults?) {
                userList = if (filterResults == null || filterResults.values == null)
                    ArrayList<Users>(userListFilter)
                else
                    filterResults.values as ArrayList<Users>

                startListening()

            }
        }
    }

}