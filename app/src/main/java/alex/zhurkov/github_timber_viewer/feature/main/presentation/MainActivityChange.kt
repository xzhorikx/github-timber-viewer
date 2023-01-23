package alex.zhurkov.github_timber_viewer.feature.main.presentation

import alex.zhurkov.github_timber_viewer.common.arch.UIStateChange
import alex.zhurkov.github_timber_viewer.domain.model.GitHubContributorsPage

sealed class MainActivityChange : UIStateChange {
    data class PageLoadingChanged(val isLoading: Boolean) : MainActivityChange()
    data class PageLoaded(val data: GitHubContributorsPage) : MainActivityChange()
}
