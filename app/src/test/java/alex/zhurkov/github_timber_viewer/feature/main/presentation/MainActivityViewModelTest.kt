package alex.zhurkov.github_timber_viewer.feature.main.presentation

import alex.zhurkov.github_timber_viewer.common.arch.CoroutinesTestRule
import alex.zhurkov.github_timber_viewer.common.arch.Reducer
import alex.zhurkov.github_timber_viewer.common.arch.StateToModelMapper
import alex.zhurkov.github_timber_viewer.domain.usecase.GitHubContributorsUseCase
import alex.zhurkov.github_timber_viewer.domain.usecase.NetworkConnectionUseCase
import alex.zhurkov.github_timber_viewer.utils.callPrivateFunc
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.internal.verification.NoInteractions
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible


@OptIn(ExperimentalCoroutinesApi::class)
class MainActivityViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val rule = CoroutinesTestRule()

    @Mock
    lateinit var gitHubContributorsUseCase: GitHubContributorsUseCase

    @Mock
    lateinit var networkConnectionUseCase: NetworkConnectionUseCase

    @Mock
    lateinit var reducer: Reducer<MainActivityState, MainActivityChange>

    @Mock
    lateinit var stateToModelMapper: StateToModelMapper<MainActivityState, MainActivityModel>

    private lateinit var observer: Observer<in MainActivityModel>

    private lateinit var viewModel: MainActivityViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        observer = mock<Observer<MainActivityModel>>()
        viewModel = MainActivityViewModel(
            dispatcher = rule.testDispatcher,
            gitHubContributorsUseCase = gitHubContributorsUseCase,
            networkConnectionUseCase = networkConnectionUseCase,
            reducer = reducer,
            stateToModelMapper = stateToModelMapper
        )
    }

    @Test
    fun `given observer is attached, when onObserverActive is invoked for the first time, then first page is loaded`() {
        viewModel.observableModel.observeForever(observer)
        runTest {
            verify(gitHubContributorsUseCase).getContributors(page = 1, skipCache = false)
        }
    }

    @Test
    fun `given last page is loaded, when loadContributorsPage is invoked without force refresh, then page is not loaded`() {
        val state: MainActivityState = mockk()
        every { state.nextPage } returns 123
        every { state.isLastPageLoaded } returns true

        viewModel.updateState(state)
        viewModel.observableModel.observeForever(observer)
        runTest {
            verify(gitHubContributorsUseCase, NoInteractions()).getContributors(any(), any())
        }
    }

    @Test
    fun `given page is loading, when loadContributorsPage is invoked without force refresh, then page is not loaded`() {
        val state: MainActivityState = mockk()
        every { state.nextPage } returns 123
        every { state.isLastPageLoaded } returns false
        every { state.isPageLoading } returns true

        viewModel.updateState(state)
        viewModel.observableModel.observeForever(observer)
        runTest {
            verify(gitHubContributorsUseCase, NoInteractions()).getContributors(any(), any())
        }
    }

    @Test
    fun `given page is loaded, when loadContributorsPage is invoked without force refresh, then page is not loaded`() {
        val state: MainActivityState = mockk()
        val pageId = 123
        every { state.nextPage } returns pageId
        every { state.isLastPageLoaded } returns false
        every { state.isPageLoading } returns false
        every { state.isPageLoaded(pageId) } returns true

        viewModel.updateState(state)
        viewModel.observableModel.observeForever(observer)
        runTest {
            verify(gitHubContributorsUseCase, NoInteractions()).getContributors(any(), any())
        }
    }

    @Test
    fun `when loadContributorsPage is invoked with force refresh, then page is loaded`() {
        val state: MainActivityState = mockk()
        val pageId = 123
        val skipCache = true
        every { state.nextPage } returns pageId
        every { state.isLastPageLoaded } returns true
        every { state.isPageLoading } returns true
        every { state.isPageLoaded(pageId) } returns true

        viewModel.updateState(state)
        runTest {
            viewModel.callPrivateFunc("loadContributorsPage", pageId, skipCache)
            verify(gitHubContributorsUseCase).getContributors(page = pageId, skipCache = skipCache)
        }
    }

    private fun MainActivityViewModel.updateState(state: MainActivityState) =
        this::class.java.kotlin.memberProperties.first { it.name == "state" }.run {
            isAccessible = true
            (this as KMutableProperty<*>).setter.call(viewModel, state)
        }

}