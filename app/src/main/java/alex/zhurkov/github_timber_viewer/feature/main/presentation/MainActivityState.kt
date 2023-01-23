package alex.zhurkov.github_timber_viewer.feature.main.presentation

import alex.zhurkov.github_timber_viewer.common.arch.UIState
import alex.zhurkov.github_timber_viewer.domain.model.GitHubContributorsPage

data class MainActivityState(
    val pages: List<GitHubContributorsPage>,
    val isPageLoading: Boolean,
    val isRefreshing: Boolean,
    val lastVisibleItemId: String?
) : UIState {
    fun isPageLoaded(pageId: Int): Boolean = pages.any { it.pageId == pageId }

    companion object {
        val EMPTY = MainActivityState(
            pages = emptyList(),
            isPageLoading = false,
            isRefreshing = false,
            lastVisibleItemId = null
        )
    }

    val nextPage = (pages.lastOrNull()?.pageId ?: 0) + 1

    val lastContributorId = pages.flatMap { it.contributors }.lastOrNull()?.id
}
