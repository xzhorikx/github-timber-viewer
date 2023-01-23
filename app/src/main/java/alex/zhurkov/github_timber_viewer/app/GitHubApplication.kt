package alex.zhurkov.github_timber_viewer.app

import alex.zhurkov.github_timber_viewer.app.di.DaggerAppComponent
import alex.zhurkov.github_timber_viewer.feature.main.MainActivity
import alex.zhurkov.github_timber_viewer.feature.main.di.MainActivityComponent
import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import timber.log.Timber
import javax.inject.Inject

class GitHubApplication : Application(), MainActivityComponent.ComponentProvider, ImageLoaderFactory {

    private val component by lazy { DaggerAppComponent.factory().create(this) }

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreate() {
        component.inject(this)
        Timber.plant(Timber.DebugTree())
        super.onCreate()
    }

    override fun provideMainComponent(activity: MainActivity): MainActivityComponent =
        component.plusMainActivityComponent().create(activity)

    override fun newImageLoader(): ImageLoader = imageLoader
}