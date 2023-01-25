@file:OptIn(ExperimentalMaterial3Api::class)

package alex.zhurkov.github_timber_viewer.feature.main

import alex.zhurkov.github_timber_viewer.R
import alex.zhurkov.github_timber_viewer.common.arch.UIEvent
import alex.zhurkov.github_timber_viewer.feature.main.di.MainActivityComponent
import alex.zhurkov.github_timber_viewer.feature.main.presentation.MainActivityAction
import alex.zhurkov.github_timber_viewer.feature.main.presentation.MainActivityEvent
import alex.zhurkov.github_timber_viewer.feature.main.presentation.MainActivityViewModel
import alex.zhurkov.github_timber_viewer.feature.main.presentation.MainActivityViewModelFactory
import alex.zhurkov.github_timber_viewer.feature.main.ui.MainScreen
import alex.zhurkov.github_timber_viewer.ui.theme.GithubtimberviewerTheme
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Observer
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    private val component: MainActivityComponent by lazy {
        (application as MainActivityComponent.ComponentProvider).provideMainComponent(this)
    }

    @Inject
    lateinit var viewModelFactory: MainActivityViewModelFactory
    private val viewModel by viewModels<MainActivityViewModel> { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
        viewModel.observableEvents.observe(this, Observer(::renderEvent))
        setContent {
            val context = LocalContext.current
            val scrollBehavior =
                TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
            GithubtimberviewerTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        TopAppBar(
                            title = { Text(stringResource(id = R.string.app_bar_title)) },
                            scrollBehavior = scrollBehavior
                        )
                    }
                ) { paddingValues ->
                    MainScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                PaddingValues(
                                    start = dimensionResource(id = R.dimen.padding_16),
                                    end = dimensionResource(id = R.dimen.padding_16),
                                    top = paddingValues.calculateTopPadding(),
                                    bottom = paddingValues.calculateBottomPadding()
                                )
                            ),
                        uiModel = viewModel.observableModel,
                        onPullToRefresh = { viewModel.dispatch(MainActivityAction.Refresh) },
                        onLastItemVisible = {
                            viewModel.dispatch(MainActivityAction.LastVisibleItemChanged(id = it))
                        },
                        onClick = {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse(it.gitHubUrl))
                            )
                        }
                    )
                }
            }
        }
    }

    private fun renderEvent(event: UIEvent) {
        if (event is MainActivityEvent) {
            when (event) {
                is MainActivityEvent.DisplayError -> {
                    Toast.makeText(
                        this,
                        getString(R.string.error_message_template, event.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is MainActivityEvent.NetworkConnectionChanged -> {
                    @StringRes val textRes = when (event.isConnected) {
                        true -> R.string.network_restored
                        false -> R.string.error_network_disconnected
                    }
                    Toast.makeText(this, getString(textRes), Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}