package no.miles.kotlindemo

import org.slf4j.Logger
import org.slf4j.LoggerFactory

val log: Logger = LoggerFactory.getLogger("adapter")

fun main() {
    log.info("Starting server")

    val demoApp = FrontApp()
    demoApp.start()
}


