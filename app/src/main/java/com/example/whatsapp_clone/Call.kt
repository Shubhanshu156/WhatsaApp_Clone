package com.example.whatsapp_clone

import java.util.*

data class Call(

    val myid:String,
    val friendid:String,

    val myimg:String,
    val frndimg:String,

    val mynumber:String,
    val frndnumber:String,

    val status:String,

    val myname:String,
    val frndname:String,
    val date:String,




)
{
    constructor():this("","","", "","","","","","","")
}
