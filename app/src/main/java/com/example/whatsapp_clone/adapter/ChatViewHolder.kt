package com.example.whatsapp_clone

import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsapp_clone.utils.formatAsListItem
import com.example.whatsapp_clone.utils.formatAsTime
import com.puldroid.whatsappclone.adapters.TAG
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_sent.view.*
import kotlinx.android.synthetic.main.listitem1.view.*


class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(item: Inbox, onClick: (name: String, photo: String, id: String,number:String) -> Unit) =
        with(itemView) {
            countTv.isVisible = item.count > 0
            countTv.text = item.count.toString()
            timeTv.text = item.time.formatAsListItem(context)

            var hr:String= item.time.formatAsTime().subSequence(0,2) as String
            var mn:String= item.time.formatAsTime().subSequence(3,5) as String
            val h=hr.toInt()
            val m=mn.toInt()
            Log.d(TAG, "onBindViewHolder: "+mn.toInt())
            if ((h)<12){
                timeTv.text=(h).toString()+":$mn"+" AM"
            }
            else if (h==12){
                timeTv.text=h.toString()+":$mn"+" PM"
            }
            else{
                timeTv.text=(h-12).toString()+":$mn"+"PM"
            }

            titleTv.text = item.name
            subTitleTv.text = item.msg
            Picasso.get()
                .load(item.image)
                .placeholder(R.drawable.ic_baseline_person_24)
                .error(R.drawable.ic_baseline_person_24)
                .into(userImgView)
            setOnClickListener {
                onClick.invoke(item.name, item.image, item.from,item.number)
            }
        }
}