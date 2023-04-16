package no.miles.kotlindemo

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import no.miles.kotlindemo.bank.Account
import no.miles.kotlindemo.bank.Customer
import no.miles.kotlindemo.bank.Transaction
import no.miles.kotlindemo.bankdb.common.callHandler

class RestController(private val client: HttpClient) {

    private val baseUrl = "http://localhost:8088/bank"
    private val customerUrl = "$baseUrl/customers"
    private val accountsUrl =  "$baseUrl/accounts"
    private val incomingTransactionUrl = "$baseUrl/transactionsIn"
    private val outgoingTransactionUrl = "$baseUrl/transactionsOut"
    private val transferUrl = "$baseUrl/transfer"

    val listCustomers = callHandler {
        val customers: List<Customer> = client.get(customerUrl).body()
        val populatedCustomer = customers.map {
            it.copy(accounts = getAccounts(it.id))
        }
        call.respond(populatedCustomer)
    }

    val createCustomer = callHandler {
        val customer = call.receive<Customer>()
        client.post(customerUrl){
            contentType(ContentType.Application.Xml)
            setBody(customer)
        }
        call.respondText("Customer stored correctly with id ${customer.id}", status = HttpStatusCode.Created)
    }

    suspend fun createCustomerIt(call: ApplicationCall) {
        val customer = call.receive<Customer>()
        client.post(customerUrl){
            contentType(ContentType.Application.Xml)
            setBody(customer)
        }
        call.respondText("Customer stored correctly with id ${customer.id}", status = HttpStatusCode.Created)
    }

    val accounts = callHandler {
        val customerId = call.parameters["customerId"]?.toInt()
        val accounts: List<Account> = getAccounts(customerId)
        call.respond(accounts)
    }

    val transfer = callHandler {
        val transaction = call.receive<Transaction>()
        val transactionResult = client.post(transferUrl){
            contentType(ContentType.Application.Xml)
            setBody(transaction)
        }
        when(transactionResult.status){
            HttpStatusCode.BadRequest ->  call.respondText(transactionResult.body(), status = HttpStatusCode.BadRequest)
            HttpStatusCode.OK -> call.respondText("transaction registerd", status = HttpStatusCode.Created)
            else ->  call.respondText(transactionResult.body(), status = HttpStatusCode.InternalServerError)
        }
    }

    private suspend fun getAccounts(customerId: Int?): List<Account> {
        val url = "$accountsUrl/$customerId"
        val accounts: List<Account> = client.get(url).body()
        val poplulatedAccounts = accounts.map{
            it.copy(
                incomingTransactions = getTransactions(it.accountNumber, incomingTransactionUrl),
                outgoingTransactions = getTransactions(it.accountNumber, outgoingTransactionUrl),
            )
        }
        return poplulatedAccounts
    }

    val incomingTransactions = callHandler {
        val accountNumber = call.parameters["accountNumber"]?.toLong()
        call.respond(getTransactions(accountNumber, incomingTransactionUrl))
    }

    val outgoingTransactions = callHandler {
        val accountNumber = call.parameters["accountNumber"]?.toLong()
        call.respond(getTransactions(accountNumber, outgoingTransactionUrl))
    }

    private suspend fun getTransactions(accountNumber: Long?, transactionUrl: String): List<Transaction> {
        val url = "$transactionUrl/$accountNumber"
        return client.get(url).body()
    }

}