package alex.zhurkov.github_timber_viewer.feature.main.ui

import alex.zhurkov.github_timber_viewer.feature.main.presentation.MainActivityModel
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.LiveData

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    uiModel: LiveData<MainActivityModel>,
) {
    val model by uiModel.observeAsState()
    model?.let { renderModel ->
        Column(modifier = modifier) {}
    }
}