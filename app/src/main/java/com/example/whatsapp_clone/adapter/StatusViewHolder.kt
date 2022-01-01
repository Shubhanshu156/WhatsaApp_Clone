package com.example.whatsapp_clone.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

import com.example.whatsapp_clone.USER
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_signup.view.*
import kotlinx.android.synthetic.main.listitem1.view.*
import android.R
import android.content.Intent
import android.media.Image
import android.widget.ImageView

import android.widget.TextView
import com.example.whatsapp_clone.ChatActivity
import com.example.whatsapp_clone.R.*
import com.example.whatsapp_clone.ShowStatus


class StatusViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    private val view: View
    fun setdetails(user:USER) {
        val countv = view.findViewById<TextView>(id.countTv)
        val titletv = view.findViewById<TextView>(id.titleTv)
        val subtitletv = view.findViewById<TextView>(id.subTitleTv)

        val userimageview = view.findViewById<ImageView>(id.userImgView)
        countv.isEnabled=false
        countv.visibility=View.GONE
        titletv.text=user.name

        Picasso.get().load(user.thumbImage).placeholder(drawable.ic_baseline_person_24)
            .into(userimageview)
        if (user.statuspic.size>0) {
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ShowStatus::class.java)
                intent.putStringArrayListExtra("images", user.statuspic)

                itemView.context.startActivity(intent)
            }
        }




    }



    init {
        view = itemView
    }
}