package alex.zhurkov.github_timber_viewer.feature.main.presentation

import alex.zhurkov.github_timber_viewer.common.arch.UIModel
import alex.zhurkov.github_timber_viewer.feature.main.model.GitHubContributorItem

data class MainActivityModel(
    val items: List<GitHubContributorItem>,
    val isRefreshing: Boolean
) : UIModel