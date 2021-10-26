package com.yourapp.stackoverflow.model

import java.io.Serializable

class QuestionCardModel :Serializable {
    var title : String = ""
    var name : String? = null
    var date : Long = 0
    var imgUrl : String? = null
    var questionLink : String? = null
    var tags: List<String> = listOf<String>()
    var answerCount : Int = 0
    var viewCount : Int = 0

    constructor()

    constructor(title : String, name : String?, date : Long, imgUrl : String?, link : String?, tags : List<String>, answerCount : Int, viewCount : Int)
    {
        this.title = title
        this.name = name
        this.date = date
        this.imgUrl = imgUrl
        this.questionLink = link
        this.tags = tags
        this.answerCount = answerCount
        this.viewCount = viewCount
    }


}