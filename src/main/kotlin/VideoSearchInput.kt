import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onSubmitFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.*
import react.dom.form
import react.dom.input
import styled.styledDiv

class VideoSearchInput : RComponent<VideoSearchInputProps, RState>() {
    override fun RBuilder.render() {
        styledDiv {
            form {
                attrs {
                    onSubmitFunction = { event ->
                        event.preventDefault()
                    }
                }

                input(InputType.text) {
                    attrs {
                        onChangeFunction = { onChange(it) }
                    }
                }
            }
        }
    }

    private fun onChange(event: Event) {
        val target = event.target ?: return
        props.onChange(target.unsafeCast<HTMLInputElement>().value)
    }
}

external interface VideoSearchInputProps : RProps {
    var onChange: (String) -> Unit
}