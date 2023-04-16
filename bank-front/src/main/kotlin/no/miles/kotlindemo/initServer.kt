package no.miles.kotlindemo

import io.ktor.serialization.kotlinx.json.*
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

fun ktorServer(
    restController: RestController,
    port: Int
): NettyApplicationEngine = embeddedServer(factory = Netty, port = port, module = {
        install(ContentNegotiation) {
            json()
        }
        exceptionHandler()
        routing {
            defineRoutes(restController, "front")
            defineHealthEndpoint(enppoint_health_liveness, CheckType.Liveness)
            defineHealthEndpoint(endpoint_health_readiness, CheckType.Readiness)
        }
    }
)

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