package no.miles.kotlindemo

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.xml.*

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class FrontApp {

    private val logger: Logger = LoggerFactory.getLogger("adapter")

    private val client = HttpClient(CIO){
        install(ContentNegotiation) {
            xml()
        }
    }

    private val restController = RestController(client)
    private val nettyServer = ktorServer(restController, 8080)

    fun start() {
        nettyServer.start(wait = true)
        logger.info("Are we here?")
    }
}


