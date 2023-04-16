package no.miles.kotlindemo.bank

import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    val id: Int,
    val name: String,
    val accounts: List<Account> = emptyList()
)

@Serializable
data class Account(
    val id: Int,
    val name: String,
    val owner: Int,
    val accountNumber: Long,
    val balance: Int,
    val incomingTransactions: List<Transaction> = emptyList(),
    val outgoingTransactions: List<Transaction> = emptyList()
)

@Serializable
data class Transaction(
    val id: Int,
    val fromAccount: Long,
    val toAccount: Long,
    val amount: Int,
    val name: String
)