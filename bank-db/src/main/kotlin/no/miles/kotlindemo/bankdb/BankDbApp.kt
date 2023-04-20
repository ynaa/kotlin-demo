package no.miles.kotlindemo.bankdb

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.ktor.serialization.kotlinx.xml.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import no.miles.kotlindemo.bank.Account
import no.miles.kotlindemo.bank.Customer
import no.miles.kotlindemo.bank.Transaction
import no.miles.kotlindemo.bank.exceptions.exceptionHandler
import no.miles.kotlindemo.bankdb.common.toDataSource
import no.miles.kotlindemo.bankdb.db.BankDao
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.sql.DataSource

fun main() {

    val defaultConfig: Config = ConfigFactory.load()
    val dataSource = defaultConfig.toDataSource()

    val app = BankDbApp(dataSource)
    app.start()
}

class BankDbApp(private val dataSource: DataSource) {

    private val bankDao = BankDao(dataSource)
    private val controller = Controller(bankDao)

    private val nettyServer = embeddedServer(factory = Netty, port = 8088, module = {
        install(ContentNegotiation) {
            xml()
        }
        exceptionHandler()
        routing {
            route("/bank") {
                get("/customers") { controller.listCustomers(call) }
                post("/customers") { controller.createCustomer(call) }
                get("/accounts/{customerId}") { controller.getAccounts(call) }
                get("/transactionsIn/{accountNumber}") { controller.getIncomingTransactions(call) }
                get("/transactionsOut/{accountNumber}") { controller.getOutgoingTransactions(call) }
                post("/transfer") { controller.transfer(call) }
            }
        }
    })

    fun start() {
        bankDao.configureBankFlyway(datasource = dataSource).migrate()
        initData()
        nettyServer.start(wait = true)
    }

    private fun initData() {

        val customer1 = Customer(1, "Yngve")
        val customer2 = Customer(2, "Håvard")

        val accNr1 = 1234_56_78910
        val accNr2 = 9234_56_78910

        val account1 = Account(3, "brukskonto", customer1.id, accNr1, 500)
        val account2 = Account(4, "brukskonto", customer2.id, accNr2, 500)

        bankDao.createOrUpdateCustomer(customer1)
        bankDao.createOrUpdateCustomer(customer2)
        bankDao.createOrUpdateAccount(account1)
        bankDao.createOrUpdateAccount(account2)

        val transaction = Transaction(
            id = 5,
            fromAccount = account1.accountNumber,
            toAccount = account2.accountNumber,
            amount = 123,
            "Beer"
        )
        try {
            bankDao.registerTransaction(transaction)
        }
        catch (e: Exception){}
    }

}


