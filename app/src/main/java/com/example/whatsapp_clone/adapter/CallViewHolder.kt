package com.example.whatsapp_clone

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsapp_clone.utils.formatAsListItem
import com.example.whatsapp_clone.utils.formatAsTime
import com.google.firebase.database.FirebaseDatabase
import com.puldroid.whatsappclone.adapters.TAG
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.call_item.view.*
import kotlinx.android.synthetic.main.list_item_sent.view.*
import kotlinx.android.synthetic.main.listitem1.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class CallViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(item: Call) =
        with(itemView) {


            titleT.text = item.frndname
            if (item.status=="call")
            subTitleT.setImageResource(R.drawable.ic_baseline_call_made_24)
            else{
                subTitleT.setImageResource(R.drawable.ic_baseline_call_received_24)
            }
            onday.text=item.date
            Picasso.get()
                .load(item.frndimg)
                .placeholder(R.drawable.ic_baseline_person_24)
                .error(R.drawable.ic_baseline_person_24)
                .into(userImgVie)
            callpls.setOnClickListener {
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:" + item.frndnumber)
                val im= UUID.randomUUID().toString()
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
//                ofPattern(")
                val formatted = current.format(formatter)
                Log.d(TAG, "bind: "+item.myid+" "+item.friendid+" "+item.myimg+" "+item.frndimg+" "+item.mynumber+" "+item.frndnumber+" "+"call"+" "+item.myname+" "+item.frndname+" "+formatted)

                getcall(item.myid, im).setValue(Call(item.myid,item.friendid,item.myimg,item.frndimg,item.mynumber,item.frndnumber,"call",item.myname,item.frndname,formatted))
                getcall(item.friendid, im).setValue(Call(item.friendid,item.myid,item.frndimg,item.myimg,item.frndnumber,item.mynumber,"recieve",item.frndname,item.myname,formatted))
//                getcall(item.friendid, im).setValue(Call(friendId,mCurrentUid,image,currentUser.imageUrl,number,currentUser.number,"recieve",name,currentUser.name,formatted))

                itemView.context.startActivity(callIntent)

            }



        }

    private fun getcall(toUser: String, fromUser: String) =
        FirebaseDatabase.getInstance().reference.child("calls/$toUser/$fromUser")
}