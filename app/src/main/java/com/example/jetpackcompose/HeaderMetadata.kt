@file:Suppress("TooManyFunctions")
 
package com.example.jetpackcompose
 
import android.content.Intent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.sxmp.locale.StringContent
import com.sxmp.uitoolkit.R
import com.sxmp.uitoolkit.icon.Icon
import com.sxmp.uitoolkit.model.ImageState
import com.sxmp.uitoolkit.primitive.BadgeType
import com.sxmp.uitoolkit.primitive.BadgeUiState
import com.sxmp.uitoolkit.primitive.OverlaySmallBadge
import com.sxmp.uitoolkit.primitive.button.PrimaryXLargeIconButton
import com.sxmp.uitoolkit.primitive.button.play.PlayButtonXLarge
import com.sxmp.uitoolkit.theme.UiTheme.colors
import com.sxmp.uitoolkit.theme.UiTheme.spacing
import com.sxmp.uitoolkit.theme.UiTheme.typography
import com.sxmp.uitoolkit.theme.contrast
import com.sxmp.uitoolkit.theme.headlineLarge
import com.sxmp.uitoolkit.theme.headlineMedium
import com.sxmp.uitoolkit.theme.headlineSmall
import com.sxmp.uitoolkit.theme.titleLarge
import com.sxmp.uitoolkit.util.PreviewTabletComponent
import com.sxmp.uitoolkit.util.PreviewTvComponent
import com.sxmp.uitoolkit.util.rememberLambda
import com.sxmp.uitoolkit.util.ui.WindowSizeClass
import com.sxmp.uitoolkit.util.ui.isMediumWindow
import com.sxmp.uitoolkit.util.ui.isSmallWindow
import com.sxmp.uitoolkit.util.ui.isTvWindow
import com.sxmp.uitoolkit.util.ui.localStableConfiguration
import sxmp.core.navigation.NavigationTarget
import sxmp.core.navigation.StartActivityEffect
import sxmp.core.navigation.createDialIntent
import sxmp.core.reminders.ui.rememberIsReminderEnabled
import sxmp.feature.content.action.localActionHandler
import sxmp.feature.content.config.Layout
import sxmp.feature.content.page.ui.entity.ENTITY_HEADER_SCORES_COMPONENT_KEY
import sxmp.feature.content.page.ui.entity.EntityHeaderDescription
import sxmp.feature.content.page.ui.entity.EntityHeaderEpisodesDescription
import sxmp.feature.content.page.ui.entity.HeaderType
import sxmp.feature.content.page.ui.entity.PageHeaderState
import sxmp.feature.content.page.ui.entity.components.score.ScoresComponent
import sxmp.feature.content.page.ui.entity.components.score.ScoresUiState
import sxmp.feature.content.page.ui.entity.pageHeaderStateForPreview
import sxmp.feature.content.page.ui.playback.isPlayingOrBuffering
import sxmp.feature.content.page.ui.playback.rememberPlayButtonState
import sxmp.feature.content.ui.component.button.EntityPlayButtonState
import sxmp.feature.content.ui.component.button.EpisodePlayButton
import sxmp.feature.content.ui.component.focus.LocalContentEnterFocusHelperState
import sxmp.feature.content.ui.component.focus.focusableChildOptional
import sxmp.feature.content.ui.model.EntityAction
import sxmp.feature.content.ui.model.EntityType
import sxmp.locale.LocalizedText
 
@Preview
@PreviewTvComponent
@PreviewTabletComponent
@Composable
fun HeaderMetadata(
    @PreviewParameter(PageHeaderStatePreviewProvider::class) state: PageHeaderState,
    modifier: Modifier = Modifier,
) {
    when (localStableConfiguration().windowSizeClass) {
        WindowSizeClass.Medium -> HeaderMetadataInternal(state = state, modifier = modifier, extraContent = null)
        WindowSizeClass.Small -> HeaderMetadataInternal(state = state, modifier = modifier) {
            if (state.headerAiringInfo != null) {
                Spacer(modifier = Modifier.height(spacing.spacing8))
                EntityHeaderAiringInfoLayout(state.headerAiringInfo)
            }
        }


        WindowSizeClass.TV -> HeaderMetadataInternal(state = state, modifier = modifier) {
            val verticalPadding = if (state.headerAiringInfo != null) spacing.spacing16 else spacing.spacing20
            Spacer(modifier = Modifier.height(verticalPadding))
            TvHeaderActionRow(state = state)
        }
    }
}
 
