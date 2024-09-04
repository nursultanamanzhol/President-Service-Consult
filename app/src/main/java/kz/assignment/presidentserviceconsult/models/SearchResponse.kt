package kz.assignment.presidentserviceconsult.models

data class SearchResponse<T>(
    val items: List<T>
)