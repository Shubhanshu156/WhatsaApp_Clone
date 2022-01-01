package com.example.whatsapp_clone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_show_status.*
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
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


class ShowStatus : AppCompatActivity() {
    val TAG = "STATUS"
    lateinit var progress: SegmentedProgressBar
    private val image: PhotoView? = null
    var count = 1
    lateinit var images: ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_show_status)
        val i = intent
        images = intent.getStringArrayListExtra("images")!!


//
        progress = findViewById<SegmentedProgressBar>(R.id.progrs)
        progress.segmentCount = images.size
        Picasso.get().load(images[0]).placeholder(R.drawable.ic_baseline_person_24).into(statuspic)
        Toast.makeText(this@ShowStatus, "loaded success", Toast.LENGTH_SHORT).show()

        progrs.start()

        progress.listener = object : SegmentedProgressBarListener {
            /**
             * Notifies when last segment finished animating
             */
            override fun onFinished() {
                finish()
                Toast.makeText(this@ShowStatus, "finished", Toast.LENGTH_SHORT).show()
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

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return when (event!!.action) {

            MotionEvent.ACTION_DOWN -> {
                // when we touch or tap on the screen
                Log.d(TAG, "Action was DOWN")
                Toast.makeText(this, "action was down", Toast.LENGTH_SHORT).show()
                true}
                MotionEvent.ACTION_MOVE -> {
                    // while pressing on the screen,
                    // we move our finger
                    Log.d(TAG, "Action was MOVE")
                    Toast.makeText(this, "action was move", Toast.LENGTH_SHORT).show()

                    true
                }
                MotionEvent.ACTION_UP -> {
                    // Lifting up the finger after
                    // pressing on the screen
                    Log.d(TAG, "Action was UP")
                    Toast.makeText(this, "action was up", Toast.LENGTH_SHORT).show()

                    true
                }
                MotionEvent.ACTION_CANCEL -> {
                    Log.d(TAG, "Action was CANCEL")
                    Toast.makeText(this@ShowStatus, "action was cancel", Toast.LENGTH_SHORT).show()

                    true
                }
                MotionEvent.ACTION_OUTSIDE -> {
                    Log.d(TAG, "Movement occurred outside of screen element")
                    Toast.makeText(this@ShowStatus, "action was move outside", Toast.LENGTH_SHORT).show()

                    true
                }
                else -> super.onTouchEvent(event)

            }
    }



}