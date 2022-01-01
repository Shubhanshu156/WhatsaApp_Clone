package com.example.whatsapp_clone

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_otp.*
import java.lang.Exception
import java.util.concurrent.TimeUnit
class OtpActivity : AppCompatActivity() {
    lateinit var phonenumber:String
lateinit var progressDialog: ProgressDialog
lateinit var mCountertimer:CountDownTimer
    var mverificationid:String?=null
    lateinit var callbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var mresendingToken:PhoneAuthProvider.ForceResendingToken?=null

        var mAuth=FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        progressDialog=createProgressDialog("Sending OTP please wait",false)
        progressDialog.show()
        val intent=intent
        phonenumber= intent.getStringExtra("PHONENUMBER").toString()
        Toast.makeText(this@OtpActivity, phonenumber, Toast.LENGTH_SHORT).show()
        verify.text="Verify $phonenumber"
        initview()
        startverify()
        check.setOnClickListener {


            val code = otp.text.toString()

            if (!code.isNullOrEmpty() && !mverificationid.isNullOrEmpty()){

                Toast.makeText(this@OtpActivity, "check for null", Toast.LENGTH_SHORT).show()
            val credential=PhoneAuthProvider.getCredential(mverificationid!!,code)


                signInWithPhoneAuthCredential(credential)

        }
            Toast.makeText(this@OtpActivity, "after null", Toast.LENGTH_SHORT).show()


        }

        resend.setOnClickListener {
            if (mresendingToken!=null){
                showtimer(60000)
            }
        }
        showtimer(60000)






    }


    private fun startverify() {
        try{
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phonenumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)}
        catch (e:Exception){
            Toast.makeText(this@OtpActivity, "not ffsf", Toast.LENGTH_SHORT).show()
        }

    }

    private fun initview() {
        val spannable=SpannableString("Waiting to Automatically detect SMS Sent to $phonenumber Wrong Number?")
        val clicablespan= object : ClickableSpan() {
            /**
             * Performs the click action associated with this span.
             */
            override fun onClick(widget: View) {
                val intent=Intent(this@OtpActivity,LoginActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText=false
                ds.color=ds.linkColor
            }

        }
        spannable.setSpan(clicablespan,spannable.length-13,spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        waiting.movementMethod=LinkMovementMethod.getInstance()
        waiting.setText(spannable)

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                val smscode:String?=credential.smsCode
                Toast.makeText(this@OtpActivity, smscode.toString(), Toast.LENGTH_SHORT).show()
                if (!smscode.isNullOrBlank()) {
                    if (smscode.isNotEmpty()){

                        Toast.makeText(this@OtpActivity, "smscode", Toast.LENGTH_SHORT).show()

                    }
                }
                progressDialog=createProgressDialog("Pleasae Wait we are Verifying",false)
                progressDialog.show()
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (progressDialog.isShowing) {
                    progressDialog.dismiss()

                }
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(this@OtpActivity, e.message, Toast.LENGTH_SHORT).show()
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(this@OtpActivity, e.message, Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this@OtpActivity, "not working at all", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                if (progressDialog.isShowing) {
                    progressDialog.dismiss()

                }
                Toast.makeText(this@OtpActivity, "sending messages", Toast.LENGTH_SHORT).show()
                if (progressDialog.isShowing) {progressDialog.dismiss()}
                mverificationid = verificationId
                mresendingToken = token
            }
        }

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Toast.makeText(this, "checking", Toast.LENGTH_SHORT).show()

        mAuth.signInWithCredential(credential).addOnCompleteListener{
            if (it.isSuccessful)
            {
                if (progressDialog.isShowing){
    progressDialog.dismiss()
}
                val intent=Intent(this@OtpActivity,SignupActivity::class.java)
                startActivity(intent)
                finish()
                Toast.makeText(this@OtpActivity, "welcome you are signin", Toast.LENGTH_SHORT).show()
            }

        else{
                if (progressDialog.isShowing){
                    progressDialog.dismiss()
                }

                Toast.makeText(this@OtpActivity, "no Working", Toast.LENGTH_SHORT).show()
        }

    }}

    private fun showtimer(milisec: Long) {
        resend.isEnabled=false
        mCountertimer=object : CountDownTimer (milisec,1000){
            /**
             * Callback fired on regular interval.
             * @param millisUntilFinished The amount of time until finished.
             */
            override fun onFinish() {
                resend.isEnabled=true
                counter.isVisible=false
            }
            override fun onTick(millisUntilFinished: Long) {
                var v=millisUntilFinished/1000
                counter.isVisible=true
                counter.text="Seconds Remaining $v"
            }

            /**
             * Callback fired when the time is up.
             */



        }.start()


    }

    override fun onDestroy() {
        super.onDestroy()
        if (mCountertimer!=null){
            mCountertimer.cancel()
        }
    }
}
fun Context.createProgressDialog(message:String,isCanceable:Boolean):ProgressDialog{
    return ProgressDialog(this).apply {
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        setMessage(message)
    }

}