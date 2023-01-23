package alex.zhurkov.github_timber_viewer.feature.main.presentation

import alex.zhurkov.github_timber_viewer.common.arch.StateToModelMapper
import alex.zhurkov.github_timber_viewer.domain.model.GitHubContributorsPage
import alex.zhurkov.github_timber_viewer.feature.main.model.GitHubContributerItem

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
    ): List<GitHubContributerItem> {
        val contributors = pages.flatMap { page ->
            page.contributors.map {
                GitHubContributerItem.Data(
                    id = it.id, name = it.login, avatar = it.avatar
                )
            }
        }
        val loadingIndicators = when (isPageLoading) {
            true -> (0..7).map { GitHubContributerItem.Loading(id = "loading_$it") }
            false -> emptyList()
        }
        return (contributors + loadingIndicators).distinctBy { it.id }
    }
}