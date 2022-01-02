package com.example.whatsapp_clone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_show_status.*
import android.os.CountDownTimer
import android.util.Log
import android.view.View

import com.github.chrisbanes.photoview.PhotoView
import com.squareup.picasso.Callback
import java.lang.Exception



import jp.shts.android.storiesprogressview.StoriesProgressView;
import kotlinx.android.synthetic.main.activity_otp.*
import kotlinx.android.synthetic.main.activity_show_status.view.*
import pt.tornelas.segmentedprogressbar.Segment
import pt.tornelas.segmentedprogressbar.SegmentedProgressBar
import pt.tornelas.segmentedprogressbar.SegmentedProgressBarListener
import android.view.MotionEvent
import android.view.View.OnTouchListener
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider


class ShowStatus : AppCompatActivity() {
    val TAG = "STATUS"
    lateinit var progress: SegmentedProgressBar
    private val image: PhotoView? = null
    var count = 1
    lateinit var images: ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        EmojiManager.install(GoogleEmojiProvider())
        setContentView(R.layout.activity_show_status)

        val i = intent
        images = intent.getStringArrayListExtra("images")!!
        val dp=intent.getStringExtra("dp")
        val nm=intent.getStringExtra("nm")
        Log.d(TAG, "onCreate: "+dp+nm)
        if (dp==null || nm==null){
            statstv.visibility=View.GONE
            statuswatch.visibility=View.GONE

        }
        else {
            statstv.visibility=View.VISIBLE
            statuswatch.visibility=View.VISIBLE
            statstv.text = nm
            Picasso.get().load(dp).placeholder(R.drawable.ic_baseline_person_24).into(statuswatch)
        }
goback.setOnClickListener {
    finish()
}

//
        progress = findViewById<SegmentedProgressBar>(R.id.progrs)
        progress.segmentCount = images.size
        Picasso.get().load(images[0]).placeholder(R.drawable.ic_baseline_person_24).into(statuspic)


        progrs.start()

        progress.listener = object : SegmentedProgressBarListener {
            /**
             * Notifies when last segment finished animating
             */
            override fun onFinished() {
                finish()

            }

            override fun onPage(oldPageIndex: Int, newPageIndex: Int) {


                Picasso.get().load(images[newPageIndex]).placeholder(R.drawable.ic_baseline_person_24).into(statuspic,
                    object : Callback {
                        override fun onSuccess() {



                        }

                        override fun onError(e: Exception?) {

                        }
                    })

            }
//
        }
        reverse.setOnClickListener {
            progress.previous()
        }
        skip.setOnClickListener {
            progress.next()
        }
    }



}