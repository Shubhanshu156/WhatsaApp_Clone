package com.example.whatsapp_clone

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsapp_clone.utils.KeyboardVisibilityUtil
import com.example.whatsapp_clone.utils.isSameDayAs
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.ktx.Firebase
import com.puldroid.whatsappclone.adapters.ChatAdapter
import com.squareup.picasso.Picasso
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.google.GoogleEmojiProvider
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.jar.Manifest
import kotlin.random.Random

const val USER_ID = "userId"
const val USER_THUMB_IMAGE = "thumbImage"
const val USER_NAME = "userName"

class ChatActivity : AppCompatActivity() {

    lateinit var friendId: String
    lateinit var name: String
    lateinit var myself:USER
    lateinit var image: String
     var mCurrentUid: String =FirebaseAuth.getInstance().uid!!
    private val db: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }
    lateinit var currentUser: USER
    lateinit var chatAdapter: ChatAdapter
    lateinit var number:String
    val TAG="WELCOME"

    private lateinit var keyboardVisibilityHelper: KeyboardVisibilityUtil
    private val mutableItems: MutableList<ChatEvent> = mutableListOf()
    private val mLinearLayout: LinearLayoutManager by lazy { LinearLayoutManager(this) }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EmojiManager.install(GoogleEmojiProvider())
        setContentView(R.layout.activity_chat)
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






        friendId= intent.getStringExtra("UID").toString()
        number=intent.getStringExtra("number").toString()
        name=intent.getStringExtra("NAME").toString()
        image=intent.getStringExtra("THUMBIMAGE").toString()
        Log.d(TAG, "onCreate: "+number)
        FirebaseFirestore.getInstance().collection("users").document(friendId).addSnapshotListener { value, error ->

                val p= value!!.toObject(USER::class.java)
            userstatus.visibility= View.VISIBLE
            userstatus.text=p!!.online
//                Log.d(TAG, "onCreate: "+ p!!.online)

            }



        keyboardVisibilityHelper = KeyboardVisibilityUtil(rootView) {
            msgRv.scrollToPosition(mutableItems.size - 1)
        }

        FirebaseFirestore.getInstance().collection("users").document(mCurrentUid).get()
            .addOnSuccessListener {
                currentUser = it.toObject(USER::class.java)!!
            }

        chatAdapter = ChatAdapter(mutableItems, mCurrentUid)
        callk.setOnClickListener {

            if ((checkSelfPermission(android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED)) {
                val permission = arrayOf(android.Manifest.permission.CALL_PHONE)

                requestPermissions(permission, 101)

            }

            else {
                FirebaseFirestore.getInstance().collection("users").document(mCurrentUid).get()
                    .addOnSuccessListener {
                        currentUser = it.toObject(USER::class.java)!!
                        val callIntent = Intent(Intent.ACTION_CALL)
                        callIntent.data = Uri.parse("tel:" + number)
                        val current = LocalDateTime.now()
                        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
                        val formatted = current.format(formatter)
                        val im= UUID.randomUUID().toString()
                        getcall(mCurrentUid, im).setValue(Call(mCurrentUid,friendId,currentUser.imageUrl,image,currentUser.number,number,"call",currentUser.name,name,formatted))

                        getcall(friendId, im).setValue(Call(friendId,mCurrentUid,image,currentUser.imageUrl,number,currentUser.number,"recieve",name,currentUser.name,formatted))
                        val i = intent.getStringExtra("THUMBIMAGE").toString()
                        startActivity(callIntent)
                    }

            }
        }

        msgRv.apply {
            layoutManager = mLinearLayout
            adapter = chatAdapter
        }

        nameTv.text = name
        Picasso.get().load(image).error(R.drawable.circle).placeholder(R.drawable.ic_baseline_person_24).
        into(userImgView)

        val emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(msgEdtv)
        smileBtn.setOnClickListener {
            emojiPopup.toggle()
        }



        sendBtn.setOnClickListener {
            msgEdtv.text?.let {
                if (it.isNotEmpty()) {
                    sendMessage(it.toString())
                    it.clear()
                }
            }
        }

        listenMessages() { msg, update ->
            if (update) {
                updateMessage(msg)
            } else {
                addMessage(msg)
            }
        }

        chatAdapter.highFiveClick = { id, status ->
            updateHighFive(id, status)
        }
        updateReadCount()
    }




    private fun updateReadCount() {
        getInbox(mCurrentUid, friendId).child("count").setValue(0)
    }

    private fun updateHighFive(id: String, status: Boolean) {
        getMessages(friendId).child(id).updateChildren(mapOf("liked" to status))
    }

    private fun addMessage(event: Message) {
        val eventBefore = mutableItems.lastOrNull()

        // Add date header if it's a different day
        if ((eventBefore != null
                    && !eventBefore.sentAt.isSameDayAs(event.sentAt))
            || eventBefore == null
        ) {
            mutableItems.add(
                DateHeader(
                    event.sentAt, this
                )
            )
        }
        mutableItems.add(event)

        chatAdapter.notifyItemInserted(mutableItems.size)
        msgRv.scrollToPosition(mutableItems.size + 1)
    }

    private fun updateMessage(msg: Message) {
        val position = mutableItems.indexOfFirst {
            when (it) {
                is Message -> it.msgId == msg.msgId
                else -> false
            }
        }
        mutableItems[position] = msg

        chatAdapter.notifyItemChanged(position)
    }

    private fun listenMessages(newMsg: (msg: Message, update: Boolean) -> Unit) {
        getMessages(friendId)
            .orderByKey()
            .addChildEventListener(object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildChanged(data: DataSnapshot, p1: String?) {
                    val msg = data.getValue(Message::class.java)!!
                    newMsg(msg, true)
                }

                override fun onChildAdded(data: DataSnapshot, p1: String?) {
                    val msg = data.getValue(Message::class.java)!!
                    newMsg(msg, false)
                }

                override fun onChildRemoved(p0: DataSnapshot) {
                }

            })

    }

    private fun sendMessage(msg: String) {
        val id = getMessages(friendId).push().key
        checkNotNull(id) { "Cannot be null" }
        val msgMap = Message(msg, mCurrentUid, id,number)
        getMessages(friendId).child(id).setValue(msgMap)
        updateLastMessage(msgMap, mCurrentUid)
    }

    private fun updateLastMessage(message: Message, mCurrentUid: String) {
        val inboxMap = Inbox(
            message.msg,
            friendId,
            name,
            image,
            message.sentAt,
            0,
            number
        )

        getInbox(mCurrentUid, friendId).setValue(inboxMap)

        getInbox(friendId, mCurrentUid).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                val value = p0.getValue(Inbox::class.java)
                inboxMap.apply {
                    from = message.senderId
                    name = currentUser.name
                    image = currentUser.thumbImage
                    count = 1
                }
                if (value?.from == message.senderId) {
                    inboxMap.count = value.count + 1
                }
                getInbox(friendId, mCurrentUid).setValue(inboxMap)
            }

        })
    }


    private fun getMessages(friendId: String) = db.reference.child("messages/${getId(friendId)}")

    private fun getInbox(toUser: String, fromUser: String) =
        db.reference.child("chats/$toUser/$fromUser")


    private fun getId(friendId: String): String {
        return if (friendId > mCurrentUid) {
            mCurrentUid + friendId
        } else {
            friendId + mCurrentUid
        }
    }


    private fun getcall(toUser: String, fromUser: String) =
        db.reference.child("calls/$toUser/$fromUser")

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
        rootView.viewTreeObserver
            .addOnGlobalLayoutListener(keyboardVisibilityHelper.visibilityListener)
    }
 
    override fun onStart() {
        super.onStart()
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
    override fun onStop() {
        super.onStop()
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).get()
            .addOnSuccessListener {
                currentUser = it.toObject(USER::class.java)!!
                val a=USER(currentUser.name,currentUser.imageUrl,currentUser.thumbImage,currentUser.deviceToken,currentUser.status,"offline",currentUser.uid,currentUser.number,currentUser.statuspic)
                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).set(a).addOnSuccessListener {
                    Log.d(TAG, "now user is offline")
                }
            }
        Log.d(TAG, "onDestroy: activity destroyed")
    }
    override fun onDestroy() {
        super.onDestroy()
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).get()
            .addOnSuccessListener {
                currentUser = it.toObject(USER::class.java)!!
                val a=USER(currentUser.name,currentUser.imageUrl,currentUser.thumbImage,currentUser.deviceToken,currentUser.status,"offline",currentUser.uid,currentUser.number,currentUser.statuspic)
                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).set(a).addOnSuccessListener {
                    Log.d(TAG, "now user is offline")
                }
            }
        Log.d(TAG, "onDestroy: activity destroyed")
    }

    companion object {

        fun createChatActivity(context: Context, id: String, name: String, image: String,number:String): Intent {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("UID", id)
            intent.putExtra("NAME", name)
            intent.putExtra("THUMBIMAGE", image)
            intent.putExtra("number",number)

            return intent
        }
    }
}