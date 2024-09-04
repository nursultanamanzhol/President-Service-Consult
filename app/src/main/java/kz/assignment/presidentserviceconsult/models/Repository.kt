package kz.assignment.presidentserviceconsult.models

data class Repository(
    val name: String,
    val description: String?,
    val forks_count: Int,
    val html_url: String
)

