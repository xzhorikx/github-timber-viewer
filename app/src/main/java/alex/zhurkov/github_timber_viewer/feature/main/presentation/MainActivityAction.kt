package alex.zhurkov.github_timber_viewer.feature.main.presentation

import alex.zhurkov.github_timber_viewer.common.arch.UIAction

sealed class MainActivityAction : UIAction {
    data class LastVisibleItemChanged(val id: String) : MainActivityAction()
    object Refresh : MainActivityAction()
}
