
package com.example.whatsapp_clone
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.RecyclerView
import com.example.whatsapp_clone.ChatActivity

import com.example.whatsapp_clone.ChatViewHolder
import com.example.whatsapp_clone.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_inbox.*
import kotlinx.android.synthetic.main.fragment_inbox.view.*


class InboxFragment : Fragment() {
    val TAG="FRAGMENT"
    private lateinit var mAdapter: FirebaseRecyclerAdapter<Inbox, ChatViewHolder>
    private lateinit var viewManager: RecyclerView.LayoutManager
    lateinit var currentUser:USER
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
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).get()
            .addOnSuccessListener {
                currentUser = it.toObject(USER::class.java)!!
                val a=USER(currentUser.name,currentUser.imageUrl,currentUser.thumbImage,currentUser.deviceToken,currentUser.status,"online",currentUser.uid,currentUser.number,currentUser.statuspic)
                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).set(a).addOnSuccessListener {
                    Log.d(TAG, "onCreate: now user is online")
                }.addOnFailureListener {
                    Log.d(TAG, "onCreate: "+it.localizedMessage)
                }
            }
        val parent=inflater.inflate(R.layout.fragment_inbox, container, false)
        viewManager = LinearLayoutManager(requireContext())

        setupAdapter()
        parent.rcv1.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView

            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }
        parent.people.setOnClickListener {
            val i= Intent(context,PeopleActivity::class.java)
            startActivity(i)
        }
        return parent
    }

    override fun onResume() {
        super.onResume()
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).get()
            .addOnSuccessListener {
                currentUser = it.toObject(USER::class.java)!!
                val a=USER(currentUser.name,currentUser.imageUrl,currentUser.thumbImage,currentUser.deviceToken,currentUser.status,"online",currentUser.uid,currentUser.number,currentUser.statuspic)
                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).set(a).addOnSuccessListener {
                    Log.d(TAG, "onCreate: now user is online")
                }.addOnFailureListener {
                    Log.d(TAG, "onCreate: "+it.localizedMessage)
                }
            }
    }

    private fun setupAdapter() {

        val baseQuery: Query =
            mDatabase.reference.child("chats").child(auth.uid!!)

        val options = FirebaseRecyclerOptions.Builder<Inbox>()
            .setLifecycleOwner(viewLifecycleOwner)
            .setQuery(baseQuery, Inbox::class.java)
            .build()
        // Instantiate Paging Adapter
        mAdapter = object : FirebaseRecyclerAdapter<Inbox, ChatViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
                val inflater = layoutInflater
                return ChatViewHolder(inflater.inflate(R.layout.listitem1, parent, false))
            }

            override fun onBindViewHolder(
                viewHolder: ChatViewHolder,
                position: Int,
                inbox: Inbox
            ) {

                viewHolder.bind(inbox) { name: String, photo: String, id: String ,number:String ->
                    startActivity(
                        ChatActivity.createChatActivity(
                            requireContext(),
                            id,
                            name,
                            photo,
                            number
                        )
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mAdapter.startListening()
        mAdapter.notifyDataSetChanged()
    }

    override fun onStop() {
        super.onStop()
        mAdapter.stopListening()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}