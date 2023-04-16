package no.miles.kotlindemo.bankdb.db


import no.miles.kotlindemo.bank.Account
import no.miles.kotlindemo.bank.Customer
import no.miles.kotlindemo.bank.Transaction
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import javax.sql.DataSource

class BankDao(private val datasource: DataSource) {

    private val database = Database.connect(datasource)

    fun configureBankFlyway(datasource: DataSource): Flyway = Flyway.configure()
        .dataSource(datasource)
        .table("BANK_FDB")
        .locations("db/migration/bank")
        .baselineOnMigrate(true)
        .load()


    fun getCustomers() = transaction(database)  {
        CustomerTable
            .selectAll()
            .map { it.toCustomer() }
    }

    fun insertCustomer(customer: Customer) = transaction(database)  {
        CustomerTable
            .replace {
                it[id] = customer.id
                it[name] = customer.name
            }
    }

    fun getAccounts(customer: Customer) = transaction(database)  {
        val accounts = getAccounts(customer.id)
        customer.copy(accounts = accounts)
    }

    fun getAccounts(customerId: Int) = transaction(database)  {
        AccountTable
            .select((AccountTable.owner eq customerId))
            .map { it.toAccount() }
    }

    fun getAccount(accountNumber: Long) = transaction(database)  {
        AccountTable
            .select((AccountTable.accountNumber eq accountNumber))
            .map { it.toAccount() }
            .firstOrNull()
    }

    fun insertAccount(account: Account) = transaction(database)  {
        AccountTable
            .replace {
                it[id] = account.id
                it[name] = account.name
                it[owner] = account.owner
                it[accountNumber] = account.accountNumber
                it[balance] = account.balance
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

    fun registerTransaction(transaction: Transaction): TransactionResult = transaction(database)  {
        val fromAccount = getAccount(transaction.fromAccount) ?: return@transaction IllegalAccount

        return@transaction if(fromAccount.balance < transaction.amount){
            NoMoneyResult
        } else {
            updateBalance(transaction.fromAccount, fromAccount.balance - transaction.amount)
            val toAccount = getAccount(transaction.toAccount) ?: return@transaction IllegalAccount
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

    fun getAccountWithIncomingTransactions(account: Account) = transaction(database)  {
        TransactionTable.join(
            otherTable = AccountTable,
            joinType = JoinType.RIGHT,
            onColumn = TransactionTable.toAccountNumber,
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