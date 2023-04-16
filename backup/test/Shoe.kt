package no.miles.kotlindemo.test
import kotlinx.serialization.Serializable

@Serializable
sealed interface Shoe {
    val name: String
    val brand: String
    val color: String
    val gender: String?
    val shoeType: String
    val season: String?
}

@Serializable
class Sandal(
    override val name: String,
    override val brand: String,
    override val color: String,
    override val gender: String?) : Shoe {
    override val shoeType: String = "SANDAL"
    override val season: String = "SUMMMER"
}

@Serializable
class Sneaker(
    override val name: String,
    override val brand: String,
    override val color: String,
    override val gender: String?,
    override val season: String?) : Shoe {
    override val shoeType: String = "SNEAKER"
}

@Serializable
class Boot(
    override val name: String,
    override val brand: String,
    override val color: String,
    override val gender: String?,
    override val season: String?) : Shoe {
    override val shoeType: String = "BOOT"
}

fun createShoe(name: String, brand: String, color: String, gender: String?, type: String, season: String?) =
    when(type){
        "SNEAKER" -> Sneaker(
            name = name,
            brand = brand,
            color = color,
            gender = gender,
            season = season
        )
        "BOOT" -> Boot(
            name = name,
            brand = brand,
            color = color,
            gender = gender,
            season = season
        )
        "SANDAL" -> Sandal(
            name = name,
            brand = brand,
            color = color,
            gender = gender
        )
        else -> throw IllegalArgumentException("Not supported shoe type")
    }