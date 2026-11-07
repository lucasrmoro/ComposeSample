package br.com.composesample.structural.video_player.di

import br.com.composesample.structural.core.ext.dispatcherIO
import br.com.composesample.structural.di.dependencyModule
import br.com.composesample.structural.di.factory
import br.com.composesample.structural.di.get
import br.com.composesample.structural.di.loadModules
import br.com.composesample.structural.video_player.VideoPlaybackProvider
import br.com.composesample.structural.video_player.createVideoPlaybackProvider

object VideoPlayerModule {

    private val videoPlayerModule = dependencyModule {
        factory<VideoPlaybackProvider> {
            createVideoPlaybackProvider(
                videoPlaybackConfiguration = get(),
                context = get(),
                ioDispatcher = dispatcherIO
            )
        }
    }

    fun init() = loadModules(videoPlayerModule)

}
