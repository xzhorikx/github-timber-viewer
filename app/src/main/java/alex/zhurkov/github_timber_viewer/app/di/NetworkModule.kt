package alex.zhurkov.github_timber_viewer.app.di

import alex.zhurkov.github_timber_viewer.data.remote.GitHubRemoteSource
import alex.zhurkov.github_timber_viewer.data.remote.interceptor.CacheControlInterceptor
import alex.zhurkov.github_timber_viewer.domain.config.ConfigSource
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Reusable
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Reusable
    fun clientBuilder(
        @AppContext context: Context,
        configSource: ConfigSource
    ): OkHttpClient.Builder =
        OkHttpClient.Builder()
            .cache(Cache(context.cacheDir, configSource.cacheSizeByte))
            .callTimeout(configSource.callTimeOutSec, TimeUnit.SECONDS)
            .connectTimeout(configSource.callTimeOutSec, TimeUnit.SECONDS)
            .readTimeout(configSource.callTimeOutSec, TimeUnit.SECONDS)
            .writeTimeout(configSource.callTimeOutSec, TimeUnit.SECONDS)

    @Provides
    @Singleton
    @LoggingInterceptor
    fun loggingInterceptor(): Interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    @CacheInterceptor
    fun cacheInterceptor(configSource: ConfigSource): Interceptor =
        CacheControlInterceptor(configSource)

    @Provides
    @Singleton
    @GitHubHttpClient
    fun gitHubHttpClient(
        builder: OkHttpClient.Builder,
        @LoggingInterceptor loggingInterceptor: Interceptor,
        @CacheInterceptor cacheInterceptor: Interceptor,
    ): OkHttpClient = builder
        .addInterceptor(cacheInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    @Provides
    @Singleton
    @ImageLoaderHttpClient
    fun imageLoaderHttpClient(
        builder: OkHttpClient.Builder,
        @LoggingInterceptor loggingInterceptor: Interceptor,
        @CacheInterceptor cacheInterceptor: Interceptor
    ): OkHttpClient = builder
        .addInterceptor(cacheInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    @Provides
    @Singleton
    fun gitHubNetworkClient(
        @GitHubHttpClient httpClient: OkHttpClient
    ): GitHubRemoteSource = Retrofit.Builder()
        .baseUrl("https://api.github.com")
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient)
        .build()
        .create(GitHubRemoteSource::class.java)
}
