package alex.zhurkov.github_timber_viewer.domain.usecase

import alex.zhurkov.github_timber_viewer.domain.model.GitHubContributorsPage

interface GitHubContributorsUseCase {
    suspend fun getContributors(page: Int): GitHubContributorsPage
}
