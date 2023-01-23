package alex.zhurkov.github_timber_viewer.data.remote

import alex.zhurkov.github_timber_viewer.data.remote.model.GitHubContributorResponse
import alex.zhurkov.github_timber_viewer.domain.config.ConfigSource
import alex.zhurkov.github_timber_viewer.domain.mapper.Mapper
import alex.zhurkov.github_timber_viewer.domain.model.GitHubContributor
import alex.zhurkov.github_timber_viewer.domain.model.GitHubContributorsPage
import alex.zhurkov.github_timber_viewer.domain.repository.GitHubRepository

class GitHubRepositoryImpl(
    private val configSource: ConfigSource,
    private val remoteSource: GitHubRemoteSource,
    private val contributorRemoteMapper: Mapper<GitHubContributorResponse, GitHubContributor>
) : GitHubRepository {
    override suspend fun getContributors(page: Int): GitHubContributorsPage {
        val result = remoteSource.getContributorsPage(
            limit = configSource.pageSize,
            page = page
        )
        return GitHubContributorsPage(
            pageId = page,
            contributors = result.map(contributorRemoteMapper::map)
        )
    }
}