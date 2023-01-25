package alex.zhurkov.github_timber_viewer.app.di

import alex.zhurkov.github_timber_viewer.app.GitHubApplication
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    @AppContext
    fun appContext(app: GitHubApplication): Context = app.applicationContext
}
