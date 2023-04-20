package no.miles.kotlindemo.bank.exceptions

import io.ktor.http.*
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val apiExceptionLogger: Logger = LoggerFactory.getLogger("RestErrorStatusLogger")

fun Application.exceptionHandler() {
    install(StatusPages) {
        exception<BadRequestApiException> { call, error ->
            call.exResponse(HttpStatusCode.BadRequest, error)
        }
        exception<Exception> { call, error ->
            call.exResponse(HttpStatusCode.InternalServerError, error)
        }
    }
}

private suspend fun ApplicationCall.exResponse(
    statusCode: HttpStatusCode,
    ex: Exception
) {
    apiExceptionLogger.info("HTTP $statusCode due to: ${ex.message}")
    respond(statusCode, ex.message ?: "")
}