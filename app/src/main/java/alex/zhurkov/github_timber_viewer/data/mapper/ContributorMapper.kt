package alex.zhurkov.github_timber_viewer.data.mapper

import alex.zhurkov.github_timber_viewer.data.remote.model.GitHubContributorResponse
import alex.zhurkov.github_timber_viewer.domain.mapper.Mapper
import alex.zhurkov.github_timber_viewer.domain.model.GitHubContributor

class ContributorMapper : Mapper<GitHubContributorResponse, GitHubContributor> {
    override fun map(from: GitHubContributorResponse): GitHubContributor = GitHubContributor(
        id = from.id,
        login = from.login,
        avatar = from.avatar
    )
}
