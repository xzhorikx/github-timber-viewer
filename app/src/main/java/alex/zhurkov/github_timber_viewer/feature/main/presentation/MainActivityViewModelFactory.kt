package alex.zhurkov.github_timber_viewer.feature.main.presentation

import alex.zhurkov.github_timber_viewer.common.arch.Reducer
import alex.zhurkov.github_timber_viewer.common.arch.StateToModelMapper
import alex.zhurkov.github_timber_viewer.domain.usecase.GitHubContributorsUseCase
import alex.zhurkov.github_timber_viewer.domain.usecase.NetworkConnectionUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivityViewModelFactory(
    private val gitHubContributorsUseCase: GitHubContributorsUseCase,
    private val networkConnectionUseCase: NetworkConnectionUseCase,
    private val reducer: Reducer<MainActivityState, MainActivityChange>,
    private val stateToModelMapper: StateToModelMapper<MainActivityState, MainActivityModel>
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = MainActivityViewModel(
        gitHubContributorsUseCase = gitHubContributorsUseCase,
        networkConnectionUseCase = networkConnectionUseCase,
        reducer = reducer,
        stateToModelMapper = stateToModelMapper
    ) as T
}
