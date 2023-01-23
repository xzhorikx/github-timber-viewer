package alex.zhurkov.github_timber_viewer.feature.main.model

import java.util.*


sealed class GitHubContributerItem {
    abstract val id: String

    data class Data(
        override val id: String,
        val name: String,
        val avatar: String
    ) : GitHubContributerItem()

    data class Loading(override val id: String = UUID.randomUUID().toString()) : GitHubContributerItem()
}
