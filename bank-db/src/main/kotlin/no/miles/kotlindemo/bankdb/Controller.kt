package no.miles.kotlindemo.bankdb

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import no.miles.kotlindemo.bank.Customer
import no.miles.kotlindemo.bank.Transaction
import no.miles.kotlindemo.bankdb.common.callHandler
import no.miles.kotlindemo.bankdb.db.BankDao
import no.miles.kotlindemo.bankdb.db.IllegalAccount
import no.miles.kotlindemo.bankdb.db.NoMoneyResult
import no.miles.kotlindemo.bankdb.db.TransactionOk

class Controller(bankDao: BankDao) {

    val listCustomers = callHandler {
        val customers = bankDao.getCustomers().sortedBy { it.id }
        call.respond(customers)
    }

    val createCustomer = callHandler {
        val customer = call.receive<Customer>()
        bankDao.insertCustomer(customer)
        call.respondText("Customer stored correctly with id ${customer.id}", status = HttpStatusCode.Created)
    }
    
    val getAccounts = callHandler{
        val customerId = call.parameters["customerId"]?.toInt()
        val accounts = bankDao.getAccounts(customerId!!)
        call.respond(accounts)
    }

    val getOutgoingTransactions = callHandler{
        val accountNumber = call.parameters["accountNumber"]?.toLong()
        val transactions = bankDao.getOutgoingTransactions(accountNumber!!)
        call.respond(transactions)
    }

    val getIncomingTransactions = callHandler{
        val accountNumber = call.parameters["accountNumber"]?.toLong()
        val transactions = bankDao.getIncomingTransactions(accountNumber!!)
        call.respond(transactions)
    }

    val getOutgoingTransactionsForAccount = callHandler{
        val accountNumber = call.parameters["accountNumber"]?.toLong()
        val transactions = bankDao.getOutgoingTransactions(accountNumber!!)
        call.respond(transactions)
    }

    val transfer = callHandler{
        val transaction = call.receive<Transaction>()
        when(bankDao.registerTransaction(transaction)){
            IllegalAccount -> call.respondText("One accountnumber is not correct for request $transaction", status = HttpStatusCode.BadRequest)
            NoMoneyResult -> call.respondText("Not sufficent balance", status = HttpStatusCode.InternalServerError)
            TransactionOk -> call.respondText("Account created with id ", status = HttpStatusCode.Created)
        }
    }
}
