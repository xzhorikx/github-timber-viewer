package alex.zhurkov.github_timber_viewer.feature.main.di

import alex.zhurkov.github_timber_viewer.app.di.ActivityScope
import alex.zhurkov.github_timber_viewer.common.arch.Reducer
import alex.zhurkov.github_timber_viewer.common.arch.StateToModelMapper
import alex.zhurkov.github_timber_viewer.domain.usecase.GitHubContributorsUseCase
import alex.zhurkov.github_timber_viewer.domain.usecase.NetworkConnectionUseCase
import alex.zhurkov.github_timber_viewer.feature.main.presentation.*
import dagger.Module
import dagger.Provides

@Module
class MainActivityPresentationModule {

    @Provides
    @ActivityScope
    fun reducer(): Reducer<MainActivityState, MainActivityChange> = MainActivityReducer()

    @Provides
    @ActivityScope
    fun stateToModelMapper(): StateToModelMapper<MainActivityState, MainActivityModel> =
        MainStateToModelMapper()

    @Provides
    @ActivityScope
    fun viewModelFactory(
        gitHubContributorsUseCase: GitHubContributorsUseCase,
        networkConnectionUseCase: NetworkConnectionUseCase,
        reducer: Reducer<MainActivityState, MainActivityChange>,
        stateToModelMapper: StateToModelMapper<MainActivityState, MainActivityModel>
    ) = MainActivityViewModelFactory(
        gitHubContributorsUseCase = gitHubContributorsUseCase,
        networkConnectionUseCase = networkConnectionUseCase,
        reducer = reducer,
        stateToModelMapper = stateToModelMapper
    )
}
