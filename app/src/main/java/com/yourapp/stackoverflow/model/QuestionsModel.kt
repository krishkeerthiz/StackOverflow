package com.yourapp.stackoverflow.model

import com.squareup.moshi.Json
import java.io.Serializable
import java.security.acl.Owner

data class QuestionsModel(
    var items : List<Items>?,
    @Json(name = "has_more")
    var hasMore : Boolean = false,
    @Json(name = "quota_max")
    var quotaMax : Int = 0,
    @Json(name = "quota_remaining")
    var quotaRemaining : Int = 0,
) : Serializable

data class Items(
    var tags : List<String>,
    var owner : com.yourapp.stackoverflow.model.Owner,
//    @Json(name="is_answered")
//    var isAnswered : Boolean = false,
    @Json(name="view_count")
    var viewCount : Int = 0,
    @Json(name="answer_count")
    var answerCount : Int = 0,
//    var score : Int =0,
//    @Json(name="last_activity_date")
//    var lastActivityDate : Int = 0,
    @Json(name="creation_date")
    var creationDate : Long = 0,
//    @Json(name="question_id")
//    var questionId : Int = 0,
//    @Json(name="content_license")
//    var contentLicense : String,
    var link : String,
    var title : String,
    ) : Serializable

data class Owner(
//    var reputation : Int =0,
//    @Json(name = "user_id")
//    var userId : Int = 0,
    @Json(name = "profile_image")
    var profileImage : String,
    @Json(name = "display_name")
    var displayName : String,
//    var link : String,
//
) :Serializable