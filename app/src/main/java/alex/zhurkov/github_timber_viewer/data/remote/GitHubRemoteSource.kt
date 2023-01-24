package alex.zhurkov.github_timber_viewer.data.remote

import alex.zhurkov.github_timber_viewer.data.remote.model.GitHubContributorResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface GitHubRemoteSource {
    @GET("repos/JakeWharton/timber/contributors")
    suspend fun getContributorsPage(
        @Query("page") page: Int,
        @Query("per_page") limit: Int,
        @Header("Cache-Control") cacheControl: String
    ): List<GitHubContributorResponse>
}
