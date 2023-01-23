package alex.zhurkov.github_timber_viewer.data.usecase

import alex.zhurkov.github_timber_viewer.domain.model.GitHubContributorsPage
import alex.zhurkov.github_timber_viewer.domain.repository.GitHubRepository
import alex.zhurkov.github_timber_viewer.domain.usecase.GitHubContributorsUseCase

class GitHubContributorsUseCaseImpl(
    private val gitHubRepository: GitHubRepository
) : GitHubContributorsUseCase {
    override suspend fun getContributors(page: Int): GitHubContributorsPage =
        gitHubRepository.getContributors(page)
}
