package alex.zhurkov.github_timber_viewer.feature.main.di

import alex.zhurkov.github_timber_viewer.app.di.ActivityScope
import alex.zhurkov.github_timber_viewer.domain.di.GitHubDomainModule
import alex.zhurkov.github_timber_viewer.domain.di.NetworkConnectionModule
import alex.zhurkov.github_timber_viewer.feature.main.MainActivity
import dagger.BindsInstance
import dagger.Subcomponent

@ActivityScope
@Subcomponent(
    modules = [
        GitHubDomainModule::class,
        NetworkConnectionModule::class,
        MainActivityPresentationModule::class
    ]
)
interface MainActivityComponent {

    fun inject(target: MainActivity)

    interface ComponentProvider {
        fun provideMainComponent(activity: MainActivity): MainActivityComponent
    }

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance activity: MainActivity): MainActivityComponent
    }
}