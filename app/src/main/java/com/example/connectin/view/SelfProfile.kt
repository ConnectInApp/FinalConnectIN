package com.example.connectin.view

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.connectin.R
import com.example.connectin.presenter.FirebasePresenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Runnable
import java.net.HttpURLConnection
import java.net.URL

class SelfProfile : Fragment() {

    var type : String? = ""

    lateinit var reference : FirebasePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    lateinit var job : Job

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_selfprofile,container,false)
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

    inner class FlagTask : AsyncTask<String,Void,String>(){
        override fun doInBackground(vararg params: String?): String {
            return getResponse(reference.currentUserId) ?: ""
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initializing presenter reference
        reference = FirebasePresenter(view)

        job = CoroutineScope(Dispatchers.Default).launch {
            val result = CoroutineScope(Dispatchers.Default).async {
                FlagTask().execute()
            }
            result.await()!!
            Thread.sleep(1000)

            activity?.runOnUiThread {
                Toast.makeText(activity,"Its working: $type",Toast.LENGTH_LONG).show()
                if(type?.compareTo("individual") == 0){
                    val frag = IndvProfileFragment()
                    activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.selfProfileLayout,frag)?.commit()
                } else {
                    val frag = OrgProfileFragment()
                    activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.selfProfileLayout,frag)?.commit()
                }

            }
        }
    }
}