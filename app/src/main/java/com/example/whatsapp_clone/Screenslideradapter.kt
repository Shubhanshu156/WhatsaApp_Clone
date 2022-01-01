package com.example.whatsapp_clone
//src/test/java/com/example/whatsapp_clone/Screenslideradapter.kt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter

class Screenslideradapter(fa: FragmentActivity) : FragmentStateAdapter(fa){

    override fun getItemCount(): Int {
        return  3
    }

    override fun createFragment(position: Int): Fragment=when(position){
        0->InboxFragment()
        1->statusFragment()

        else -> {
            CallFragment()
        }
    }


}
