package alex.zhurkov.github_timber_viewer.app.di

import alex.zhurkov.github_timber_viewer.data.usecase.NetworkConnectionUseCaseImpl
import alex.zhurkov.github_timber_viewer.domain.usecase.NetworkConnectionUseCase
import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class NetworkConnectionModule {
    @Provides
    @ActivityScope
    fun networkConnectionUseCase(
        @AppContext context: Context
    ): NetworkConnectionUseCase = NetworkConnectionUseCaseImpl(
        context = context
    )
}
