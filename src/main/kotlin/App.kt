import kotlinx.coroutines.*
import kotlinx.serialization.DynamicObjectParser
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.w3c.dom.get
import org.w3c.dom.set
import react.*
import react.dom.div
import react.dom.h1
import react.dom.h3
import kotlin.browser.window

class App : RComponent<RProps, AppState>() {
    override fun RBuilder.render() {
        h1 {
            +"KotlinConf Explorer"
        }

        div {
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

    override fun AppState.init() {
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

external interface AppState : RState {
    var currentVideo: Video?
    var watchedVideos: List<Video>
    var unwatchedVideos: List<Video>
}

