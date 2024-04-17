package com.fake.fakebook

data class Posts(

    val imageUrl: String,
    val username: String,
    val caption: String,
    val date: String,
    val hates: String,
    val likes: String,
    var isLiked: Boolean = false,
    var likesCount: Int = 0,
    var isHated: Boolean = false,
    var HatesCount: Int = 0
){
    constructor() : this("", "", "", "", "", "") // Empty constructor with default values
}
