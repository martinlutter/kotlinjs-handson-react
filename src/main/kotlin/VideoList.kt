import kotlinx.html.js.onClickFunction
import react.*
import react.dom.p
import kotlin.browser.window

class VideoList : RComponent<VideoListProps, RState>() {
    override fun RBuilder.render() {
        for (video in props.videos) {
            p {
                key = video.id.toString()

                attrs {
                    onClickFunction = {
                        props.onSelectVideo(video)
                    }
                }

                if (video == props.selectedVideo) {
                    +"▶ "
                }

                +"${video.speaker}: ${video.title}"
            }
        }
    }
}

external interface VideoListProps: RProps {
    var videos: List<Video>
    var selectedVideo: Video?
    var onSelectVideo: (Video) -> Unit
}