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
import no.miles.kotlindemo.bank.exceptions.BadRequestApiException

class RestController(private val client: HttpClient) {

    private val baseUrl = "http://localhost:8088/bank"
    private val customerUrl = "$baseUrl/customers"
    private val accountsUrl =  "$baseUrl/accounts"
    private val incomingTransactionUrl = "$baseUrl/transactionsIn"
    private val outgoingTransactionUrl = "$baseUrl/transactionsOut"
    private val transferUrl = "$baseUrl/transfer"

    suspend fun listCustomers(call: ApplicationCall) {
        val customers: List<Customer> = client.get(customerUrl).body()
        val populatedCustomer = customers.map {
            it.copy(accounts = getAccounts(it.id))
        }
        call.respond(populatedCustomer)
    }

    suspend fun createCustomer(call: ApplicationCall) {
        val customer = call.receive<Customer>()
        client.post(customerUrl){
            contentType(ContentType.Application.Xml)
            setBody(customer)
        }
        call.respondText("Customer stored correctly with id ${customer.id}", status = HttpStatusCode.Created)
    }

    suspend fun accounts(call: ApplicationCall) {
        val customerId = call.parameters["customerId"]?.toInt()
        val accounts: List<Account> = getAccounts(customerId)
        call.respond(accounts)
    }

    suspend fun transfer(call: ApplicationCall) {
        val transaction = call.receive<Transaction>()
        val transactionResult = client.post(transferUrl){
            contentType(ContentType.Application.Xml)
            setBody(transaction)
        }
        when(transactionResult.status){
            HttpStatusCode.BadRequest -> throw BadRequestApiException(transactionResult.body())
            HttpStatusCode.OK -> call.respondText("transaction registered", status = HttpStatusCode.Created)
            else ->  call.respondText(transactionResult.body(), status = HttpStatusCode.InternalServerError)
        }
    }

    suspend fun incomingTransactions(call: ApplicationCall) {
        val accountNumber = call.parameters["accountNumber"]?.toLong()
        call.respond(getTransactions(accountNumber, incomingTransactionUrl))
    }

    suspend fun outgoingTransactions(call: ApplicationCall) {
        val accountNumber = call.parameters["accountNumber"]?.toLong()
        call.respond(getTransactions(accountNumber, outgoingTransactionUrl))
    }

    private suspend fun getTransactions(accountNumber: Long?, transactionUrl: String): List<Transaction> {
        val url = "$transactionUrl/$accountNumber"
        return client.get(url).body()
    }

    private suspend fun getAccounts(customerId: Int?): List<Account> {
        val url = "$accountsUrl/$customerId"
        val accounts: List<Account> = client.get(url).body()
        return accounts.map{
            it.copy(
                incomingTransactions = getTransactions(it.accountNumber, incomingTransactionUrl),
                outgoingTransactions = getTransactions(it.accountNumber, outgoingTransactionUrl),
            )
        }
    }
}