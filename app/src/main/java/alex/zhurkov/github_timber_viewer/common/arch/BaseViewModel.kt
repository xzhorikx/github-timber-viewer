package alex.zhurkov.github_timber_viewer.common.arch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseViewModel<A : UIAction, C : UIStateChange, S : UIState, M : UIModel>(
    protected val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val reducer: Reducer<S, C>,
    private val stateToModelMapper: StateToModelMapper<S, M>
) : ViewModel() {

    protected abstract var state: S
    private val changes = MutableSharedFlow<C>(
        replay = 5,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    private val actions = Channel<A>(Channel.RENDEZVOUS)
    private val modelLiveData = object : MutableLiveData<M>() {
        private var isObserverAlreadyAttached = false

        override fun onActive() {
            val isAttachedFirstTime = !isObserverAlreadyAttached
            if (!isObserverAlreadyAttached) {
                isObserverAlreadyAttached = true
                bindChanges()
                onObserverActive(isAttachedFirstTime)
            }
        }

        override fun onInactive() {
            onObserverInactive()
        }
    }
    private val event = SingleLiveEvent<UIEvent>()
    protected abstract suspend fun provideChangesObservable(): Flow<C>
    protected open fun onObserverActive(isFirstTime: Boolean) {}
    protected open fun onObserverInactive() {}
    protected open fun onStateUpdated(oldState: S, newState: S) {}
    protected abstract fun processAction(action: A)
    val observableEvents: LiveData<UIEvent> = event
    val observableModel: LiveData<M> = modelLiveData

    init {
        viewModelScope.launch(dispatcher) {
            actions.consumeEach {
                viewModelScope.launch {
                    kotlin.runCatching { processAction(it) }
                }
            }
        }
    }

    fun dispatch(action: A) {
        Timber.e("Action received: [$action]")
        viewModelScope.launch(dispatcher) { actions.send(action) }
    }

    protected fun sendChange(change: C) {
        viewModelScope.launch(dispatcher) { changes.emit(change) }
    }

    fun sendEvent(ev: UIEvent) {
        viewModelScope.launch { event.value = ev }
    }

    private fun bindChanges() {
        viewModelScope.launch(dispatcher) {
            merge(
                provideChangesObservable().catch { e -> Timber.e(e) },
                changes
            )
                .distinctUntilChanged()
                .flatMapConcat { change ->
                    Timber.e("Change received: [$change]")
                    flow {
                        val oldState = state
                        emit(reducer.reduce(state, change).also {
                            this@BaseViewModel.state = it
                            onStateUpdated(oldState = oldState, newState = it)
                        })
                    }
                }
                .distinctUntilChanged()
                .onStart { emit(state) }
                .map { stateToModelMapper.mapStateToModel(it) }
                .distinctUntilChanged()
                .catch { e -> Timber.e(e) }
                .collectLatest {
                    Timber.e("New model: [$it]")
                    viewModelScope.launch { modelLiveData.value = it }
                }
        }
    }

    protected suspend inline fun <T> execute(
        crossinline action: suspend () -> T,
        noinline onStart: (suspend () -> Unit)? = null,
        crossinline onSuccess: suspend (T) -> Unit,
        noinline onComplete: (suspend () -> Unit)? = null,
        noinline onErrorOccurred: ((Throwable) -> Unit)? = null
    ) {
        onStart?.invoke()
        try {
            val result = action()
            onSuccess(result)
        } catch (e: Exception) {
            onErrorOccurred?.invoke(e) ?: Timber.e(e)
        }
        onComplete?.invoke()
    }
}
