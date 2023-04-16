package no.miles.kotlindemo.bankdb

import no.miles.kotlindemo.bank.Account
import no.miles.kotlindemo.bank.Customer
import no.miles.kotlindemo.bank.Transaction
import no.miles.kotlindemo.bankdb.db.BankDao

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.sql.DataSource

const val fileName = "/shoes1.txt"

class BankDbApp(private val dataSource: DataSource) {

    private val logger: Logger = LoggerFactory.getLogger(BankDbApp::class.java)
    private val bankDao = BankDao(dataSource)
    private val controller = Controller(bankDao)

    private val nettyServer = ktorServer(controller, 8088)

    fun start() {
        bankDao.configureBankFlyway(datasource = dataSource).migrate()
        initData()
        nettyServer.start(wait = true)
        logger.info("Are we here?")
    }

    private fun initData() {

        val customer1 = Customer(1, "Yngve")
        val customer2 = Customer(2, "Håvard")

        val accNr1 = 1234_56_78910
        val accNr2 = 9234_56_78910

        val account1 = Account(3, "brukskonto", customer1.id, accNr1, 1000)
        val account2 = Account(4, "brukskonto", customer2.id, accNr2, 1000)

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


