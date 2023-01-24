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
    override suspend fun getContributors(page: Int, skipCache: Boolean): GitHubContributorsPage {
        val cacheControl = when (skipCache) {
            true -> "no-cache"
            false -> "public, max-stale=${configSource.cacheStaleSec}"
        }
        val response = remoteSource.getContributorsPage(
            limit = configSource.pageSize,
            page = page,
            cacheControl = cacheControl
        )
        return GitHubContributorsPage(
            pageId = page,
            isLastPage = response.isEmpty(),
            contributors = response.map(contributorRemoteMapper::map)
        )
    }
}