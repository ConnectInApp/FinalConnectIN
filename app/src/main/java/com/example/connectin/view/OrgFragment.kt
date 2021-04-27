package com.example.connectin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.connectin.R
import com.example.connectin.presenter.OrgPresenter
import kotlinx.android.synthetic.main.business_registration_layout.*

class OrgFragment : Fragment(), OrgPresenter.View {

    lateinit var presenter: OrgPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = OrgPresenter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.business_registration_layout,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*val button : Button = view?.findViewById(R.id.registerB)

        button?.setOnClickListener {
            val name = orgName_EV.text
            val email = orgEmail_EV.text
            val password = orgPassword_EV.text
            val address = orgAddress_EV.text
            val website = orgWebsite.text

            registerOrg()
        }*/
    }

    override fun registerOrg() {
        //Toast.makeText(activity,"$name, $email",Toast.LENGTH_SHORT).show()
        Toast.makeText(activity,"Signed Up",Toast.LENGTH_LONG).show()
    }

}