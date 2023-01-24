@file:OptIn(ExperimentalCoroutinesApi::class)

package alex.zhurkov.github_timber_viewer.data.remote

import alex.zhurkov.github_timber_viewer.common.arch.CoroutinesTestRule
import alex.zhurkov.github_timber_viewer.data.remote.model.GitHubContributorResponse
import alex.zhurkov.github_timber_viewer.domain.config.ConfigSource
import alex.zhurkov.github_timber_viewer.domain.mapper.Mapper
import alex.zhurkov.github_timber_viewer.domain.model.GitHubContributor
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*


class GitHubRepositoryImplTest {

    @get:Rule
    val rule = CoroutinesTestRule()

    private lateinit var repository: GitHubRepositoryImpl

    @Mock
    lateinit var configSource: ConfigSource

    @Mock
    lateinit var remoteSource: GitHubRemoteSource

    @Mock
    lateinit var contributorRemoteMapper: Mapper<GitHubContributorResponse, GitHubContributor>

    lateinit var response: GitHubContributorResponse
    lateinit var gitHubContributor: GitHubContributor

    private val pageIndex = 64
    private val pageSize = 7493
    private val cacheStaleSec = 950007L

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        response = mockk()
        gitHubContributor = mockk()

        remoteSource.stub {
            onBlocking { getContributorsPage(any(), any(), any()) } doReturn listOf(response)
        }
        whenever(contributorRemoteMapper.map(response)).thenReturn(gitHubContributor)
        whenever(configSource.pageSize).thenReturn(pageSize)
        whenever(configSource.cacheStaleSec).thenReturn(cacheStaleSec)

        repository = GitHubRepositoryImpl(
            configSource = configSource,
            remoteSource = remoteSource,
            contributorRemoteMapper = contributorRemoteMapper
        )
    }

    @Test
    fun `when getContributors is called, then page id is the same as passed to the method`() {
        runTest(context = rule.testDispatcher) {
            val result = repository.getContributors(pageIndex, skipCache = true)
            assertEquals(result.pageId, pageIndex)
        }
    }

    @Test
    fun `when getContributors is called, then page with mapped contributors is returned`() {
        runTest(context = rule.testDispatcher) {
            val result = repository.getContributors(pageIndex, skipCache = true)
            assertEquals(result.contributors.first(), gitHubContributor)
        }
    }

    @Test
    fun `when remoteSource_getContributorsPage returns empty list of contributors, then isLastPage is true`() {
        runTest(context = rule.testDispatcher) {
            remoteSource.stub {
                onBlocking { getContributorsPage(any(), any(), any()) } doReturn emptyList()
            }
            val result = repository.getContributors(pageIndex, skipCache = true)
            assertTrue(result.isLastPage)
        }
    }

    @Test
    fun `when skipCache is true, cache control is no-cache`() {
        runTest(context = rule.testDispatcher) {
            repository.getContributors(pageIndex, skipCache = true)
            verify(remoteSource).getContributorsPage(
                limit = pageSize,
                page = pageIndex,
                cacheControl = "no-cache"
            )
        }
    }

    @Test
    fun `when skipCache is true, cache control is public, max-stale=Cache_Stale`() {
        runTest(context = rule.testDispatcher) {
            repository.getContributors(pageIndex, skipCache = false)
            verify(configSource).cacheStaleSec
            verify(remoteSource).getContributorsPage(
                limit = pageSize,
                page = pageIndex,
                cacheControl = "public, max-stale=$cacheStaleSec"
            )
        }
    }
}