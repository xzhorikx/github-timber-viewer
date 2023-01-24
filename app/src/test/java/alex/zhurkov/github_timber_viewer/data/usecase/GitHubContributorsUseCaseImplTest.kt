package alex.zhurkov.github_timber_viewer.data.usecase

import alex.zhurkov.github_timber_viewer.common.arch.CoroutinesTestRule
import alex.zhurkov.github_timber_viewer.domain.repository.GitHubRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify


@OptIn(ExperimentalCoroutinesApi::class)
class GitHubContributorsUseCaseImplTest {

    @get:Rule
    val rule = CoroutinesTestRule()

    private lateinit var useCase: GitHubContributorsUseCaseImpl

    @Mock
    private lateinit var gitHubRepository: GitHubRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = GitHubContributorsUseCaseImpl(
            gitHubRepository = gitHubRepository
        )
    }

    @Test
    fun `when getContributors is called, gitHubRepository_getContributors is invoked`() {
        runTest {
            val page = 123
            val skipCache = true
            useCase.getContributors(page = page, skipCache = skipCache)
            verify(gitHubRepository).getContributors(page, skipCache)
        }
    }
}