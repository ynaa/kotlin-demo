package no.miles.kotlindemo.bankdb.db

import org.jetbrains.exposed.sql.Table

object CustomerTable : Table("CUSTOMER_TABLE") {
    val id = integer("ID")
    val name = varchar("NAME", 255)

    override val primaryKey: PrimaryKey = PrimaryKey(id, name = "${tableName}_pk")
}

object AccountTable : Table("ACCOUNT_TABLE") {
    val id = integer("ID")
    val owner = integer("OWNER")
    val accountNumber = long("ACCOUNT_NUMBER")
    val name = varchar("NAME", 255)
    val balance = integer("BALANCE")

    override val primaryKey: PrimaryKey = PrimaryKey(id, name = "${tableName}_pk")
}

object TransactionTable : Table("TRANSACTION_TABLE") {
    val id = integer("ID")
    val toAccountNumber = long("TO_ACOUNT_NUMBER")
    val fromAccountNumber = long("FROM_ACOUNT_NUMBER")
    val name = varchar("NAME", 255)
    val amount = integer("AMOUNT")

    override val primaryKey: PrimaryKey = PrimaryKey(id, name = "${tableName}_pk")
}