package com.example.whatsapp_clone

import java.util.*

data class USER
    (
    val name: String,
    val imageUrl: String,
    val thumbImage: String,
    val deviceToken: String,
    val status: String,
    val online: String,
    val uid: String,
    var number:String,
    var statuspic:ArrayList<String>,


    )
{

    /** Empty [Constructor] for Firebase */
    constructor() : this("", "", "", "", "Hey There, I am using whatsapp", "offline", "","", arrayListOf<String>())

    constructor(name: String, imageUrl: String, thumbImage: String, uid: String,number:String) :
            this(name, imageUrl, thumbImage, "", uid = uid, status = "Hey There, I am using whatsapp", online = "offline", number = number,statuspic = arrayListOf())

}
