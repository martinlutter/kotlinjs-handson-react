import kotlinx.serialization.Serializable
import react.dom.render
import kotlin.browser.document

fun main() {
    render(document.getElementById("root")) {
        child(App::class) {}
    }
}

@Serializable
data class Video(val id: Int, val title: String, val speaker: String, val videoUrl: String)
