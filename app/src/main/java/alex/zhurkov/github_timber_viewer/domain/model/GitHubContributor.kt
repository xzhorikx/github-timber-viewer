package alex.zhurkov.github_timber_viewer.domain.model

data class GitHubContributor(
    val id: String,
    val login: String,
    val avatar: String,
    val contributions: Int,
    val gitHubUrl: String,
)