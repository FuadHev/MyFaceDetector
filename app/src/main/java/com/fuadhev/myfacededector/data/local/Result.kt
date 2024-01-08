package com.fuadhev.myfacededector.data.local

data class Result(
    val id:Int,
    var left:Boolean=false,
    var right:Boolean=false,
    var smile:Boolean=false,
    var neutral: Boolean=false
) {
}