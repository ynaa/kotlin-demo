package no.miles.kotlindemo.bankdb

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import no.miles.kotlindemo.bankdb.common.toDataSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val log: Logger = LoggerFactory.getLogger("database-adapter")

fun main() {
    log.info("Starting server")

    val defaultConfig: Config = ConfigFactory.load()
    val dataSource = defaultConfig.toDataSource()

    val app = BankDbApp(dataSource)
    app.start()
}