@Composable
private fun HeaderMetadataInternal(
    state: PageHeaderState,
    modifier: Modifier = Modifier,
    extraContent: @Composable (() -> Unit)? = null,
) {
    val titleStyle = metadataStyling(state.identifier?.entityType)
    val (alignment, justification) = state.metadataAlignment(state.identifier?.entityType)
    if (state.shouldDisplayMetadata()) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = spacedBy(spacing.spacing4.div(2)),
            horizontalAlignment = alignment
        ) {
            state.toBadge()?.let { badgeState ->
                OverlaySmallBadge(
                    type = badgeState.type,
                    text = badgeState.text,
                    lottieSpec = LottieCompositionSpec.RawRes(R.raw.on_air).takeIf { badgeState.type == BadgeType.Live }
                )
            }


            LocalizedText(
                text = state.headerMetaData.title,
                style = titleStyle,
                color = colors.contrast,
                justification = justification,
            )
 
            HeaderSubtitle(
                state = state,
                justification = justification
            )
 
            if (extraContent != null) {
                extraContent()
            }
        }
    }
}
 
internal fun PageHeaderState.toBadge(): BadgeUiState? =
    when (identifier?.entityType) {
        EntityType.EPISODE_PODCAST,
        EntityType.EPISODE_LINEAR,
        EntityType.EPISODE_AOD -> if (!headerEpisodeUiState.isLive) {
            headerEyebrow?.badge
        } else {
            null
        }
 
        EntityType.EVENT -> headerEyebrow?.badge
 
        else -> null
    }
 
/**
* Brand on small screens don't have metadata if the foreground image is displayed.
*/
@Suppress("UseIfInsteadOfWhen")
@Composable
fun PageHeaderState.shouldDisplayMetadata(): Boolean {
    val isSmall = localStableConfiguration().isSmallWindow
    return if (isSmall) {
        when (identifier?.entityType) {
            EntityType.BRAND -> !headerArtStateV2.hasValidBackground()
 
            else -> true
        }
    } else {
        true
    }
}
 
@Composable
internal fun Description(
    pageHeaderState: PageHeaderState,
    modifier: Modifier = Modifier,
) {
    val verticalSpacing = if (isTvWindow()) spacing.spacing56.div(2) else spacing.spacing16
    Column(
        modifier = modifier,
        verticalArrangement = spacedBy(verticalSpacing)
    ) {
        val scoresUiState = pageHeaderState.toHeaderScoreUiState(isTvWindow())
        if (scoresUiState != null) {
            ScoresComponent(
                state = scoresUiState,
                modifier = Modifier
                    .focusableChildOptional(
                        LocalContentEnterFocusHelperState.current,
                        ENTITY_HEADER_SCORES_COMPONENT_KEY
                    )
                    // Forces it to be focusable allowing it to follow scroll on TV
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    )
            )
        }
        pageHeaderState.headerMetaData.description
            ?.takeIf { it != StringContent.Empty }
            ?.let { description ->
                if (pageHeaderState.headerType == HeaderType.EPISODES && !isTvWindow()) {
                    EntityHeaderEpisodesDescription(description = description)
                } else {
                    EntityHeaderDescription(
                        text = description,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = pageHeaderState.toDescriptionMaxLines(isTvWindow()),
                        identifier = pageHeaderState.identifier,
                        shouldCenterWhenNotOverflow = pageHeaderState.shouldCenterIfNotOverflow()
                    )
                }
            }
    }
}
 
private fun PageHeaderState.toHeaderScoreUiState(isTv: Boolean): ScoresUiState? =
    when {
        isTv && headerScoreUiState != null -> headerScoreUiState
        identifier?.entityType == EntityType.EPISODE_LINEAR && headerScoreUiState != null -> headerScoreUiState
        else -> null
    }
 
