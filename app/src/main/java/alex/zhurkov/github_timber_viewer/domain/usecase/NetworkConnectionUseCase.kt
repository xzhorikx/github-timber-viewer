package alex.zhurkov.github_timber_viewer.domain.usecase

import kotlinx.coroutines.flow.Flow

interface NetworkConnectionUseCase {
    /**
     * Emits a boolean representing the current state of network connection
     */
    fun observeConnectionState(): Flow<Boolean>
}
