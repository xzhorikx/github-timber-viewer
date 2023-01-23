package alex.zhurkov.github_timber_viewer.feature.main.presentation

import alex.zhurkov.github_timber_viewer.common.arch.UIEvent

sealed class MainActivityEvent : UIEvent {
    data class DisplayError(val error: Throwable) : MainActivityEvent()
}