private fun PageHeaderState.toDescriptionMaxLines(isTvWindow: Boolean): Int =
    if (isTvWindow) {
        DEFAULT_MAX_LINES
    } else {
        when (identifier?.entityType) {
            EntityType.EPISODE_VOD,
            EntityType.EPISODE_LINEAR,
            EntityType.EPISODE_AOD,
            EntityType.EPISODE_PODCAST -> Int.MAX_VALUE
 
            else -> DEFAULT_MAX_LINES
        }
    }
 
@Composable
private fun PageHeaderState.shouldCenterIfNotOverflow(): Boolean =
    when (identifier?.entityType) {
        // For these entities, the metadata is also centered in the fallback without images
        EntityType.CHANNEL_LINEAR,
        EntityType.CHANNEL_XTRA,
        EntityType.ARTIST_STATION -> localStableConfiguration().isSmallWindow


        EntityType.TALENT,
        EntityType.GENRE,
        EntityType.CURATED_GROUPING,
        EntityType.BRAND,
        EntityType.SHOW,
        EntityType.PODCAST -> localStableConfiguration().isSmallWindow && headerArtStateV2.hasValidBackground()
 
        else -> false
    }
 
@Composable
private fun ChannelPlayButton(pageHeaderState: PageHeaderState) {
    val playAction = pageHeaderState.headerActions.playAction ?: return
    if (pageHeaderState.identifier?.entityType == EntityType.CHANNEL_LINEAR &&
        localStableConfiguration().isMediumWindow &&
        pageHeaderState.headerAiringInfo != null
    ) {
        EntityHeaderAiringInfoLayout(
            airingInfoState = pageHeaderState.headerAiringInfo,
            playAction = playAction,
            background = pageHeaderState.headerArtStateV2.backgroundImageState()?.removePlaceholder()
        )
    } else {
        if (pageHeaderState.headerCallInInfoState?.isAiringLive == true /*&& pageHeaderState.headerCallInInfoState.callInPhone != null*/) {
            Log.d("Hemang", "Hemang is live")
            ButtonSlideInOutAnimation(pageHeaderState, playAction)
        } else {
            Log.d("Hemang", "Hemang is not live")
            PlayButton(
                playAction = playAction,
                contentDescription = pageHeaderState.headerAiringInfo?.title ?: pageHeaderState.headerMetaData.title,
            )
        }
    }
}
 
@Composable
private fun PlayButton(
    modifier: Modifier = Modifier,
    playAction: EntityAction,
    contentDescription: StringContent? = null,
) {
    val actionHandler = localActionHandler()
    PlayButtonXLarge(
        modifier = modifier,
        state = rememberPlayButtonState(playAction = playAction, contentDescription = contentDescription),
        onClick = {
            actionHandler.handleAction(playAction)
        },
    )
}
 
@Composable
fun CallInDialButton(
    modifier: Modifier = Modifier,
    contentDescription: StringContent? = null,
) {
    val dialTarget = NavigationTarget<Intent>()
    StartActivityEffect(dialTarget)
    PrimaryXLargeIconButton(
        modifier = modifier,
        icon = ImageState.IconSxmImage(icon = Icon.SocialPhone, contentDescription = contentDescription.toString()),
        onClick = { dialTarget.navigate(createDialIntent(number = "1234567890")) },
    )
}
 
@Composable
internal fun PlayableContent(pageHeaderState: PageHeaderState) {
    if (pageHeaderState.headerActions.playAction != null) {
        when (pageHeaderState.identifier?.entityType) {
            EntityType.CHANNEL_XTRA,
            EntityType.CHANNEL_LINEAR -> ChannelPlayButton(pageHeaderState)
 
            EntityType.EPISODE_VOD,
            EntityType.EPISODE_LINEAR,
            EntityType.EPISODE_AOD,
            EntityType.EPISODE_PODCAST -> EpisodePlayButton(pageHeaderState)
 
            else -> PlayButton(
                playAction = pageHeaderState.headerActions.playAction,
                contentDescription = pageHeaderState.headerMetaData.title,
            )
        }
    }
}
 
