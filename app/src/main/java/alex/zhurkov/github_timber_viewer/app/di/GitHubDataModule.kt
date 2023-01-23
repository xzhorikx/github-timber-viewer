package alex.zhurkov.github_timber_viewer.app.di

import alex.zhurkov.github_timber_viewer.data.mapper.ContributorMapper
import alex.zhurkov.github_timber_viewer.data.remote.GitHubRepositoryImpl
import alex.zhurkov.github_timber_viewer.data.remote.GitHubRemoteSource
import alex.zhurkov.github_timber_viewer.data.remote.model.GitHubContributorResponse
import alex.zhurkov.github_timber_viewer.data.source.ConfigSourceImpl
import alex.zhurkov.github_timber_viewer.domain.config.ConfigSource
import alex.zhurkov.github_timber_viewer.domain.mapper.Mapper
import alex.zhurkov.github_timber_viewer.domain.model.GitHubContributor
import alex.zhurkov.github_timber_viewer.domain.repository.GitHubRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class GitHubDataModule {

    @Provides
    fun gitHubRepository(
        configSource: ConfigSource,
        remoteSource: GitHubRemoteSource,
        contributorRemoteMapper: Mapper<GitHubContributorResponse, GitHubContributor>
    ): GitHubRepository = GitHubRepositoryImpl(
        configSource = configSource,
        remoteSource = remoteSource,
        contributorRemoteMapper = contributorRemoteMapper
    )

    @Provides
    @Singleton
    fun configSource(): ConfigSource = ConfigSourceImpl()

    @Provides
    @Singleton
    fun contributorMapper(): Mapper<GitHubContributorResponse, GitHubContributor> =
        ContributorMapper()

}
