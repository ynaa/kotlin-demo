package no.miles.kotlindemo.health

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

enum class CheckType {
    Liveness, Readiness
}

val monitoringEndpointLogger: Logger = LoggerFactory.getLogger("monitoring_endpoint")

fun Routing.defineHealthEndpoint(
    path: String,
    type: CheckType
) {
    monitoringEndpointLogger.info("Enabling health endpoint, type=$type, path=$path")
    get(path) {
        call.respond(HttpStatusCode.OK, "$path is UP")
    }
}
