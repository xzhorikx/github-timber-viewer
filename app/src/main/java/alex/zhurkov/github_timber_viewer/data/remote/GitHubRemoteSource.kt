package alex.zhurkov.github_timber_viewer.data.remote

import alex.zhurkov.github_timber_viewer.data.remote.model.GitHubContributorResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface GitHubRemoteSource {
    @GET("repos/JakeWharton/timber/contributors")
    suspend fun getContributorsPage(
        @Header("page") page: Int,
        @Header("per_page") limit: Int
    ): List<GitHubContributorResponse>
}
