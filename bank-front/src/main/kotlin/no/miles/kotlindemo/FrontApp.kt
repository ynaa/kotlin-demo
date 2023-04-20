package no.miles.kotlindemo

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.serialization.kotlinx.json.*

import io.ktor.serialization.kotlinx.xml.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import no.miles.kotlindemo.bank.exceptions.exceptionHandler
import no.miles.kotlindemo.bankdb.common.endpoint_health_readiness
import no.miles.kotlindemo.bankdb.common.enppoint_health_liveness
import no.miles.kotlindemo.health.CheckType
import no.miles.kotlindemo.health.defineHealthEndpoint

typealias ClientNegotiation = io.ktor.client.plugins.contentnegotiation.ContentNegotiation

fun main() {
    val frontApp = FrontApp()
    frontApp.start()
}

class FrontApp {

    /*
    import io.ktor.client.engine.java.*
    private val javaClient = HttpClient(Java){
        install(ContentNegotiation) {
            xml()
        }
    }
     */

    private val cioClient = HttpClient(engineFactory = CIO){
        install(ClientNegotiation) {
            xml()
        }
    }

    private val restController = RestController(cioClient)

    private val nettyServer = embeddedServer(factory = Netty, port = 8080, module = {
        install(ContentNegotiation) {
            json()
        }
        exceptionHandler()
        routing {
            defineRoutes(restController, "front")
            defineHealthEndpoint(enppoint_health_liveness, CheckType.Liveness)
            defineHealthEndpoint(endpoint_health_readiness, CheckType.Readiness)
        }}
    )

    fun start() {
        nettyServer.start(wait = true)
    }
}

fun Route.defineRoutes(controller: RestController, path: String) {
    route("/$path") {
        get("/customers") { controller.listCustomers(call) }
        post("/customer") { controller.createCustomer(call) }
        get("/accounts/{customerId}") { controller.accounts(call) }
        get("/transactionsIn/{accountNumber}") { controller.incomingTransactions(call) }
        get("/transactionsOut/{accountNumber}") { controller.outgoingTransactions(call) }
        post("/transfer") { controller.transfer(call) }
    }
}


