package alex.zhurkov.github_timber_viewer.feature.main.presentation

import alex.zhurkov.github_timber_viewer.common.singleItemList
import alex.zhurkov.github_timber_viewer.domain.model.GitHubContributor
import alex.zhurkov.github_timber_viewer.domain.model.GitHubContributorsPage
import alex.zhurkov.github_timber_viewer.feature.main.model.GitHubContributorItem
import io.mockk.every
import io.mockk.mockk
import junit.framework.Assert.assertTrue
import org.junit.Test


class MainStateToModelMapperTest {
    private val mapper = MainStateToModelMapper()
    private val contributor = GitHubContributor(
        id = "id",
        login = "login",
        avatar = "avatar",
        contributions = 15,
        gitHubUrl = "url"
    )

    @Test
    fun `when state_isRefreshing is true, then model_isRefreshing is true`() {
        val state = MainActivityState.EMPTY.copy(isRefreshing = true)
        mapper.mapStateToModel(state).run {
            assertTrue(this.isRefreshing)
        }
    }

    @Test
    fun `given contributors list is empty, when page is loading, items contain only loading indicators`() {
        val state = MainActivityState.EMPTY.copy(
            isPageLoading = true
        )
        mapper.mapStateToModel(state).run {
            assertTrue(items.all { it is GitHubContributorItem.Loading })
        }
    }

    @Test
    fun `given contributors list is not empty, when page is loading, items contain data and loading indicators`() {
        val pages = mockk<GitHubContributorsPage>()
        every { pages.isLastPage } returns true
        every { pages.contributors } returns contributor.singleItemList()
        val state = MainActivityState.EMPTY.copy(
            isPageLoading = true,
            pages = pages.singleItemList()
        )
        mapper.mapStateToModel(state).run {
            assertTrue(items.first() is GitHubContributorItem.Data)
            assertTrue(
                items.subList(1, items.lastIndex).all { it is GitHubContributorItem.Loading }
            )
        }
    }

    @Test
    fun `given contributors list is not empty, when page is loading, items contain only data items`() {
        val pages = mockk<GitHubContributorsPage>()
        every { pages.isLastPage } returns true
        every { pages.contributors } returns contributor.singleItemList()
        val state = MainActivityState.EMPTY.copy(
            isPageLoading = false,
            pages = pages.singleItemList()
        )
        mapper.mapStateToModel(state).run {
            assertTrue(items.all { it is GitHubContributorItem.Data })
        }
    }
}
