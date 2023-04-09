package app.jinoralen.service.wc.model

sealed class WcAccountEvent {
    data class SessionRequestSuccessResponse(
        val result: String
    ): WcAccountEvent()

    data class SessionRequestErrorResponse(
        val errorMessage: String,
        val errorCode: Int
    ): WcAccountEvent()
}
