import kotlin.js.Date

actual fun currentDateTime(): String {
    return Date().toISOString()
}
