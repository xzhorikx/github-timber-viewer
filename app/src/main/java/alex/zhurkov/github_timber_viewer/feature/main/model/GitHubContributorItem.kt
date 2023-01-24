package alex.zhurkov.github_timber_viewer.feature.main.model

import java.util.*


sealed class GitHubContributorItem {
    abstract val id: String

    data class Data(
        override val id: String,
        val name: String,
        val contributions: Int,
        val avatar: String,
        val gitHubUrl: String
    ) : GitHubContributorItem()

    data class Loading(override val id: String = UUID.randomUUID().toString()) : GitHubContributorItem()
}
