package com.example.whatsapp_clone

import java.util.*

data class Inbox(
    val msg:String,
    var from:String,
    var name:String,
    var image:String,
    val time:Date=Date(),
    var count:Int=0,
var number:String,

){
    constructor():this("","","","",Date(),0,"")
}
