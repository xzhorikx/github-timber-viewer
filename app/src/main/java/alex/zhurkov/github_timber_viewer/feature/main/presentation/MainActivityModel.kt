package alex.zhurkov.github_timber_viewer.feature.main.presentation

import alex.zhurkov.github_timber_viewer.common.arch.UIModel
import alex.zhurkov.github_timber_viewer.feature.main.model.GitHubContributerItem

data class MainActivityModel(
    val items: List<GitHubContributerItem>
) : UIModel