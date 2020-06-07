import react.dom.*
import kotlin.browser.document

fun main() {
    render(document.getElementById("root")) {
        child(App::class) {}
    }
}

data class Video(val id: Int, val title: String, val speaker: String, val videoUrl: String)
