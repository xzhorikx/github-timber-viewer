package alex.zhurkov.github_timber_viewer.feature.main.ui

import alex.zhurkov.github_timber_viewer.R
import alex.zhurkov.github_timber_viewer.feature.main.model.GitHubContributorItem
import alex.zhurkov.github_timber_viewer.feature.main.presentation.MainActivityModel
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.LiveData
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.valentinilk.shimmer.*

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    uiModel: LiveData<MainActivityModel>,
    onPullToRefresh: () -> Unit,
    onLastItemVisible: (id: String) -> Unit,
    onClick: (GitHubContributorItem.Data) -> Unit
) {
    val model by uiModel.observeAsState()
    model?.let { renderModel ->
        val pullToRefreshState =
            rememberPullRefreshState(renderModel.isRefreshing, onPullToRefresh)
        val state = rememberLazyListState()
        CompositionLocalProvider(
            LocalOverscrollConfiguration provides null
        ) {
            Box(Modifier.pullRefresh(pullToRefreshState)) {
                LazyColumn(
                    modifier = modifier.testTag("item_container"),
                    state = state,
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_4)),
                    contentPadding = PaddingValues(
                        vertical = dimensionResource(id = R.dimen.padding_8)
                    )
                ) {
                    items(items = renderModel.items, key = { it.id }) { item ->
                        val lastVisibleId by remember {
                            derivedStateOf {
                                with(state.layoutInfo) {
                                    (visibleItemsInfo.lastOrNull()?.key as? String).takeIf { it == (item as? GitHubContributorItem.Data)?.id }
                                }
                            }
                        }
                        lastVisibleId?.run(onLastItemVisible)
                        when (item) {
                            is GitHubContributorItem.Data -> {
                                ContributorPreview(
                                    item,
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = onClick
                                )
                            }
                            is GitHubContributorItem.Loading -> {
                                ContributorLoading(
                                    item = item,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(dimensionResource(id = R.dimen.shimmer_height))
                                )
                            }
                        }
                    }
                }
                PullRefreshIndicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    refreshing = renderModel.isRefreshing,
                    state = pullToRefreshState,
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ContributorPreview(
    item: GitHubContributorItem.Data,
    modifier: Modifier = Modifier,
    onClick: (GitHubContributorItem.Data) -> Unit
) {
    val context = LocalContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .placeholder(R.drawable.ic_loading_animated)
            .error(R.drawable.ic_loading_error)
            .build()
    }
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context).data(data = item.avatar)
            .crossfade(true)
            .build(),
        imageLoader = imageLoader,
        error = ColorPainter(Color.Black),
    )
    Row(
        modifier = modifier
            .clickable(onClick = { onClick(item) })
            .testTag(item.id)
    ) {
        Image(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.avatar_size))
                .clip(RoundedCornerShape(dimensionResource(id = R.dimen.corners_4))),
            painter = painter,
            contentDescription = "Contributor avatar",
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_16)))
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = item.name,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = pluralStringResource(
                    id = R.plurals.contributions_template,
                    item.contributions,
                    item.contributions
                ),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun ContributorLoading(
    modifier: Modifier = Modifier,
    item: GitHubContributorItem.Loading
) {
    val shimmer = rememberShimmer(
        shimmerBounds = ShimmerBounds.Window,
        theme = LocalShimmerTheme.current.copy(
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 600,
                    easing = LinearEasing,
                    delayMillis = 300,
                ),
                repeatMode = RepeatMode.Restart,
            ),
        ),
    )
    Box(
        modifier = modifier
            .shimmer(shimmer)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .testTag(item.id)
    )
}

