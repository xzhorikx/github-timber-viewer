package alex.zhurkov.github_timber_viewer.feature.main.presentation

import alex.zhurkov.github_timber_viewer.common.arch.Reducer

class MainActivityReducer : Reducer<MainActivityState, MainActivityChange> {
    override fun reduce(state: MainActivityState, change: MainActivityChange) = when (change) {
        is MainActivityChange.PageLoaded -> state.copy(pages = state.pages + change.data)
        is MainActivityChange.PageLoadingChanged -> state.copy(isPageLoading = change.isLoading)
        MainActivityChange.ItemsCleared -> state.copy(pages = emptyList())
        is MainActivityChange.RefreshChanged -> state.copy(isRefreshing = change.isRefreshing)
        is MainActivityChange.LastVisibleItemChanged -> state.copy(lastVisibleItemId = change.id)
    }
}
