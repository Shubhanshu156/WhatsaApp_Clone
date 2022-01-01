package com.puldroid.whatsappclone.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsapp_clone.ChatEvent
import com.example.whatsapp_clone.DateHeader
import com.example.whatsapp_clone.Message
import com.example.whatsapp_clone.R
import com.example.whatsapp_clone.utils.*
import kotlinx.android.synthetic.main.list_item_date_header.view.*
import kotlinx.android.synthetic.main.list_item_sent.view.*
import kotlinx.android.synthetic.main.list_item_sent.view.content
import kotlinx.android.synthetic.main.list_item_sent.view.highFiveImg
import kotlinx.android.synthetic.main.list_item_sent.view.time
import kotlinx.android.synthetic.main.text_recieve.view.*
import java.util.*
val TAG="VALUE"

public class ChatAdapter(private val list: MutableList<ChatEvent>, private val mCurrentUser: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var highFiveClick: ((id: String, status: Boolean) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflate = { layout: Int ->
            LayoutInflater.from(parent.context)
                .inflate(layout, parent, false)
        }
        return when (viewType) {
            TEXT_MESSAGE_RECEIVED -> {
                MessageHolder(
                    inflate(R.layout.text_recieve)
                )
            }
            TEXT_MESSAGE_SENT -> {
                MessageHolder(
                    inflate(R.layout.list_item_sent)
                )
            }
            DATE_HEADER -> {
                DateHeaderHolder(
                    inflate(R.layout.list_item_date_header)
                )
            }
            else -> {
                MessageHolder(
                    inflate(R.layout.text_recieve)
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {

        return when (val event = list[position]) {
            is Message -> {
                if (event.senderId == mCurrentUser) {
                    TEXT_MESSAGE_SENT
                } else {
                    TEXT_MESSAGE_RECEIVED
                }
            }
            is DateHeader -> DATE_HEADER
            else -> UNSUPPORTED
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = list[position]) {

            is DateHeader -> {
                if (item.sentAt.isToday()){
                    Log.d(TAG, "onBindViewHolder: 1")
                    holder.itemView.textView.text="Today"
                }
                else if (item.sentAt.isYesterday()){
                    Log.d(TAG, "onBindViewHolder: 2")
                    holder.itemView.textView.text = item.sentAt.formatAsYesterday(holder.itemView.context)
                }
                else if (item.sentAt.isThisWeek()){
                    Log.d(TAG, "onBindViewHolder: 3")
                    holder.itemView.textView.text = item.sentAt.formatAsWeekDay(holder.itemView.context)
                }
                else{
                    Log.d(TAG, "onBindViewHolder: 4")
                    holder.itemView.textView.text=item.sentAt.formatAsFull(holder.itemView.context)
                }

//                holder.itemView.textView.text = item.sentAt.formatAsWeekDay(holder.itemView.context)
            }
            is Message -> {
                holder.itemView.content.text = item.msg
                holder.itemView.time.text = item.sentAt.formatAsTime()
                var hr:String= item.sentAt.formatAsTime().subSequence(0,2) as String
                var mn:String= item.sentAt.formatAsTime().subSequence(3,5) as String
                val h=hr.toInt()
                val m=mn.toInt()
                Log.d(TAG, "onBindViewHolder: "+mn.toInt())
                if ((h)<12){
                    holder.itemView.time.text=(h).toString()+":$mn"+"AM"
                }
                else if (h==12){
                    holder.itemView.time.text=h.toString()+":$mn"+"PM"
                }
                else{
                    holder.itemView.time.text=(h-12).toString()+":$mn"+"PM"
                }
                when (getItemViewType(position)) {
                    TEXT_MESSAGE_RECEIVED -> {
                        holder.itemView.messageCardView.setOnClickListener(object :
                            DoubleClickListener() {
                            override fun onDoubleClick(v: View?) {
                                highFiveClick?.invoke(item.msgId, !item.liked)
                            }
                        })
                        holder.itemView.highFiveImg.apply {
                            isVisible = position == itemCount - 1 || item.liked
                            isSelected = item.liked
                            setOnClickListener {
                                highFiveClick?.invoke(item.msgId, !isSelected)
                            }
                        }
                    }

                    TEXT_MESSAGE_SENT -> {
                        holder.itemView.highFiveImg.apply {
                            isVisible = item.liked
                        }
                    }
                }
            }
        }
    }

    class DateHeaderHolder(view: View) : RecyclerView.ViewHolder(view)

    class MessageHolder(view: View) : RecyclerView.ViewHolder(view)

    companion object {
        private const val UNSUPPORTED = -1
        private const val TEXT_MESSAGE_RECEIVED = 0
        private const val TEXT_MESSAGE_SENT = 1
        private const val DATE_HEADER = 2
    }

}

abstract class DoubleClickListener : View.OnClickListener {
    var lastClickTime: Long = 0
    override fun onClick(v: View?) {
        val clickTime = System.currentTimeMillis()
        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
            onDoubleClick(v)
            lastClickTime = 0
        }
//        else {
//            onSingleClick(v)
//        }
        lastClickTime = clickTime
    }

    //    abstract fun onSingleClick(v: View?)
    abstract fun onDoubleClick(v: View?)

    companion object {
        private const val DOUBLE_CLICK_TIME_DELTA: Long = 300 //milliseconds
    }
}