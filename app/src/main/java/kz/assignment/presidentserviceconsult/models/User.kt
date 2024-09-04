package kz.assignment.presidentserviceconsult.models

data class User(
    val login: String,
    val avatar_url: String,
    val html_url: String,
    val score: Float
)