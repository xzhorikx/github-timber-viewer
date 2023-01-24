package alex.zhurkov.github_timber_viewer.common.arch

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*


@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelTest {

    @get:Rule
    val rule = CoroutinesTestRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var baseViewModel: TestViewModel
    private val dispatcher: CoroutineDispatcher = rule.testDispatcher

    @Mock
    lateinit var actionMock: UIAction

    @Mock
    lateinit var changeMock: UIStateChange

    @Mock
    lateinit var stateMock: UIState

    @Mock
    lateinit var changedState: UIState

    @Mock
    lateinit var modelMock: UIModel

    @Mock
    lateinit var uiEvent: UIEvent

    @Mock
    lateinit var actionResolver: (UIAction) -> Unit

    @Mock
    lateinit var stateUpdateResolver: (oldState: UIState, newState: UIState) -> Unit

    @Mock
    lateinit var observerActiveResolver: (isFirstTime: Boolean) -> Unit

    @Mock
    lateinit var reducer: Reducer<UIState, UIStateChange>

    @Mock
    lateinit var stateToModelMapper: StateToModelMapper<UIState, UIModel>

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        whenever(reducer.reduce(stateMock, changeMock)).thenReturn(changedState)
        whenever(stateToModelMapper.mapStateToModel(stateMock)).thenReturn(modelMock)

        baseViewModel = TestViewModel(
            initialState = stateMock,
            changeFlow = flowOf(changeMock),
            actionResolver = actionResolver,
            stateUpdateResolver = stateUpdateResolver,
            observerActiveResolver = observerActiveResolver,
            dispatcher = dispatcher,
            reducer = reducer,
            stateToModelMapper = stateToModelMapper
        )
    }

    @Test
    fun `when ViewModel is initialized, then actions can be consumed`() {
        runTest(context = dispatcher) {
            baseViewModel.dispatch(actionMock)
            verify(actionResolver).invoke(actionMock)
        }
    }

    @Test
    fun `when ViewModel is initialized, then events are received`() {
        runTest(context = dispatcher) {
            baseViewModel.sendEvent(uiEvent)

            baseViewModel.observableEvents.observeForever {
                assertEquals(it, uiEvent)
            }
        }
    }

    @Test
    fun `when ViewModel_observableModel is observed, then bindChanges is invoked`() {
        runTest(context = dispatcher) {
            baseViewModel.observableModel.observeForever {
                verify(reducer).reduce(stateMock, changeMock)
                verify(stateUpdateResolver).invoke(stateMock, changedState)
                verify(stateToModelMapper).mapStateToModel(changedState)
                assertEquals(baseViewModel.currentState, changedState)
            }
        }
    }

    @Test
    fun `when ViewModel_sendChange is called, then changes are reduced to a new state`() {
        runTest(context = dispatcher) {
            baseViewModel.observableModel.observeForever {
                val change: UIStateChange = mock()
                baseViewModel.testSendChange(change)
                verify(reducer).reduce(any(), eq(change))
            }
        }
    }

    @Test
    fun `when state is updated, then onStateUpdated is called`() {
        runTest(context = dispatcher) {
            baseViewModel.observableModel.observeForever {
                val change: UIStateChange = mock()
                baseViewModel.testSendChange(change)
                verify(stateUpdateResolver).invoke(stateMock, changedState)
            }
        }
    }

    @Test
    fun `when change is applied, then ui model is updated`() {
        runTest(context = dispatcher) {
            baseViewModel.observableModel.observeForever {
                val change: UIStateChange = mock()
                baseViewModel.testSendChange(change)
                assertEquals(it, modelMock)
            }
        }
    }

    @Test
    fun `given observableModel observer for the first time, when onObserverActive is invoked, isFirstTime is true`() {
        runTest(context = dispatcher) {
            val firstObserver = Observer<UIModel> {
                verify(observerActiveResolver).invoke(true)
            }
            val secondObserver = Observer<UIModel> {
                verify(observerActiveResolver).invoke(false)
            }
            baseViewModel.observableModel.observeForever(firstObserver)
            baseViewModel.observableModel.observeForever(secondObserver)
        }
    }

    private class TestViewModel(
        initialState: UIState,
        private val changeFlow: Flow<UIStateChange>,
        private val actionResolver: (UIAction) -> Unit,
        private val stateUpdateResolver: (oldState: UIState, newState: UIState) -> Unit,
        private val observerActiveResolver: (isFirstTime: Boolean) -> Unit,
        dispatcher: CoroutineDispatcher,
        reducer: Reducer<UIState, UIStateChange>,
        stateToModelMapper: StateToModelMapper<UIState, UIModel>
    ) : BaseViewModel<UIAction, UIStateChange, UIState, UIModel>(
        dispatcher = dispatcher,
        reducer = reducer,
        stateToModelMapper = stateToModelMapper
    ) {
        override var state: UIState = initialState
        val currentState = state
        override suspend fun provideChangesObservable(): Flow<UIStateChange> = changeFlow

        override fun processAction(action: UIAction) = actionResolver(action)

        override fun onStateUpdated(oldState: UIState, newState: UIState) {
            super.onStateUpdated(oldState, newState)
            stateUpdateResolver(oldState, newState)
        }

        override fun onObserverActive(isFirstTime: Boolean) {
            super.onObserverActive(isFirstTime)
            observerActiveResolver(isFirstTime)
        }

        fun testSendChange(change: UIStateChange) = sendChange(change)
    }
}