package no.miles.kotlindemo.bankdb

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import no.miles.kotlindemo.bank.Customer
import no.miles.kotlindemo.bank.Transaction
import no.miles.kotlindemo.bank.exceptions.BadRequestApiException
import no.miles.kotlindemo.bankdb.db.BankDao
import no.miles.kotlindemo.bankdb.db.IllegalAccount
import no.miles.kotlindemo.bankdb.db.NoMoneyResult
import no.miles.kotlindemo.bankdb.db.TransactionOk

class Controller(private val bankDao: BankDao) {

    suspend fun listCustomers(call: ApplicationCall) {
        val customers = bankDao.getCustomers().sortedBy { it.id }
        call.respond(customers)
    }

    suspend fun createCustomer(call: ApplicationCall) {
        val customer = call.receive<Customer>()
        bankDao.createOrUpdateCustomer(customer)
        call.respondText("Customer stored correctly with id ${customer.id}", status = HttpStatusCode.Created)
    }

    suspend fun getAccounts(call: ApplicationCall) {
        val customerId = call.getParameter("customerId").toInt()
        val accounts = bankDao.getAccountsForCustomer(customerId)
        call.respond(accounts)
    }

    suspend fun getOutgoingTransactions(call: ApplicationCall) {
        val accountNumber = call.getParameter("accountNumber").toLong()
        val transactions = bankDao.getOutgoingTransactions(accountNumber)
        call.respond(transactions)
    }

    suspend fun getIncomingTransactions(call: ApplicationCall) {
        val accountNumber = call.getParameter("accountNumber").toLong()
        val transactions = bankDao.getIncomingTransactions(accountNumber)
        call.respond(transactions)
    }

    suspend fun getOutgoingTransactionsForAccount(call: ApplicationCall) {
        val accountNumber = call.getParameter("accountNumber").toLong()
        val transactions = bankDao.getOutgoingTransactions(accountNumber)
        call.respond(transactions)
    }

    suspend fun transfer(call: ApplicationCall) {
        val transaction = call.receive<Transaction>()
        when (bankDao.registerTransaction(transaction)) {
            IllegalAccount -> throw BadRequestApiException("Transaction $transaction contains illegal accountnumber")
            NoMoneyResult -> throw BadRequestApiException("Balance of ${transaction.fromAccount} is lower then amount ${transaction.amount}")
            TransactionOk -> call.respondText("Transactions registered with id ${transaction.id}", status = HttpStatusCode.Created)
        }
    }
}

private fun ApplicationCall.getParameter(parameterName: String): String {
    return parameters[parameterName] ?: throw BadRequestApiException("Missing parameter $parameterName")
}
