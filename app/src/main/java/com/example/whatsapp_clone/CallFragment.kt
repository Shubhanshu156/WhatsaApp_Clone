
package com.example.whatsapp_clone
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.RecyclerView
import com.example.whatsapp_clone.ChatActivity


import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import kotlinx.android.synthetic.main.fragment_call.view.*



class CallFragment : Fragment() {

    private lateinit var mAdapter: FirebaseRecyclerAdapter<Call, CallViewHolder>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val mDatabase by lazy {
        FirebaseDatabase.getInstance()
    }
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parent=inflater.inflate(R.layout.fragment_call, container, false)
        viewManager = LinearLayoutManager(requireContext())
        setupAdapter()
        parent.rcv2.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView

            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }
//        parent.people.setOnClickListener {
//            val i= Intent(context,PeopleActivity::class.java)
//            startActivity(i)
//        }
        return parent
    }

    private fun setupAdapter() {

        val baseQuery: Query =
            mDatabase.reference.child("calls").child(auth.uid!!)

        val options = FirebaseRecyclerOptions.Builder<Call>()
            .setLifecycleOwner(viewLifecycleOwner)
            .setQuery(baseQuery, Call::class.java)
            .build()
        // Instantiate Paging Adapter
        mAdapter = object : FirebaseRecyclerAdapter<Call, CallViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallViewHolder {
                val inflater = layoutInflater
                return CallViewHolder(inflater.inflate(R.layout.call_item, parent, false))
            }

            /**
             * @param model the model object containing the data that should be used to populate the view.
             * @see .onBindViewHolder
             */
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onBindViewHolder(holder: CallViewHolder, position: Int, model: Call) {
                holder.bind(model)
            }
        }


    }

    override fun onStart() {
        super.onStart()
        mAdapter.notifyDataSetChanged()
    }
}