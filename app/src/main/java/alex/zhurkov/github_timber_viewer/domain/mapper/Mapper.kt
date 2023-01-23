package alex.zhurkov.github_timber_viewer.domain.mapper

interface Mapper<T, E> {
    fun map(from: T): E
}
