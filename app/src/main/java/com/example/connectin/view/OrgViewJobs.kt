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
import com.example.connectin.presenter.FirebasePresenter
import com.example.connectin.presenter.ViewJobsPresenter
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

class OrgViewJobs : Fragment(),ViewJobsPresenter.View {

    lateinit var jobList : RecyclerView

    lateinit var reference : FirebasePresenter
    lateinit var viewJobsPresenter: ViewJobsPresenter

    var postKey : String? = null
    //var currentUserId : String? = null
    var check:String?=null

    var type : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        postKey=arguments?.getString("postKey","")
        check=arguments?.getString("profile")
        Toast.makeText(activity,"$postKey",Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.indv_view_post,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reference = FirebasePresenter(view)
        viewJobsPresenter = ViewJobsPresenter(view)

        jobList = view.findViewById(R.id.indvViewPostRV)
        jobList.setHasFixedSize(true)
        val layout = LinearLayoutManager(view.context)
        layout.reverseLayout = false
        layout.stackFromEnd = false
        jobList.layoutManager = layout

        displayAllJobs()
    }

    override fun displayAllJobs() {

        val orgQuery = reference.jobsReference.orderByChild("uid").startAt(postKey).endAt(postKey + "\uf8ff")

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
                    if(reference.currentUserId.compareTo(postKey!!) !=0 || type?.compareTo("organisation")!=0)
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
                                            viewJobsPresenter.applyJob(reference,model.title,model.description,model.salary,model.username,postKey!!,requireActivity())
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
                if(reference.currentUserId.compareTo(postKey!!) ==0) {
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

    inner class JobsViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {

        val title = itemView.findViewById<TextView>(R.id.orgJobTitle)
        val desc = itemView.findViewById<TextView>(R.id.orgJobDesc)
        val salary = itemView.findViewById<TextView>(R.id.orgJobSalary)
        val cardView = itemView.findViewById<CardView>(R.id.orgJobCV)
    }

    inner class FlagTask : AsyncTask<String, Void, String>(){
        override fun doInBackground(vararg params: String?): String {
            return viewJobsPresenter.getResponse(reference.currentUserId) ?: ""
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