@Composable
fun EpisodePlayButton(
    state: PageHeaderState,
    modifier: Modifier = Modifier,
) {
    val playAction = state.headerActions.playAction
    val reminderAction = state.headerActions.reminderAction
    if (state.headerEpisodeUiState.isPlayable && (playAction != null || reminderAction != null)) {
        if (state.shouldUseSimplePlayButton() && playAction != null) {
            val extraEndPadding = if (isTvWindow()) spacing.spacing4 else 0.dp
            PlayButton(
                modifier = modifier.padding(end = extraEndPadding),
                playAction = playAction,
                contentDescription = state.headerMetaData.title,
            )
        } else {
            if (state.headerCallInInfoState?.isAiringLive == true /*&& state.headerCallInInfoState.callInPhone != null*/) {
                Log.d("Hemang", "Hemang is live")
                //ButtonSlideInOutAnimation(pageHeaderState, playAction)
            } else {
                Log.d("Hemang", "Hemang is not live")
            }
            val actionHandler = localActionHandler()
            val isPlaying = playAction?.isPlayingOrBuffering() == true
            val isReminderEnabled = state.identifier?.let { rememberIsReminderEnabled(it.entityId).value } == true
            EpisodePlayButton(
                state = remember(state, isPlaying, isReminderEnabled) {
                    EntityPlayButtonState(
                        isUnentitled = state.headerActions.isEntitled.not(),
                        isLive = state.headerEpisodeUiState.isLive,
                        isPlaying = isPlaying,
                        isUpcoming = state.headerEpisodeUiState.isUpcoming,
                        durationLeft = state.headerEpisodeUiState.durationLeft,
                        progress = state.headerEpisodeUiState.progress,
                        hasNotificationsEnabled = isReminderEnabled,
                        progressState = state.headerEpisodeUiState.progressState,
                        contentDescription = state.headerMetaData.title,
                    )
                },
                onClick = rememberLambda(playAction, reminderAction) { ->
                    when {
                        playAction != null -> actionHandler.handleAction(playAction)
                        reminderAction != null -> actionHandler.handleAction(reminderAction)
                    }
                },
                modifier = modifier,
            )
        }
    }
}
 
@Suppress("UnusedPrivateMember")
@Composable
private fun ButtonSlideInOutAnimation(
    pageHeaderState: PageHeaderState,
    playAction: EntityAction,
) {
 
    val isPlaying by rememberUpdatedState(newValue = playAction.isPlayingOrBuffering())
 
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val playButtonOffset by animateDpAsState(
            targetValue = if (isPlaying) (-CALL_IN_BUTTON_ANIMATION_TARGET_VALUE).dp else 0.dp,
            animationSpec = tween(durationMillis = CALL_IN_ANIMATION_DURATION, easing = FastOutSlowInEasing)
        )
 
        val callButtonOffset by animateDpAsState(
            targetValue = if (isPlaying) CALL_IN_BUTTON_ANIMATION_TARGET_VALUE.dp else 0.dp,
            animationSpec = tween(durationMillis = CALL_IN_ANIMATION_DURATION, easing = FastOutSlowInEasing)
        )
 
        val callButtonAlpha by animateFloatAsState(
            targetValue = if (isPlaying) 1f else 0f,
            animationSpec = tween(durationMillis = CALL_IN_ANIMATION_DURATION, easing = FastOutSlowInEasing)
        )
 
        CallInDialButton(
            modifier = Modifier
                .offset { IntOffset(callButtonOffset.roundToPx(), 0) }
                .alpha(callButtonAlpha)
        )
 
        PlayButton(
            playAction = playAction,
            contentDescription = pageHeaderState.headerAiringInfo?.title ?: pageHeaderState.headerMetaData.title,
            modifier = Modifier.offset { IntOffset(playButtonOffset.roundToPx(), 0) }
        )
    }
}

