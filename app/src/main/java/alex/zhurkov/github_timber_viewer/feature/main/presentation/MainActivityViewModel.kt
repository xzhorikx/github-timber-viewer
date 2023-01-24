package alex.zhurkov.github_timber_viewer.feature.main.presentation

import alex.zhurkov.github_timber_viewer.common.arch.BaseViewModel
import alex.zhurkov.github_timber_viewer.common.arch.Reducer
import alex.zhurkov.github_timber_viewer.common.arch.StateToModelMapper
import alex.zhurkov.github_timber_viewer.common.whenTrue
import alex.zhurkov.github_timber_viewer.domain.usecase.GitHubContributorsUseCase
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.CancellationException

class MainActivityViewModel(
    private val gitHubContributorsUseCase: GitHubContributorsUseCase,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    reducer: Reducer<MainActivityState, MainActivityChange>,
    stateToModelMapper: StateToModelMapper<MainActivityState, MainActivityModel>
) : BaseViewModel<MainActivityAction, MainActivityChange, MainActivityState, MainActivityModel>(
    dispatcher = dispatcher, reducer = reducer, stateToModelMapper = stateToModelMapper
) {
    override var state = MainActivityState.EMPTY
    private var pageJob: Job? = null
    override fun onObserverActive(isFirstTime: Boolean) {
        super.onObserverActive(isFirstTime)
        if (isFirstTime) {
            state.nextPage?.let(::loadContributorsPage)
        }
    }

    override suspend fun provideChangesObservable(): Flow<MainActivityChange> {
        return emptyFlow()
    }

    override fun onStateUpdated(oldState: MainActivityState, newState: MainActivityState) {
        super.onStateUpdated(oldState, newState)
        val isLastGifUpdated = oldState.lastVisibleItemId != newState.lastVisibleItemId
        val shouldLoadNextPage =
            isLastGifUpdated && newState.lastContributorId == newState.lastVisibleItemId
        shouldLoadNextPage.whenTrue {
            state.nextPage?.let(::loadContributorsPage)
        }
    }

    override fun processAction(action: MainActivityAction) = when (action) {
        MainActivityAction.Refresh -> refreshContributors()
        is MainActivityAction.LastVisibleItemChanged -> {
            if (action.id != state.lastVisibleItemId) {
                sendChange(MainActivityChange.LastVisibleItemChanged(action.id))
            }
            Unit
        }
    }

    override fun onCleared() {
        super.onCleared()
        pageJob?.cancel()
    }

    private fun refreshContributors() {
        sendChange(MainActivityChange.ItemsCleared)
        loadContributorsPage(pageIndex = state.initialPageId, isRefreshing = true)
    }

    private fun loadContributorsPage(pageIndex: Int, isRefreshing: Boolean = false) {
        when (isRefreshing) {
            true -> pageJob?.cancel(CancellationException("Force refresh"))
            false -> {
                if (state.isLastPageLoaded) return
                if (state.isPageLoading) return
                if (state.isPageLoaded(pageIndex)) return
            }
        }

        pageJob = viewModelScope.launch(dispatcher) {
            execute(
                action = {
                    gitHubContributorsUseCase.getContributors(
                        page = pageIndex,
                        skipCache = isRefreshing
                    )
                },
                onStart = {
                    sendChange(MainActivityChange.PageLoadingChanged(isLoading = true))
                    isRefreshing.whenTrue {
                        sendChange(MainActivityChange.RefreshChanged(isRefreshing = true))
                    }
                },
                onComplete = {
                    sendChange(MainActivityChange.PageLoadingChanged(isLoading = false))
                    isRefreshing.whenTrue {
                        sendChange(MainActivityChange.RefreshChanged(isRefreshing = false))
                    }
                },
                onErrorOccurred = { onPageLoadingError(it) },
                onSuccess = { sendChange(MainActivityChange.PageLoaded(it)) }
            )
        }
    }

    private fun onPageLoadingError(e: Throwable) {
        sendEvent(MainActivityEvent.DisplayError(e))
        Timber.e(e)
    }
}
