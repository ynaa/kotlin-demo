package no.miles.kotlindemo.bankdb.db

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.instanceOf
import no.miles.kotlindemo.bank.Account
import no.miles.kotlindemo.bank.Customer
import no.miles.kotlindemo.bank.Transaction
import no.miles.kotlindemo.bankdb.common.toDataSource
import org.h2.jdbcx.JdbcDataSource
import javax.sql.DataSource
/*
abstract class Test: StringSpec(){

    fun getDataSource(useInMemory: Boolean): DataSource {
        val defaultConfig: Config = ConfigFactory.load()
        val jdbcDataSource = defaultConfig.toDataSource()
        val h2DataSource: DataSource = JdbcDataSource().apply {
            setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
            user = "sa"
            password = "sa"
        }
        return if(useInMemory) h2DataSource else jdbcDataSource
    }
}

 */

class BankDaoTest: StringSpec(){

    private val datasource = JdbcDataSource().apply {
        setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
        user = "sa"
        password = "sa"
    }

    private val bankDao = BankDao(datasource)

    override suspend fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        bankDao.configureBankFlyway(datasource).migrate()
    }

    private val customer1 = Customer(1, "Yngve")
    private val customer2 = Customer(2, "Håvard")

    private val accNr1 = 1234_56_78910
    private val accNr2 = 9234_56_78910

    private val account1 = Account(3, "brukskonto", customer1.id, accNr1, 1000)
    private val account2 = Account(4, "brukskonto", customer2.id, accNr2, 1000)

    init {

        "Test customer"{
            val result = bankDao.createOrUpdateCustomer(customer1)
            result shouldBe 1

            val customers = bankDao.getCustomers()
            customers.size shouldBe 1
            customers.first() shouldBe customer1
        }

        "Test accounts" {
            bankDao.createOrUpdateCustomer(customer1)
            bankDao.createOrUpdateAccount(account1)

            val accounts = bankDao.getAccountsForCustomer(customer1.id)
            accounts.size shouldBe 1
            accounts.first() shouldBe account1
        }

        "Test transactions" {
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
            val result = bankDao.registerTransaction(transaction)
            result shouldBe instanceOf<TransactionOk>()

            val outgoingTransactions = bankDao.getOutgoingTransactions(account1.accountNumber)
            outgoingTransactions.size shouldBe 1
            outgoingTransactions.first().fromAccount shouldBe account1.accountNumber
            outgoingTransactions.first().toAccount shouldBe account2.accountNumber

            val updateAccount1 = bankDao.getAccountByAccountNumber(account1.accountNumber)!!
            updateAccount1.balance shouldBe account1.balance - transaction.amount

            val updateAccount2 = bankDao.getAccountByAccountNumber(account2.accountNumber)!!
            updateAccount2.balance shouldBe account2.balance + transaction.amount

            bankDao.createOrUpdateAccount(account1.copy(balance = 50))

            val illegalTransaction = Transaction(
                id = 6,
                fromAccount = account1.accountNumber,
                toAccount = account2.accountNumber,
                amount = 123,
                "Beer"
            )
            bankDao.registerTransaction(illegalTransaction) shouldBe instanceOf<NoMoneyResult>()
        }

        "Test transaction rollback" {
            bankDao.createOrUpdateCustomer(customer1)
            bankDao.createOrUpdateCustomer(customer2)

            bankDao.createOrUpdateAccount(account1)
            bankDao.createOrUpdateAccount(account2)

            val transaction = Transaction(
                id = 51,
                fromAccount = account1.accountNumber,
                toAccount = 1234,
                amount = 123,
                "Beer"
            )
            val result = bankDao.registerTransaction(transaction)
            result shouldBe instanceOf<IllegalAccount>()

            val notUpdatedAccount = bankDao.getAccountByAccountNumber(account1.accountNumber)
            notUpdatedAccount shouldBe account1
        }
    }
}