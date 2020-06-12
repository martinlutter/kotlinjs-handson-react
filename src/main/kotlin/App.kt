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

class App : RComponent<RProps, RState>() {
    override fun RBuilder.render() {
        h1 {
            +"KotlinConf Explorer"
        }


        div {
            child(VideoSearch::class) {}
        }
    }
}

