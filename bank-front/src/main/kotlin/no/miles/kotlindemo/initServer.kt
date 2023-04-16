package no.miles.kotlindemo

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import no.miles.kotlindemo.bankdb.common.endpoint_health_readiness
import no.miles.kotlindemo.bankdb.common.enppoint_health_liveness
import no.miles.kotlindemo.exceptions.exceptionHandler
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
            //defineFrontRoutes(restController, "front")
            defineHealthEndpoint(enppoint_health_liveness, CheckType.Liveness)
            defineHealthEndpoint(endpoint_health_readiness, CheckType.Readiness)
            defineRouting(restController, "front")
        }
    }
)

fun Routing.defineRouting(controller: RestController, contextPath: String) {
    get("/$contextPath/customers", controller.listCustomers)
    //post("/customer", controller.createCustomer)
    get("/$contextPath/accounts/{customerId}", controller.accounts)
    get("/$contextPath/transfer", controller.transfer)
    get("/$contextPath/transactionsIn/{accountNumber}", controller.incomingTransactions)
    get("/$contextPath/transactionsOut/{accountNumber}", controller.outgoingTransactions)
    post("/$contextPath/transfer", controller.transfer)

    post("/$contextPath/customer") { controller.createCustomerIt(call) }
}

fun Route.defineFrontRoutes(controller: RestController, contextPath: String) {
    route("/$contextPath") {
        get("/customers", controller.listCustomers)
        post("/customer", controller.createCustomer)
        get("/accounts/{customerId}", controller.accounts)
        get("/transfer", controller.transfer)
        get("/transactionsIn/{accountNumber}", controller.incomingTransactions)
        get("/transactionsOut/{accountNumber}", controller.outgoingTransactions)
        post("/transfer", controller.transfer)
    }
}