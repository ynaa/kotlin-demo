package no.miles.kotlindemo.bankdb

import io.ktor.serialization.kotlinx.xml.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*

fun ktorServer(
    controller: Controller,
    port: Int
): NettyApplicationEngine = embeddedServer(factory = Netty, port = port, module = {
        install(ContentNegotiation) {
            xml()
        }
        routing {
            defineRoutes(controller, "bank")
        }
    }
)

fun Route.defineRoutes(controller: Controller, contextPath: String) {
    route("/$contextPath") {
        get("/customers", controller.listCustomers)
        post("/customers", controller.createCustomer)
        get("/accounts/{customerId}", controller.getAccounts)
        get("/transactionsIn/{accountNumber}", controller.getIncomingTransactions)
        get("/transactionsOut/{accountNumber}", controller.getOutgoingTransactions)
        post("/transfer", controller.transfer)
    }
}