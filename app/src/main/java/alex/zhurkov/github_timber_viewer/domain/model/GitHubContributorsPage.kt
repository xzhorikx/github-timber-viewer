package alex.zhurkov.github_timber_viewer.domain.model

data class GitHubContributorsPage(
    val pageId: Int,
    val isLastPage: Boolean,
    val contributors: List<GitHubContributor>
)
