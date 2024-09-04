package kz.assignment.presidentserviceconsult.network;

import io.reactivex.rxjava3.core.Observable;
import kz.assignment.presidentserviceconsult.models.Repository;
import kz.assignment.presidentserviceconsult.models.SearchResponse;
import kz.assignment.presidentserviceconsult.models.User;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GitHubApi {
    @GET("search/users")
    Observable<SearchResponse<User>> searchUsers(@Query("q") String query);

    @GET("search/repositories")
    Observable<SearchResponse<Repository>> searchRepositories(@Query("q") String query);
}
