package no.miles.kotlindemo.bankdb.db

import no.miles.kotlindemo.bank.Account
import no.miles.kotlindemo.bank.Customer
import no.miles.kotlindemo.bank.Transaction
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import javax.sql.DataSource

class BankDao(datasource: DataSource) {

    private val database = Database.connect(datasource)

    fun configureBankFlyway(datasource: DataSource): Flyway = Flyway.configure()
        .dataSource(datasource)
        .table("BANK_FDB")
        .locations("db/migration/bank")
        .baselineOnMigrate(true)
        .load()

    fun createOrUpdateCustomer(customer: Customer): Int {
        val existing = getCustomer(customer.id)
        return if(existing == null){
            insert(customer).insertedCount
        }
        else {
            updateCustomer(customer)
        }
    }

    private fun getCustomer(customerId: Int) = transaction(database)  {
        CustomerTable
            .select((CustomerTable.id eq customerId))
            .map { it.toCustomer() }
            .firstOrNull()
    }

    private fun insert(customer: Customer) = transaction(database)  {
        CustomerTable
            .insert {
                it[id] = customer.id
                it[name] = customer.name
            }
    }

    private fun updateCustomer(customer: Customer) = transaction(database)  {
        CustomerTable
            .update (
                where = { (CustomerTable.id eq customer.id) }
            ){
                it[name] = customer.name
            }
    }

    fun getCustomers() = transaction(database)  {
        CustomerTable
            .selectAll()
            .map { it.toCustomer() }
    }

    fun getAccountsForCustomer(customerId: Int) = transaction(database)  {
        AccountTable
            .select((AccountTable.owner eq customerId))
            .map { it.toAccount() }
    }

    fun getAccountByAccountNumber(accountNumber: Long) = transaction(database)  {
        AccountTable
            .select((AccountTable.accountNumber eq accountNumber))
            .map { it.toAccount() }
            .firstOrNull()
    }


    fun createOrUpdateAccount(account: Account) {
        val existing = getAccount(account.id)
        if(existing == null){
            insert(account)
        }
        else {
            updateAccount(account)
        }
    }

    private fun getAccount(accountId: Int) = transaction(database)  {
        AccountTable
            .select((AccountTable.id eq accountId))
            .map { it.toAccount() }
            .firstOrNull()
    }

    private fun updateAccount(account: Account) = transaction(database)  {
        AccountTable
            .update (
                where = { (AccountTable.id eq account.id) }
            ){
                it[name] = account.name
                it[owner] = account.owner
                it[accountNumber] = account.accountNumber
                it[balance] = account.balance
            }
    }

    fun insert(account: Account) = transaction(database)  {
        AccountTable
            .replace {
                it[id] = account.id
                it[name] = account.name
                it[owner] = account.owner
                it[accountNumber] = account.accountNumber
                it[balance] = account.balance
            }
    }


    fun registerTransaction(transaction: Transaction): TransactionResult = transaction(database)  {
        val fromAccount = getAccountByAccountNumber(transaction.fromAccount) ?: return@transaction IllegalAccount

        return@transaction if(fromAccount.balance < transaction.amount){
            rollback()
            NoMoneyResult
        } else {
            updateBalance(transaction.fromAccount, fromAccount.balance - transaction.amount)
            val toAccount = getAccountByAccountNumber(transaction.toAccount)
            if(toAccount == null) {
                rollback()
                return@transaction IllegalAccount
            }
            updateBalance(transaction.toAccount, toAccount.balance + transaction.amount)
            TransactionTable
                .insert {
                    it[id] = transaction.id
                    it[amount] = transaction.amount
                    it[name] = transaction.name
                    it[fromAccountNumber] = transaction.fromAccount
                    it[toAccountNumber] = transaction.toAccount
                }
            TransactionOk
        }
    }

    private fun updateBalance(accountNumber: Long, newBalance: Int) = transaction(database)  {
        AccountTable
            .update (
                where = {
                    (AccountTable.accountNumber eq accountNumber)
                }
            ){
                it[balance] = newBalance
            }
    }
    fun getOutgoingTransactions(accountNumber: Long) = transaction(database)  {
        TransactionTable
            .select((TransactionTable.fromAccountNumber eq accountNumber))
            .map { it.toTransaction() }
    }

    fun getIncomingTransactions(accountNumber: Long) = transaction(database)  {
        TransactionTable
            .select((TransactionTable.toAccountNumber eq accountNumber))
            .filterNotNull()
            .map { it.toTransaction() }
    }

    fun getAllTransactionsForAccount(account: Account) = transaction(database)  {
        TransactionTable.join(
            otherTable = AccountTable,
            joinType = JoinType.RIGHT,
            onColumn = TransactionTable.toAccountNumber,
            otherColumn = AccountTable.accountNumber
        ).slice(
            TransactionTable.id
            //Pick fields to return
        ).select(
            (TransactionTable.fromAccountNumber eq account.accountNumber) or
                    (TransactionTable.toAccountNumber eq account.accountNumber)
        ).map { it /* do mapping*/ }
    }

    fun getAccountWithOutgoingTransactions(account: Account) = transaction(database)  {
        TransactionTable.join(
            otherTable = AccountTable,
            joinType = JoinType.RIGHT,
            onColumn = TransactionTable.fromAccountNumber,
            otherColumn = AccountTable.accountNumber
        ).slice(
            TransactionTable.id,
            TransactionTable.name,
            TransactionTable.amount,
            TransactionTable.fromAccountNumber,
            TransactionTable.toAccountNumber,
        )
            .select((AccountTable.accountNumber eq account.accountNumber))
            .map { it.toTransaction() }
    }

}

fun ResultRow.toCustomer() = Customer(
    id = this[CustomerTable.id],
    name = this[CustomerTable.name]
)

fun ResultRow.toAccount() = Account(
    id = this[AccountTable.id],
    name = this[AccountTable.name],
    owner = this[AccountTable.owner],
    accountNumber = this[AccountTable.accountNumber],
    balance = this[AccountTable.balance]
)

fun ResultRow.toTransaction() = Transaction(
    id = this[TransactionTable.id],
    fromAccount = this[TransactionTable.fromAccountNumber],
    toAccount = this[TransactionTable.toAccountNumber],
    amount = this[TransactionTable.amount],
    name = this[TransactionTable.name]
)

sealed interface TransactionResult

object TransactionOk: TransactionResult
object NoMoneyResult: TransactionResult
object IllegalAccount: TransactionResult