@Suppress("UnusedPrivateMember")
@Composable
private fun ButtonSlideInOutAnimation1(
    pageHeaderState: PageHeaderState,
    playAction: EntityAction,
) {

    val isPlaying by rememberUpdatedState(newValue = playAction.isPlayingOrBuffering())

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val playButtonOffset by animateDpAsState(
            targetValue = if (isPlaying) (-CALL_IN_BUTTON_ANIMATION_TARGET_VALUE).dp else 0.dp,
            animationSpec = tween(durationMillis = CALL_IN_ANIMATION_DURATION, easing = FastOutSlowInEasing)
        )

        val callButtonOffset by animateDpAsState(
            targetValue = if (isPlaying) CALL_IN_BUTTON_ANIMATION_TARGET_VALUE.dp else 0.dp,
            animationSpec = tween(durationMillis = CALL_IN_ANIMATION_DURATION, easing = FastOutSlowInEasing)
        )

        val callButtonAlpha by animateFloatAsState(
            targetValue = if (isPlaying) 1f else 0f,
            animationSpec = tween(durationMillis = CALL_IN_ANIMATION_DURATION, easing = FastOutSlowInEasing)
        )

        CallInDialButton(
            modifier = Modifier
                .offset { IntOffset(callButtonOffset.roundToPx(), 0) }
                .alpha(callButtonAlpha)
        )

        PlayButton(val actionHandler = localActionHandler()
        val isPlaying = playAction?.isPlayingOrBuffering() == true
        val isReminderEnabled = state.identifier?.let { rememberIsReminderEnabled(it.entityId).value } == true
        EpisodePlayButton(
            state = remember(state, isPlaying, isReminderEnabled) {
                EntityPlayButtonState(
                    isUnentitled = state.headerActions.isEntitled.not(),
                    isLive = state.headerEpisodeUiState.isLive,
                    isPlaying = isPlaying,
                    isUpcoming = state.headerEpisodeUiState.isUpcoming,
                    durationLeft = state.headerEpisodeUiState.durationLeft,
                    progress = state.headerEpisodeUiState.progress,
                    hasNotificationsEnabled = isReminderEnabled,
                    progressState = state.headerEpisodeUiState.progressState,
                    contentDescription = state.headerMetaData.title,
                )
            },
            onClick = rememberLambda(playAction, reminderAction) { ->
                when {
                    playAction != null -> actionHandler.handleAction(playAction)
                    reminderAction != null -> actionHandler.handleAction(reminderAction)
                }
            },
            modifier = Modifier.offset { IntOffset(playButtonOffset.roundToPx(), 0) },
        )
    }
}
 
private fun PageHeaderState.shouldUseSimplePlayButton(): Boolean {
    return when {
        identifier?.entityType != EntityType.EPISODE_LINEAR -> false
        headerEpisodeUiState.isLive || headerEpisodeUiState.isUpcoming -> false
        else -> true
    }
}
 
@Composable
fun PageHeaderState.metadataAlignment(entityType: EntityType?): Pair<Alignment.Horizontal, TextAlign> =
    when (localStableConfiguration().windowSizeClass) {
        WindowSizeClass.Small -> metadataAlignmentSmall(entityType)
        WindowSizeClass.Medium -> metadataAlignmentMedium(entityType)
        WindowSizeClass.TV -> Alignment.Start to TextAlign.Start
    }
 
fun metadataAlignmentMedium(entityType: EntityType?): Pair<Alignment.Horizontal, TextAlign> {
    val alignment = when (entityType) {
        EntityType.TEAM,
        EntityType.LEAGUE -> Alignment.CenterHorizontally
 
        else -> Alignment.Start
    }
 
    val textAlignment = when (entityType) {
        EntityType.TEAM,
        EntityType.LEAGUE -> TextAlign.Center
 
        else -> TextAlign.Start
    }
 
    return alignment to textAlignment
}
 
@Composable
fun PageHeaderState.metadataAlignmentSmall(entityType: EntityType?): Pair<Alignment.Horizontal, TextAlign> {
    val alignment = when (entityType) {
        EntityType.BRAND,
        EntityType.EPISODE_LINEAR,
        EntityType.EPISODE_AOD,
        EntityType.EPISODE_VOD,
        EntityType.EPISODE_PODCAST,
        EntityType.GENRE,
        EntityType.CURATED_GROUPING -> Alignment.Start
 
        EntityType.LEAGUE,
        EntityType.TEAM,
        EntityType.TALENT,
        EntityType.SHOW,
        EntityType.PODCAST -> withFallback(Alignment.CenterHorizontally, Alignment.Start)
 
        else -> Alignment.CenterHorizontally
    }
    val textAlignment = when (entityType) {
        EntityType.BRAND,
        EntityType.EPISODE_LINEAR,
        EntityType.EPISODE_AOD,
        EntityType.EPISODE_VOD,
        EntityType.EPISODE_PODCAST,
        EntityType.GENRE,
        EntityType.CURATED_GROUPING -> TextAlign.Start
 
        EntityType.TALENT,
        EntityType.SHOW,
        EntityType.PODCAST -> withFallback(TextAlign.Center, TextAlign.Start)
 
        else -> TextAlign.Center
    }
 
    return alignment to textAlignment
}
 
