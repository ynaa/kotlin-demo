package no.miles.kw
/*
import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import createShoe
import no.miles.kw.db.*
import org.h2.jdbcx.JdbcDataSource
import org.jetbrains.exposed.sql.Database
import javax.sql.DataSource

const val fileName = "/shoes1.txt"

fun main() {
    val server = Services()
    server.nettyServer.start(wait = true)
}

class Services {

    private val db = Db()
    private val controller: Controller = Controller(db.dao)

    init {
        db.insertData()
    }

    val nettyServer = embeddedServer(Netty, port = 8080) {
        routing {
            get("/") {
                controller.helloWorld(call)
            }
            get("/shoes") {
                controller.query(call)
            }
            get("/sizes") {
                controller.queryBySize(call)
            }
            post("/shoes") {
                controller.addShoe(call)
            }
        }
    }
}

class Db {

    private val datasource: DataSource = createH2Datasource()
    val dao = ShoeTableDao(datasource)

    init {
        dao.createTable()
        val connect = Database.connect(datasource)
        println("Connected to $connect")
    }

    fun insertData(){
        println("Gonna insert data")
        val fileContent = Db::class.java.getResourceAsStream(fileName).bufferedReader().readLines()
        fileContent
            .filter { !it.startsWith("#") }
            .forEach{
                val splitted = it.split(";")
                println(splitted)
                val shoe = createShoe(
                    name = splitted[0].trim(),
                    brand = splitted[1].trim(),
                    color = splitted[3].trim(),
                    gender = splitted[4].trim(),
                    season = splitted[5].trim(),
                    type = splitted[2].trim()
                )
                dao.insert(shoe)
        }
    }

    private fun createH2Datasource() =
        JdbcDataSource().apply {
            setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
            user = "sa"
            password = "sa"
        }
}

 */