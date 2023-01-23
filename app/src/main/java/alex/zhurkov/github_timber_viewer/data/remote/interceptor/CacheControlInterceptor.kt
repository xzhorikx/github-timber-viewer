package alex.zhurkov.github_timber_viewer.data.remote.interceptor

import alex.zhurkov.github_timber_viewer.domain.config.ConfigSource
import okhttp3.Interceptor
import okhttp3.Response

class CacheControlInterceptor(
    private val configSource: ConfigSource
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val modifiedHeader = original
            .newBuilder()
            .header("Cache-Control", "public, max-stale=${configSource.cacheStaleSec}")
            .build()
        return chain.proceed(modifiedHeader)
    }
}