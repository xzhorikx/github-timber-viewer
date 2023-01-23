package alex.zhurkov.github_timber_viewer.data.remote.model

import com.google.gson.annotations.SerializedName

data class GitHubContributorResponse(
    @SerializedName("id") val id: String,
    @SerializedName("login") val login: String,
    @SerializedName("avatar_url") val avatar: String
)