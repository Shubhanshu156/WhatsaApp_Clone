package com.example.whatsapp_clone

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsapp_clone.adapter.EmptyViewHolder
import com.example.whatsapp_clone.adapter.StatusViewHolder



import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.fragment_status.*
import kotlinx.android.synthetic.main.fragment_status.view.*
import java.lang.Exception
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [statusFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class statusFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    lateinit var currentUser:USER
    private var param2: String? = null
    private val TAG="UPLOADING"
    lateinit var progressDialog:ProgressDialog

    lateinit var downloadurl: String
    val mauth= FirebaseAuth.getInstance()
    var query: Query = FirebaseFirestore.getInstance().collection("users")
    lateinit var madapter: FirestoreRecyclerAdapter<USER, RecyclerView.ViewHolder>
    lateinit var statusimage:String
    val storage by lazy {
        FirebaseStorage.getInstance()
    }
    val database by lazy {
        FirebaseFirestore.getInstance().collection("users")
            .orderBy("name", Query.Direction.DESCENDING).get( )
    }
    val profile by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val parent=inflater.inflate(R.layout.fragment_status, container, false)
        val options: FirestoreRecyclerOptions<USER> =
            FirestoreRecyclerOptions.Builder<USER>()
                .setQuery(query, USER::class.java)
                .build()
        FirebaseFirestore.getInstance().collection("users").document(mauth.uid!!).get()
            .addOnSuccessListener {
                currentUser = it.toObject(USER::class.java)!!
                val name: String=currentUser.name
                val imageUrl: String=currentUser.imageUrl
                val thumbImage: String=currentUser.thumbImage
                val deviceToken: String=currentUser.deviceToken
                val status: String=currentUser.status
                val online=currentUser.online
                val uid: String=currentUser.uid
            Picasso.get().load(imageUrl).placeholder(R.drawable.ic_baseline_person_24).
            into(parent.selfimage)
            }
        madapter= object : FirestoreRecyclerAdapter<USER, RecyclerView.ViewHolder>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                if (viewType==1){
                    return  EmptyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.emptylayout,parent,false))
                }
                val view=LayoutInflater.from(parent.context).inflate(R.layout.listitem1,parent,false)
                return StatusViewHolder(view)
            }



            override fun getItemViewType(position: Int): Int {
                val item = getItem(position).statuspic
                val a=getItem(position).uid
                if ((item.size==0 ) or (a==mauth.uid)){
                    return  1
                }
                else {
                    return 2
                }
                return super.getItemViewType(position)
            }

            /**
             * @param model the model object containing the data that should be used to populate the view.
             * @see .onBindViewHolder
             */
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: USER){
                if (holder is StatusViewHolder){
                    holder.setdetails(model)}
                else{
                    //Todo-Something
                }
            }


        }
        parent.statusrcv.apply {
            adapter=madapter
            layoutManager=LinearLayoutManager(context)
        }
        parent.setstatusa.setOnClickListener{

            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "image/*"

            val pickIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select Image")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
            startActivityForResult(chooserIntent, 1000)
        }
        parent.setstatus.setOnClickListener {
            FirebaseFirestore.getInstance().collection("users").document(mauth.uid!!).get()
                .addOnSuccessListener {
                    currentUser = it.toObject(USER::class.java)!!
                    val name: String=currentUser.name
                    val imageUrl: String=currentUser.imageUrl
                    val thumbImage: String=currentUser.thumbImage
                    val deviceToken: String=currentUser.deviceToken
                    val status: String=currentUser.status
                    val online=currentUser.online
                    val uid: String=currentUser.uid
                    var statuspic=currentUser.statuspic
                    val intent=Intent(context,ShowStatus::class.java)

                    intent.putStringArrayListExtra("images",statuspic)
                    if (statuspic.size>0){
                    requireContext().startActivity(intent)}}


        }



        return parent
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Uploading ")
        progressDialog.setMessage("Updating Your Status, please wait")
        progressDialog.show()

        if (resultCode == Activity.RESULT_OK && requestCode == 1000) {
            Toast.makeText(context, "success", Toast.LENGTH_SHORT).show()
            if (data != null) {
                data.data.let {

                    try {
                        Log.d(TAG, "onActivityResult: "+it)
                        uploadimage(it)

                }
                    catch (e:Exception){
                        progressDialog.dismiss()
                    }
                }


        }
            else{
                progressDialog.dismiss()
            }
        }
        else{
            progressDialog.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        madapter.startListening()
        madapter.notifyDataSetChanged()
    }
    fun uploadimage(it: Uri?) {


        val ref = storage.reference.child("status/" + UUID.randomUUID().toString())
        val uploadTask = ref.putFile(it!!)
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                    progressDialog.dismiss()
                    Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()

                }
            }
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener {
            if (it.isSuccessful) {
                downloadurl = it.result.toString()
                FirebaseFirestore.getInstance().collection("users").document(mauth.uid!!).get()
                    .addOnSuccessListener {
                        currentUser = it.toObject(USER::class.java)!!
                        val name: String=currentUser.name
                        val imageUrl: String=currentUser.imageUrl
                        val thumbImage: String=currentUser.thumbImage
                        val deviceToken: String=currentUser.deviceToken
                        val status: String=currentUser.status
                        val online=currentUser.online
                        val uid: String=currentUser.uid
                        var statuspic=currentUser.statuspic
                        var number=currentUser.number
                        Log.d(TAG, "before adding download url "+statuspic)
                        statuspic.add(downloadurl)
                        downloadurl=""
                        Log.d(TAG, "after adding download url"+statuspic)
                        val nextuser=USER(name,imageUrl,thumbImage,deviceToken,status,online,uid,number,statuspic)
                        FirebaseFirestore.getInstance().collection("users").document(mauth.uid!!).set(nextuser).addOnSuccessListener {

                            Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show()
                            progressDialog.dismiss()   

                        }

                    }



            } else {
                progressDialog.dismiss()
//                Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
                Toast.makeText(
                    context,
                    it.exception!!.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    }

    override fun onStop() {
        super.onStop()
        if (madapter != null) {
            madapter.stopListening()
        }
    }



    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            statusFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}