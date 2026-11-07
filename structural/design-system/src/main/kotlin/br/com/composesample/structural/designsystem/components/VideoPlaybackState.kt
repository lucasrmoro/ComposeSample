package br.com.composesample.structural.designsystem.components

import br.com.composesample.structural.video_player.VideoEmbed

internal sealed interface VideoPlaybackState {

    data object Ready : VideoPlaybackState
    data object Loading : VideoPlaybackState
    data object Failed : VideoPlaybackState
    data class Playing(val embed: VideoEmbed) : VideoPlaybackState

}
