package alex.zhurkov.github_timber_viewer.app.di

import alex.zhurkov.github_timber_viewer.app.GitHubApplication
import alex.zhurkov.github_timber_viewer.feature.main.di.MainActivityComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        NetworkModule::class,
        GitHubDataModule::class,
        ImageLoaderModule::class
    ]
)
interface AppComponent {
    fun inject(target: GitHubApplication)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance app: GitHubApplication): AppComponent
    }

    fun plusMainActivityComponent(): MainActivityComponent.Factory
}