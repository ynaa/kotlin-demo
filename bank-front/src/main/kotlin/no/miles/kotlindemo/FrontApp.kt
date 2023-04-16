package no.miles.kotlindemo

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.xml.*

class FrontApp {

    /*
    import io.ktor.client.engine.java.*
    private val javaClient = HttpClient(Java){
        install(ContentNegotiation) {
            xml()
        }
    }
     */

    private val cioClient = HttpClient(CIO){
        install(ContentNegotiation) {
            xml()
        }
    }

    private val restController = RestController(cioClient)
    private val nettyServer = ktorServer(restController, 8080)

    fun start() {
        nettyServer.start(wait = true)
    }
}