@Composable
private fun PageHeaderState.withFallback(
    alignment: Alignment.Horizontal,
    fallback: Alignment.Horizontal
): Alignment.Horizontal = if (headerArtStateV2.hasValidBackground()) alignment else fallback
 
@Composable
private fun PageHeaderState.withFallback(alignment: TextAlign, fallback: TextAlign): TextAlign =
    if (headerArtStateV2.hasValidBackground()) alignment else fallback
 
@Composable
private fun metadataStyling(entityType: EntityType?): TextStyle {
    return when (localStableConfiguration().windowSizeClass) {
        WindowSizeClass.Small -> metadataStylingSmall(entityType)
        WindowSizeClass.Medium -> metadataStylingMedium(entityType)
        WindowSizeClass.TV -> metadataStylingTv(entityType)
    }
}
 
@Composable
private fun metadataStylingSmall(entityType: EntityType?): TextStyle {
    return when (entityType) {
        EntityType.EPISODE_LINEAR,
        EntityType.EPISODE_AOD,
        EntityType.EPISODE_VOD,
        EntityType.EPISODE_PODCAST -> typography.headlineSmall


        else -> typography.titleLarge
    }
}
 
@Composable
private fun metadataStylingMedium(entityType: EntityType?): TextStyle {
    return when (entityType) {
        EntityType.EPISODE_VOD,
        EntityType.TEAM,
        EntityType.LEAGUE -> typography.headlineMedium


        else -> typography.titleLarge
    }
}
 
@Composable
private fun metadataStylingTv(entityType: EntityType?): TextStyle {
    return when (entityType) {
        EntityType.EPISODE_LINEAR,
        EntityType.EPISODE_AOD,
        EntityType.EPISODE_VOD,
        EntityType.EPISODE_PODCAST -> typography.headlineSmall


        else -> typography.headlineLarge
    }
}
 
private class PageHeaderStatePreviewProvider(
    override val values: Sequence<PageHeaderState> = sequenceOf(
        // v1 default
        pageHeaderStateForPreview(HeaderType.DEFAULT, "Channel Entity Screen").run {
            copy(headerActions = headerActions.copy(textButtonState = null, downloadAction = null))
        },
        // v2 channel
        pageHeaderStateForPreview(
            pageTitle = "Entity Header V2",
            layout = Layout.V2_HEADER,
            entityType = EntityType.CHANNEL_LINEAR,
        ),
        // v2 show & podcast
        pageHeaderStateForPreview(
            pageTitle = "Entity Header V2",
            layout = Layout.V2_HEADER,
            entityType = EntityType.PODCAST,
            showParentLink = true
        ),
        // v2 episode vod
        pageHeaderStateForPreview(
            pageTitle = "Entity Header V2",
            layout = Layout.V2_HEADER,
            entityType = EntityType.EPISODE_VOD,
            showParentLink = true
        ).copy(headerAiringInfo = null),
        // v2 event with score
        pageHeaderStateForPreview(
            pageTitle = "Entity Header V2",
            layout = Layout.V2_HEADER_TV_DESCRIPTION,
            entityType = EntityType.EVENT,
            headerType = HeaderType.EPISODES,
            showParentLink = true,
            isLiveEvent = true,
        ).copy(headerAiringInfo = null), // Remove the airing info,
        // All Episode
        pageHeaderStateForPreview(HeaderType.EPISODES, "All Episodes").run {
            copy(headerActions = headerActions.copy(textButtonState = null, downloadAction = null))
        }
    )
) : PreviewParameterProvider<PageHeaderState>
 
private const val DEFAULT_MAX_LINES = 2
private const val CALL_IN_ANIMATION_DURATION = 300
private const val CALL_IN_BUTTON_WIDTH = 64
private const val CALL_IN_BUTTON_PADDING = 10
private const val CALL_IN_BUTTON_ANIMATION_TARGET_VALUE = (CALL_IN_BUTTON_WIDTH + CALL_IN_BUTTON_PADDING) / 2
 
 