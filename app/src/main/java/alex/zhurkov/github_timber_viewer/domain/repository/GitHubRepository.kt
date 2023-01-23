package alex.zhurkov.github_timber_viewer.domain.repository

import alex.zhurkov.github_timber_viewer.domain.model.GitHubContributorsPage

interface GitHubRepository {
    suspend fun getContributors(page: Int): GitHubContributorsPage
}
