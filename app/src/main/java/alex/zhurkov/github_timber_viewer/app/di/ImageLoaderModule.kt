package alex.zhurkov.github_timber_viewer.app.di

import android.content.Context
import coil.ImageLoader
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
class ImageLoaderModule {

    @Provides
    @Singleton
    fun imageLoader(
        @AppContext context: Context,
        @ImageLoaderHttpClient client: OkHttpClient
    ): ImageLoader = ImageLoader.Builder(context)
        .okHttpClient(client)
        .build()
}
