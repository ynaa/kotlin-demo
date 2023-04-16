package no.miles.kotlindemo.test

import org.jetbrains.exposed.sql.Table

object ShoeTable : Table("SHOE_TABLE") {
    val id = integer("id").autoIncrement()
    val name = varchar("NAME", 255)
    val brand = varchar("BRAND", 255)
    val color = varchar("COLOR", 255)
    val gender = varchar("GENDER", 255).nullable()
    val type = varchar("TYPE", 255)
    val season = varchar("SEASON", 255).nullable()

    override val primaryKey: PrimaryKey = PrimaryKey(id, name = "${tableName}_pk")
}