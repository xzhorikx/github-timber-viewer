package alex.zhurkov.github_timber_viewer.feature.main.presentation

import alex.zhurkov.github_timber_viewer.common.arch.BaseViewModel
import alex.zhurkov.github_timber_viewer.common.arch.Reducer
import alex.zhurkov.github_timber_viewer.common.arch.StateToModelMapper
import alex.zhurkov.github_timber_viewer.domain.usecase.GitHubContributorsUseCase
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivityViewModel(
    private val gitHubContributorsUseCase: GitHubContributorsUseCase,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    reducer: Reducer<MainActivityState, MainActivityChange>,
    stateToModelMapper: StateToModelMapper<MainActivityState, MainActivityModel>
) : BaseViewModel<MainActivityAction, MainActivityChange, MainActivityState, MainActivityModel>(
    dispatcher = dispatcher, reducer = reducer, stateToModelMapper = stateToModelMapper
) {
    override var state = MainActivityState.EMPTY

    override fun onObserverActive(isFirstTime: Boolean) {
        if (isFirstTime) {
            loadContributorsPage(state.nextPage)
        }
        super.onObserverActive(isFirstTime)
    }

    override suspend fun provideChangesObservable(): Flow<MainActivityChange> {
        return emptyFlow()
    }

    override fun processAction(action: MainActivityAction) {
    }

    private fun loadContributorsPage(pageIndex: Int) {
        viewModelScope.launch(dispatcher) {
            execute(
                action = { gitHubContributorsUseCase.getContributors(pageIndex) },
                onStart = { sendChange(MainActivityChange.PageLoadingChanged(isLoading = true)) },
                onComplete = { sendChange(MainActivityChange.PageLoadingChanged(isLoading = false)) },
                onErrorOccurred = { onPageLoadingError(it) },
                onSuccess = { sendChange(MainActivityChange.PageLoaded(it)) }
            )
        }
    }

    private fun onPageLoadingError(e: Throwable) = Timber.e(e)
}
