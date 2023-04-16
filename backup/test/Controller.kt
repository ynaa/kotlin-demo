package no.miles.kw
/*
import com.google.gson.GsonBuilder
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import no.miles.kotlindemo.test.*
import no.miles.kw.db.*

class Controller(val dao: ShoeTableDao) {

    private val gson = GsonBuilder().setPrettyPrinting().create()

    suspend fun helloWorld(call: ApplicationCall){
        return call.respondText(toJson("Its working"))
    }
    suspend fun query(call: ApplicationCall){
        val shoes = queryDb(call)
        return call.respondText(toJson(shoes))
    }


    private fun toJson(theObjet: Any) = gson.toJson(theObjet)

    private fun convertJson(jsonString: String) =
        gson.fromJson<Map<String, String>>(jsonString, MutableMap::class.java)

    suspend fun addShoe(call: ApplicationCall) {
        val params = call.receive<String>()
        val paramters = convertJson(params)
        val name: String? = paramters["name"]
        val brand: String? = paramters["brand"]
        val type: String? = paramters["type"]
        val color: String? = paramters["color"]

        requireNotNull(name) { "Navn er påkrevd" }
        requireNotNull(brand) { "Brand er påkrevd" }
        requireNotNull(type) { "Type er påkrevd" }
        requireNotNull(color) { "Farge er påkrevd" }

        dao.insert(
            createShoe(
                name = name,
                brand = brand,
                type = type,
                color = color,
                gender = paramters["gender"],
                season = paramters["season"]
            )
        )
        return call.respondText("Shoe stored correctly", status = HttpStatusCode.Created)
    }

    suspend fun queryBySize(call: ApplicationCall){
        val shoeStorage = NewShoeStorage()
        val shoes = queryDb(call)
        val sizeQuery = call.request.queryParameters["size"]

        val shoeSizes = shoeStorage.getShoeSizes(shoes)

        val shoesBySizes =
            shoes.mapNotNull { shoe ->
                val sizes = shoeSizes[shoe]!!
                val availableSizes = sizes.filter { pair ->
                    sizeQuery?.let { pair.first == it.toInt() } ?: true
                            && pair.second > 0
                }
                if (availableSizes.isEmpty()) null else shoe.toUiShoe(availableSizes)
            }
        return call.respondText(gson.toJson(shoesBySizes))
    }

    private fun queryDb(call: ApplicationCall): List<Shoe> {
        val brandQuery = call.request.queryParameters["brand"]
        val typeQuery = call.request.queryParameters["type"]
        val genderQuery = call.request.queryParameters["gender"]
        val seasonQuery = call.request.queryParameters["season"]
        val shoes = dao.get()
            .filter { shoe -> brandQuery?.let { shoe.brand == it } ?: true }
            .filter { shoe ->
                typeQuery?.let {
                    when(it){
                        "Sneaker" -> shoe is Sneaker
                        "Boot" -> shoe is Boot
                        "Sandal" -> shoe is Sandal
                        else -> throw IllegalStateException("Unknown shoe type")
                    }
                } ?: true }
            .filter { shoe -> genderQuery?.let { shoe.gender == it } ?: true }
            .filter { shoe -> seasonQuery?.let { shoe.season == it } ?: true }
        return shoes
    }
}


fun Shoe.toUiShoe(sizes: List<Pair<Int, Int>>) =
    UIShoe(this, sizes)


data class UIShoe(val shoe: Shoe, val sizes: List<Pair<Int, Int>>)

 */
