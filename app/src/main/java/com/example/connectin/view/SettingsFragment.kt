package com.example.connectin.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.connectin.R
import com.google.firebase.auth.FirebaseAuth

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var mauth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        mauth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val signOut = view.findViewById<Button>(R.id.signOut)
        signOut.setOnClickListener {
            Toast.makeText(activity,"Signing Out...",Toast.LENGTH_LONG).show()
            mauth.signOut()
            val i = Intent(activity,LoginActivity::class.java)
            startActivity(i)
            activity?.finish()
        }

        val search = view.findViewById<Button>(R.id.searchButton)
        search.setOnClickListener {
            //intent for search activity
            //supportfragment
            val i=Intent(activity,SearchActivity::class.java)
            startActivity(i)

        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}