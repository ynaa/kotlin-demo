package no.miles.kotlindemo.bankdb

import io.ktor.serialization.kotlinx.xml.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import no.miles.kotlindemo.bank.exceptions.exceptionHandler

fun ktorServer(
    controller: Controller,
    port: Int
): NettyApplicationEngine = embeddedServer(factory = Netty, port = port, module = {
        install(ContentNegotiation) {
            xml()
        }
        exceptionHandler()
        routing {
            defineRoutes(controller, "bank")
        }
    }
)

fun Route.defineRoutes(controller: Controller, contextPath: String) {
    route("/$contextPath") {
        get("/customers") { controller.listCustomers(call) }
        post("/customers") { controller.createCustomer(call) }
        get("/accounts/{customerId}") { controller.getAccounts(call) }
        get("/transactionsIn/{accountNumber}") { controller.getIncomingTransactions(call) }
        get("/transactionsOut/{accountNumber}") { controller.getOutgoingTransactions(call) }
        post("/transfer") { controller.transfer(call) }
    }
}