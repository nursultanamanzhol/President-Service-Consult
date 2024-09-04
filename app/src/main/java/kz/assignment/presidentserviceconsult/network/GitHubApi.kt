package kz.assignment.presidentserviceconsult.network

import io.reactivex.rxjava3.core.Observable
import kz.assignment.presidentserviceconsult.models.Repository
import kz.assignment.presidentserviceconsult.models.SearchResponse
import kz.assignment.presidentserviceconsult.models.User
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubApi {
    @GET("search/users")
    fun searchUsers(@Query("q") query: String): Observable<SearchResponse<User>>

    @GET("search/repositories")
    fun searchRepositories(@Query("q") query: String): Observable<SearchResponse<Repository>>
}
