package alex.zhurkov.github_timber_viewer.feature.main.presentation

import alex.zhurkov.github_timber_viewer.common.arch.UIAction

sealed class MainActivityAction : UIAction {
    data class QueryChanged(val data: String) : MainActivityAction()
    data class LastVisibleGitHubChanged(val id: String?) : MainActivityAction()
}
