package alex.zhurkov.github_timber_viewer.feature.main.presentation

import alex.zhurkov.github_timber_viewer.common.arch.UIState
import alex.zhurkov.github_timber_viewer.domain.model.GitHubContributorsPage

data class MainActivityState(
    val pages: List<GitHubContributorsPage>,
    val isPageLoading: Boolean
) : UIState {
    companion object {
        val EMPTY = MainActivityState(
            pages = emptyList(),
            isPageLoading = false
        )
    }

    val nextPage = (pages.lastOrNull()?.pageId ?: 0) + 1
}
