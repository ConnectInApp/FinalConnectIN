package com.example.connectin.view

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.connectin.R
import com.example.connectin.model.Jobs
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class OrgViewJobs : Fragment() {

    lateinit var jobList : RecyclerView

    lateinit var userReference: DatabaseReference
    lateinit var jobReference: DatabaseReference
    lateinit var mauth : FirebaseAuth

    var postKey : String? = null
    var currentUserId : String? = null
    var check:String?=null

    var type : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mauth = FirebaseAuth.getInstance()
        currentUserId = mauth.currentUser.uid
        postKey=arguments?.getString("postKey","")
        check=arguments?.getString("profile")
        Toast.makeText(activity,"$postKey",Toast.LENGTH_SHORT).show()
        jobReference = FirebaseDatabase.getInstance().reference.child("Jobs")
        userReference = FirebaseDatabase.getInstance().reference.child("Users")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.indv_view_post,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        jobList = view.findViewById(R.id.indvViewPostRV)
        jobList.setHasFixedSize(true)
        val layout = LinearLayoutManager(view.context)
        layout.reverseLayout = false
        layout.stackFromEnd = false
        jobList.layoutManager = layout

        displayAllJobs()
    }

    private fun displayAllJobs() {

        val orgQuery = jobReference.orderByChild("uid").startAt(postKey).endAt(postKey + "\uf8ff")

        val options = FirebaseRecyclerOptions.Builder<Jobs>().setQuery(orgQuery,Jobs::class.java).build()

        val firebaseRecyclerAdapter : FirebaseRecyclerAdapter<Jobs, JobsViewHolder> = object : FirebaseRecyclerAdapter<Jobs, JobsViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobsViewHolder {
                val view:View = LayoutInflater.from(parent.context).inflate(R.layout.org_jobs_layout,parent,false)
                val viewHolder = JobsViewHolder(view)

                return viewHolder
            }

            override fun onBindViewHolder(holder: JobsViewHolder, position: Int, model: Jobs) {
                val jobKey = getRef(position).key

                holder.title.append(model.title)
                holder.desc.setText(model.description)
                holder.salary.append(model.salary)

                CoroutineScope(Dispatchers.Default).launch {
                    val result = CoroutineScope(Dispatchers.Default).async {
                        FlagTask().execute()
                    }
                    result.await()!!
                    Thread.sleep(1000)
                    if(currentUserId?.compareTo(postKey!!)!=0 || type?.compareTo("organisation")!=0)
                    {
                        CoroutineScope(Dispatchers.Main).launch {
                            holder.cardView.setOnClickListener {
                                val builder = AlertDialog.Builder(activity)
                                builder.setTitle("Job Application")
                                builder.setMessage("Apply for this job?")
                                val dialogClick = DialogInterface.OnClickListener { dialog, which ->
                                    when(which)
                                    {
                                        DialogInterface.BUTTON_POSITIVE -> {
                                            applyJob(model.title,model.description,model.salary,model.username)
                                        }
                                        DialogInterface.BUTTON_NEUTRAL -> {
                                            Toast.makeText(activity,"Application cancelled",Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                                builder.setPositiveButton("Yes",dialogClick)
                                builder.setNegativeButton("No",dialogClick)
                                val dialog = builder.create()
                                dialog.show()
                            }
                        }
                    }
                }
                if(currentUserId?.compareTo(postKey!!) ==0) {
                    holder.cardView.setOnClickListener {
                        val frag = JobApplicationsFragment()
                        val bundle = Bundle()
                        bundle.putString("jobKey",postKey)
                        bundle.putString("jobTitle",model.title)
                        frag.arguments = bundle
                        if (check.isNullOrEmpty())
                        {
                            activity?.supportFragmentManager?.beginTransaction()
                                    ?.replace(R.id.parentL,frag)
                                    ?.addToBackStack(null)
                                    ?.commit()
                        }else {
                            activity?.supportFragmentManager?.beginTransaction()
                                    ?.replace(R.id.orgSelfProfileL,frag)
                                    ?.addToBackStack(null)
                                    ?.commit()
                        }
                    }
                }
            }
        }
        jobList.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()
    }

    private fun applyJob(title:String,desc:String,salary:String,username:String) {
        userReference.child(currentUserId!!).addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val name = snapshot.child("username").value.toString()
                    val pfp = snapshot.child("profileImage").value.toString()
                    val hm = HashMap<String,Any>()
                    hm["username"] = name
                    hm["email"] = mauth.currentUser.email
                    hm["profileImg"] = pfp
                    jobReference.child("$postKey$title").child("applications").child(currentUserId!!).updateChildren(hm).addOnCompleteListener {
                        Toast.makeText(activity,"Job Applied",Toast.LENGTH_SHORT).show()
                        if (it.isSuccessful){
                            val um = HashMap<String,Any>()
                            um["title"] = title
                            um["description"] = desc
                            um["salary"] = salary
                            um["username"] = username
                            userReference.child(currentUserId!!).child("jobsApplied").child("$postKey$title").updateChildren(um).addOnCompleteListener {
                                if(it.isSuccessful){}
                            }
                        } else Toast.makeText(activity,"Error: ${it.exception?.message}",Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    inner class JobsViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {

        val title = itemView.findViewById<TextView>(R.id.orgJobTitle)
        val desc = itemView.findViewById<TextView>(R.id.orgJobDesc)
        val salary = itemView.findViewById<TextView>(R.id.orgJobSalary)
        val cardView = itemView.findViewById<CardView>(R.id.orgJobCV)
    }

    fun getResponse(id: String) : String?{
        val urlS = "https://connectin-77e24-default-rtdb.firebaseio.com/Users/$id.json"
        val url = URL(urlS)
        val connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 3000
        connection.readTimeout = 3000
        if(connection.responseCode == 200){
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            var line = reader.readLine()
            var response = ""
            while(line != null) {
                response += line
                line = reader.readLine()
            }
            return response
        } else {
            Log.d("selfProfile","Error: ${connection.responseCode}, ${connection.responseMessage}")
        }
        return null
    }

    inner class FlagTask : AsyncTask<String, Void, String>(){
        override fun doInBackground(vararg params: String?): String {
            return getResponse(currentUserId!!) ?: ""
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(result?.isNotEmpty()!!){
                val responseObj = JSONObject(result)
                val accountType  = responseObj.getString("accountType")
                Toast.makeText(activity,"$accountType",Toast.LENGTH_LONG).show()
                type = accountType
            } else {
                Toast.makeText(activity,"Error",Toast.LENGTH_LONG).show()
            }
        }
    }

}