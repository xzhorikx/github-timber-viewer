package alex.zhurkov.github_timber_viewer.feature.main.presentation

import alex.zhurkov.github_timber_viewer.common.singleItemList
import alex.zhurkov.github_timber_viewer.domain.model.GitHubContributorsPage
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito


class MainActivityReducerTest {

    private val reducer = MainActivityReducer()
    private val initialState = MainActivityState.EMPTY

    @Test
    fun `ItemsCleared test`() {
        val change = MainActivityChange.ItemsCleared
        val expectedState = initialState.copy(pages = emptyList())
        val newState = reducer.reduce(initialState, change)
        assertEquals(expectedState, newState)
    }

    @Test
    fun `PageLoaded test`() {
        val value = Mockito.mock(GitHubContributorsPage::class.java)
        val change = MainActivityChange.PageLoaded(value)
        val expectedState = initialState.copy(pages = value.singleItemList())
        val newState = reducer.reduce(initialState, change)
        assertEquals(expectedState, newState)
    }

    @Test
    fun `PageLoadingChanged test`() {
        val isLoading = true
        val change = MainActivityChange.PageLoadingChanged(isLoading = isLoading)
        val expectedState = initialState.copy(isPageLoading = isLoading)
        val newState = reducer.reduce(initialState, change)
        assertEquals(expectedState, newState)
    }

    @Test
    fun `RefreshChanged test`() {
        val isRefreshing = true
        val change = MainActivityChange.RefreshChanged(isRefreshing = isRefreshing)
        val expectedState = initialState.copy(isRefreshing = isRefreshing)
        val newState = reducer.reduce(initialState, change)
        assertEquals(expectedState, newState)
    }

    @Test
    fun `LastVisibleItemChanged test`() {
        val lastVisibleItemId = "lastVisibleItemId"
        val change = MainActivityChange.LastVisibleItemChanged(id = lastVisibleItemId)
        val expectedState = initialState.copy(lastVisibleItemId = lastVisibleItemId)
        val newState = reducer.reduce(initialState, change)
        assertEquals(expectedState, newState)
    }
}