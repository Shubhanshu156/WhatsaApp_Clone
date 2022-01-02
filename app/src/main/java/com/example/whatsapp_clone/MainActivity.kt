package com.example.whatsapp_clone

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {
    val TAG="TESTING"
    lateinit var currentUser:USER
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        viewPager.adapter=Screenslideradapter(this)
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

            TabLayoutMediator(tabs,viewPager
        ,TabLayoutMediator.TabConfigurationStrategy{tab, position ->
                when(position){
                    0->tab.text="CHATS"
                    1->tab.text="STATUS"
                    2->tab.text="CALLS"
                }

            }).attach()
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }



//    override fun onResumeFragments() {
//        super.onResumeFragments()
//        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).get()
//            .addOnSuccessListener {
//                currentUser = it.toObject(USER::class.java)!!
//                val a=USER(currentUser.name,currentUser.imageUrl,currentUser.thumbImage,currentUser.deviceToken,currentUser.status,"online",currentUser.uid,currentUser.number,currentUser.statuspic)
//                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).set(a).addOnSuccessListener {
//                    Log.d(TAG, "onCreate: now user is online")
//                }.addOnFailureListener {
//                    Log.d(TAG, "onCreate: "+it.localizedMessage)
//                }
//            }
//
//    }
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

    override fun onPause() {


         super.onPause()
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).get()
            .addOnSuccessListener {
                currentUser = it.toObject(USER::class.java)!!
                val a=USER(currentUser.name,currentUser.imageUrl,currentUser.thumbImage,currentUser.deviceToken,currentUser.status,"offline",currentUser.uid,currentUser.number,currentUser.statuspic)
                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).set(a).addOnSuccessListener {
                    Log.d(TAG, "now user is offline")
                }
            }
        Log.d(TAG, "onPause: activity pause")
    }


}