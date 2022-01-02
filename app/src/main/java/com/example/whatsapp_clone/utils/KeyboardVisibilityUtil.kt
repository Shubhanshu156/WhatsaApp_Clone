//
package com.example.whatsapp_clone.utils

import android.graphics.Rect
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.whatsapp_clone.USER
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class KeyboardVisibilityUtil(contentView: View, onKeyboardShown: (Boolean) -> Unit) {

    private var currentKeyboardState: Boolean = false
    val TAG="keyboard"
lateinit var currentUser:USER
    val visibilityListener = {
        val rectangle = Rect()
        contentView.getWindowVisibleDisplayFrame(rectangle)
        val screenHeight = contentView.rootView.height

        // r.bottom is the position above soft keypad or device button.
        // If keypad is shown, the rectangle.bottom is smaller than that before.
        val keypadHeight = screenHeight.minus(rectangle.bottom)
        // 0.15 ratio is perhaps enough to determine keypad height.
        val isKeyboardNowVisible = keypadHeight > screenHeight * 0.15

        if (currentKeyboardState != isKeyboardNowVisible) {
            if (isKeyboardNowVisible) {
                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).get()
                    .addOnSuccessListener {
                        currentUser = it.toObject(USER::class.java)!!
                        val a= USER(currentUser.name,currentUser.imageUrl,currentUser.thumbImage,currentUser.deviceToken,currentUser.status,"Typing...",currentUser.uid,currentUser.number,currentUser.statuspic)
                        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).set(a).addOnSuccessListener {
                            Log.d(TAG, "now user is offline")
                        }
                    }
                onKeyboardShown(false)
            } else {
                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).get()
                    .addOnSuccessListener {
                        currentUser = it.toObject(USER::class.java)!!
                        val a= USER(currentUser.name,currentUser.imageUrl,currentUser.thumbImage,currentUser.deviceToken,currentUser.status,"online",currentUser.uid,currentUser.number,currentUser.statuspic)
                        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).set(a).addOnSuccessListener {
                            Log.d(TAG, "now user is offline")
                        }
                    }
                onKeyboardShown(true)
            }
        }
        currentKeyboardState = isKeyboardNowVisible
    }
}