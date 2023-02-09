package alex.zhurkov.github_timber_viewer.app.di

import alex.zhurkov.github_timber_viewer.data.usecase.GitHubContributorsUseCaseImpl
import alex.zhurkov.github_timber_viewer.domain.repository.GitHubRepository
import alex.zhurkov.github_timber_viewer.domain.usecase.GitHubContributorsUseCase
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
class GitHubDomainModule {

    @Provides
    @Reusable
    fun gitHubContributorsUseCase(
        gitHubRepository: GitHubRepository
    ): GitHubContributorsUseCase = GitHubContributorsUseCaseImpl(
        gitHubRepository = gitHubRepository
    )

}
