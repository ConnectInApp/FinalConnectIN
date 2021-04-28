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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Runnable
import java.net.HttpURLConnection
import java.net.URL

class selfProfile : Fragment() {

    lateinit var mauth : FirebaseAuth
    lateinit var userReference: DatabaseReference

    lateinit var currentUserId : String
    var flag : Int? = 0
    var type : String? = ""

    var userProfileName : TextView? = null
    var orgProfileInfo : TextView? = null

    val pref = activity?.getSharedPreferences("flags",Context.MODE_PRIVATE)
    val editor = pref?.edit()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mauth = FirebaseAuth.getInstance()
        currentUserId = mauth.currentUser.uid
        userReference = FirebaseDatabase.getInstance().reference.child("Users")
    }

    lateinit var job : Job

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        /*var decision : View = inflater.inflate(R.layout.indv_self_profile,container,false)
        var layout = R.layout.indv_self_profile
        var f = 3*/

        //FlagTask().execute()

        /*job = CoroutineScope(Dispatchers.Default).launch {
            val result = CoroutineScope(Dispatchers.Default).async {
                FlagTask().execute()
            }
            result.await()!!

            val bundle = Bundle()
            val fl = bundle.getInt("flag")
            val fl1 = pref?.getInt("f",15)

            Thread.sleep(3000)

            activity?.runOnUiThread {
                Toast.makeText(activity,"Its working: $type",Toast.LENGTH_LONG).show()

            }
        }*/

        //Thread.sleep(3000)

        /*if (flag?.compareTo(1) == 0) {
            layout = R.layout.indv_self_profile
        } else if (flag?.compareTo(0) == 0) {
            layout = R.layout.org_self_profile
        }*/

        /*if(type?.compareTo("individual") == 0)
        {
            return inflater.inflate(R.layout.indv_self_profile,container,false)
        } else
            return inflater.inflate(R.layout.org_self_profile,container,false)*/
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
            return getResponse(currentUserId) ?: ""
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

    /*fun getLayout() : Int{
        var f = 5
        val bundle = Bundle()

        val a = userReference.child(currentUserId).child("accountType")
        activity?.runOnUiThread {
            Toast.makeText(activity,"$a",Toast.LENGTH_LONG).show()
        }

        userReference.child(currentUserId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val accountType = snapshot.child("accountType").getValue().toString()
                    //Toast.makeText(activity, "$accountType", Toast.LENGTH_LONG).show()
                    if (accountType.equals("individual", true) == true) {
                        //decision
                            activity?.runOnUiThread {
                                f = 1
                                bundle.putInt("flag",1)
                                editor?.putInt("f",1)
                                editor?.commit()
                            }

                        //Toast.makeText(activity,"Indv: $flag",Toast.LENGTH_LONG).show()
                    } else {
                        //decision = inflater.inflate(R.layout.org_self_profile,container,false)
                        f = 0
                        bundle.putInt("flag",0)
                        editor?.putInt("f",0)
                        editor?.apply()
                        editor?.commit()
                        //Toast.makeText(activity,"Org",Toast.LENGTH_LONG).show()
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
        return f
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        job = CoroutineScope(Dispatchers.Default).launch {
            val result = CoroutineScope(Dispatchers.Default).async {
                FlagTask().execute()
            }
            result.await()!!

            val bundle = Bundle()
            val fl = bundle.getInt("flag")
            val fl1 = pref?.getInt("f",15)

            Thread.sleep(3000)

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