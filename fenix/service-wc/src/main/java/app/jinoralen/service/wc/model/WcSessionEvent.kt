package app.jinoralen.service.wc.model

sealed class WcSessionEvent {
    data class WcSessionApproved(val topic: String): WcSessionEvent()
    object WcSessionRejected: WcSessionEvent()
}
