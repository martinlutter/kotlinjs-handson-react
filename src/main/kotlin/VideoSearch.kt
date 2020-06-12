import kotlinx.coroutines.*
import kotlinx.serialization.DynamicObjectParser
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.w3c.dom.get
import org.w3c.dom.set
import react.*
import react.dom.h3
import kotlin.browser.window

class VideoSearch : RComponent<RProps, VideoSearchState>() {
    override fun RBuilder.render() {
        child(VideoSearchInput::class) {
            attrs.onChange = { inputText ->
                setState {
                    query = inputText
                    searchResults = (state.unwatchedVideos + state.watchedVideos).filter { video ->
                        (video.speaker + video.title).contains(inputText, true)
                    }
                }
            }
        }

        if (state.query.isEmpty()) {
            h3 {
                +"Videos to watch"
            }

            videoList {
                videos = state.unwatchedVideos
                selectedVideo = state.currentVideo
                onSelectVideo = { video ->
                    setState {
                        currentVideo = video
                    }
                }
            }

            h3 {
                +"Videos watched"
            }

            videoList {
                videos = state.watchedVideos
                selectedVideo = state.currentVideo
                onSelectVideo = { video ->
                    setState {
                        currentVideo = video
                    }
                }
            }
        }

        if (state.query.isNotEmpty()) {
            h3 {
                +"Search:"
            }

            videoList {
                videos = state.searchResults
                selectedVideo = state.currentVideo
                onSelectVideo = { video ->
                    setState {
                        currentVideo = video
                    }
                }
            }
        }

        state.currentVideo?.let { currentVideo ->
            videoPlayer {
                video = currentVideo
                unwatchedVideo = currentVideo in state.unwatchedVideos
                onWatchedButtonPressed = {
                    if (video in state.unwatchedVideos) {
                        setState {
                            unwatchedVideos -= video
                            watchedVideos += video
                            window.localStorage["watchedVideos"] =
                                Json.stringify(ListSerializer(Video.serializer()), watchedVideos)
                        }
                    } else {
                        setState {
                            watchedVideos -= video
                            unwatchedVideos += video
                            window.localStorage["watchedVideos"] =
                                Json.stringify(ListSerializer(Video.serializer()), watchedVideos)
                        }
                    }

                }
            }
        }
    }

    override fun VideoSearchState.init() {
        query = ""
        unwatchedVideos = listOf()

        val storedWatchedVideos = window.localStorage["watchedVideos"] ?: ""
        if (storedWatchedVideos.isNotEmpty()) {
            watchedVideos = Json.parse(ListSerializer(Video.serializer()), storedWatchedVideos)
        } else {
            watchedVideos = listOf()
        }

        MainScope().launch {
            val videos = fetchVideos()
            setState {
                unwatchedVideos = videos - watchedVideos
            }
        }
    }
}

external interface VideoSearchState : RState {
    var query: String
    var searchResults: List<Video>
    var currentVideo: Video?
    var watchedVideos: List<Video>
    var unwatchedVideos: List<Video>
}


fun RBuilder.videoList(handler: VideoListProps.() -> Unit): ReactElement {
    return child(VideoList::class) {
        this.attrs(handler)
    }
}

suspend fun fetchVideo(id: Int): Video =
    DynamicObjectParser().parse(
        window.fetch("https://my-json-server.typicode.com/kotlin-hands-on/kotlinconf-json/videos/$id").await()
            .json().await().asDynamic(), Video.serializer()
    )

suspend fun fetchVideos(): List<Video> = coroutineScope {
    (1..25).map {
        async {
            fetchVideo(it)
        }
    }.awaitAll()
}