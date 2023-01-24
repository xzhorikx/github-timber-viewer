package alex.zhurkov.github_timber_viewer.feature.main.presentation

import alex.zhurkov.github_timber_viewer.common.arch.StateToModelMapper
import alex.zhurkov.github_timber_viewer.domain.model.GitHubContributorsPage
import alex.zhurkov.github_timber_viewer.feature.main.model.GitHubContributorItem

class MainStateToModelMapper : StateToModelMapper<MainActivityState, MainActivityModel> {
    override fun mapStateToModel(state: MainActivityState): MainActivityModel {
        val items = mapContributors(pages = state.pages, isPageLoading = state.isPageLoading)
        return MainActivityModel(
            items = items,
            isRefreshing = state.isRefreshing
        )
    }

    private fun mapContributors(
        pages: List<GitHubContributorsPage>, isPageLoading: Boolean
    ): List<GitHubContributorItem> {
        val contributors = pages.flatMap { page ->
            page.contributors.map {
                GitHubContributorItem.Data(
                    id = it.id,
                    name = it.login,
                    avatar = it.avatar,
                    contributions = it.contributions,
                    gitHubUrl = it.gitHubUrl
                )
            }
        }
        val loadingIndicators = when (isPageLoading) {
            true -> (0..7).map { GitHubContributorItem.Loading(id = "loading_$it") }
            false -> emptyList()
        }
        return (contributors + loadingIndicators).distinctBy { it.id }
    }
}