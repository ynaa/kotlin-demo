package no.miles.kotlindemo

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.Serializable
import no.miles.kotlindemo.exceptions.ItemNotFoundException
import no.miles.kotlindemo.test.Shoe
import no.miles.kotlindemo.test.ShoeTableDao

class Controller(shoeDao: ShoeTableDao) {

    val defaultGetter = callHandler {
        call.respondText("Hello, world!")
    }

    val customer = callHandler {
        val customer = Customer(
            id = 1,
            firstName = "Yngve",
            lastName = "Aas"
        )
        call.respond(customer)
    }

    val list = callHandler {
        val shoes = shoeDao.get()
        call.respond(shoes)
    }

    val create = callHandler {
        val shoe = call.receive<Shoe>()
        shoeDao.insert(shoe)
        call.respondText("Shoe stored correctly with date $shoe", status = HttpStatusCode.Created)
    }

    val createCustomer = callHandler {
        val customer = call.receive<Customer>()
        call.respondText("Customer stored correctly", status = HttpStatusCode.Created)
    }


    val notfound = callHandler {
        throw ItemNotFoundException("Item not found")
    }
}

@Serializable
data class Customer(val id: Int, val firstName: String, val lastName: String)

inline fun callHandler(
    crossinline callFun: suspend PipelineContext<Unit, ApplicationCall>.() -> Unit
): suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit = {
        callFun()
}