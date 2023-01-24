package alex.zhurkov.github_timber_viewer

import alex.zhurkov.github_timber_viewer.common.singleItemList
import alex.zhurkov.github_timber_viewer.feature.main.model.GitHubContributorItem
import alex.zhurkov.github_timber_viewer.feature.main.presentation.MainActivityModel
import alex.zhurkov.github_timber_viewer.feature.main.ui.MainScreen
import alex.zhurkov.github_timber_viewer.ui.theme.GithubtimberviewerTheme
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.lifecycle.MutableLiveData
import junit.framework.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class Test {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private fun buildDataItem(id: String) = GitHubContributorItem.Data(
        id = id,
        name = id,
        contributions = id.hashCode(),
        avatar = id,
        gitHubUrl = id
    )

    @Test
    fun when_ui_model_has_data_items_they_are_displayed() {
        val itemCount = 3
        val loadingItems = (0..itemCount).map {
            buildDataItem(id = "item_$it")
        }
        setMainScreenContent(
            MainActivityModel(
                items = loadingItems,
                isRefreshing = false
            )
        )
        (0..itemCount).forEach {
            rule.onNodeWithTag("item_$it").assertExists()
        }
    }

    @Test
    fun when_ui_model_has_loading_items_they_are_displayed() {
        val itemCount = 3
        val loadingItems = (0..itemCount).map {
            GitHubContributorItem.Loading(id = "loading_$it")
        }
        setMainScreenContent(MainActivityModel(items = loadingItems, isRefreshing = false))

        (0..itemCount).forEach {
            rule.onNodeWithTag("loading_$it").assertExists()
        }
    }

    @Test
    fun when_data_item_is_clicked_then_onClick_is_called() {
        val id = "id"
        var onClickInvocations = 0
        setMainScreenContent(
            model = MainActivityModel(
                items = buildDataItem(id = id).singleItemList(),
                isRefreshing = false
            ),
            onClick = { onClickInvocations++ }
        )
        rule.onNodeWithTag(id).performClick()
        assertEquals(1, onClickInvocations)
    }

    @Test
    fun when_last_item_is_updated_then_onLastItemVisible_is_called() {
        val itemCount = 20
        val loadingItems = (0..itemCount).map {
            buildDataItem(id = "item_$it")
        }
        var lastItemId: String? = null
        setMainScreenContent(
            model = MainActivityModel(
                items = loadingItems,
                isRefreshing = false
            ),
            onLastItemVisible = { lastItemId = it }
        )
        rule.onNodeWithTag("item_container").performTouchInput { swipeUp() }
        assertEquals("item_$itemCount", lastItemId)
    }

    private fun setMainScreenContent(
        model: MainActivityModel,
        onClick: (() -> Unit)? = null,
        onLastItemVisible: ((id: String) -> Unit)? = null
    ) {
        rule.setContent {
            GithubtimberviewerTheme {
                MainScreen(
                    uiModel = MutableLiveData(model),
                    onPullToRefresh = { },
                    onLastItemVisible = { onLastItemVisible?.invoke(it) },
                    onClick = { onClick?.invoke() }
                )
            }
        }
    }
}