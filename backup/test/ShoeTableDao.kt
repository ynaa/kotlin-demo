package no.miles.kotlindemo.test

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.sql.DataSource

class ShoeTableDao(dataSource: DataSource) {

    private val logger: Logger = LoggerFactory.getLogger(ShoeTableDao::class.java)

    private val database = Database.connect(dataSource)
    private val table = ShoeTable

    fun insert(shoe: Shoe): InsertStatement<Number> =
        transaction(database) {
            table.insert {
                it[table.name] = shoe.name
                it[table.brand] = shoe.brand
                it[table.type] = shoe.shoeType
                it[table.gender] = shoe.gender
                it[table.color] = shoe.color
                it[table.season] = shoe.season
            }.also { logger.info("Inserted shoe. (name: ${shoe.name}, type: ${shoe.brand} )") }
    }

    fun get(): List<Shoe> =
        transaction(database) {
            ShoeTable.selectAll().map { it.toShoe() }
        }
}

private fun ResultRow.toShoe() =
    createShoe(
        name = this[ShoeTable.name],
        brand = this[ShoeTable.brand],
        type = this[ShoeTable.type],
        color = this[ShoeTable.color],
        gender = this[ShoeTable.gender],
        season = this[ShoeTable.season])