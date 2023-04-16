package no.miles.kotlindemo.bankdb.common

import com.typesafe.config.Config
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

const val enppoint_health_liveness = "/health/liveness"
const val endpoint_health_readiness = "/health/readiness"

fun Config.toDataSource(): DataSource =
    loadConfig("datasource") {
        HikariDataSource().also {
            it.username = getString("user")
            it.password = getString("password")
            it.driverClassName = getString("driver")
            it.jdbcUrl = getString("url")
            it.maximumPoolSize = 5
            it.isAutoCommit = false
        }
    }

fun <C> Config.loadConfig(name: String, load: Config.() -> C): C =
    getConfig(name).load()