package com.example.whatsapp_clone

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_signup.*
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_login.*
import java.lang.Exception
import android.content.SharedPreferences





class SignupActivity : AppCompatActivity() {
    lateinit var nexbtn:Button
    lateinit var progressDialog: ProgressDialog
    val storage by lazy {
        FirebaseStorage.getInstance()
    }
    val mAuth by lazy {
        FirebaseAuth.getInstance()
    }
    val database by lazy {
        FirebaseFirestore.getInstance()
    }
    val TAG = "welcome"
    lateinit var downloadurl: String

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_signup)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading ")
        progressDialog.setMessage("Updating Your Status, please wait")

        userimage.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        nexbtn=findViewById(R.id.   nexbtn2)
        nexbtn.setOnClickListener {
            if (username.text.toString().isNullOrEmpty() or (downloadurl==null)){
                Toast.makeText(this@SignupActivity, "One of Argument is Null", Toast.LENGTH_SHORT).show()
            }
            else{

//
                val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
                val s1 = sh.getString("number", "")
                Log.d(TAG, "number is$s1")

                val user=USER(username.text.toString(),downloadurl,downloadurl,mAuth.uid!!, number = s1!!)
                database.collection("users").document(mAuth.uid!!).set(user).addOnSuccessListener {
                    Toast.makeText(this@SignupActivity, "Account Created", Toast.LENGTH_SHORT).show()
                    val intent=Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()

                }.addOnFailureListener {
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                    nexbtn2.isEnabled=true
                }
            }
        }
        checkpermission()
        userimage.setOnClickListener {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "image/*"

            val pickIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select Image")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
            startActivityForResult(chooserIntent, 1000)
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkpermission() {

        if ((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) ||
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
        ) {
            val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            val permissionwrite = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestPermissions(permission, 1001)
            requestPermissions(permissionwrite, 1002)

        } else {
            pickfromgallery()
        }


    }

    private fun pickfromgallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading ")
        progressDialog.setMessage("Updating Your Profile Pic, please wait")
        progressDialog.show()
        if (resultCode == Activity.RESULT_OK && requestCode == 1000) {
            Toast.makeText(this@SignupActivity, "success", Toast.LENGTH_SHORT).show()
            if (data != null) {
                data.data.let {
                    userimage.setImageURI(it)
                    try {
                        uploadimage(it)

                    }
                    catch (e: Exception) {
                        progressDialog.dismiss()
                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    }
                }}
                else{
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@SignupActivity,
                        "$resultCode$requestCode",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }}
        fun uploadimage(it: Uri?) {


                nexbtn2.isEnabled = false
                val ref = storage.reference.child("uploads/" + mAuth.uid.toString())
                val uploadTask = ref.putFile(it!!)

                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation ref.downloadUrl
                }).addOnCompleteListener {
                    if (it.isSuccessful) {
                        downloadurl = it.result.toString()
                        nexbtn2.isEnabled = true
                       progressDialog.dismiss()
                    } else {
                        progressDialog.dismiss()
                        Toast.makeText(
                            this@SignupActivity,
                            it.exception!!.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }


            }
        }

