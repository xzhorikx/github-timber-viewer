package alex.zhurkov.github_timber_viewer.domain.config

interface ConfigSource {
    val pageSize: Int
    val callTimeOutSec: Long
    val cacheStaleSec: Long
    val cacheSizeByte: Long
}
