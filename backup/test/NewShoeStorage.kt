package no.miles.kw

import no.miles.kotlindemo.test.Boot
import no.miles.kotlindemo.test.Sandal
import no.miles.kotlindemo.test.Shoe
import no.miles.kotlindemo.test.Sneaker

class NewShoeStorage {

    fun getShoeSizes(shoes: List<Shoe>): Map<Shoe, List<Pair<Int, Int>>> = shoes.map { getSizes(it) }.toMap()

    private fun getSizes(shoe: Shoe): Pair<Shoe, List<Pair<Int, Int>>> =
        when(shoe){
            is Sneaker -> Pair(shoe, IntRange(40, 45).map {Pair(it, getRandomNumber()) })
            is Boot -> Pair(shoe, IntRange(40, 45).map {Pair(it, getRandomNumber()) })
            is Sandal -> Pair(shoe, IntRange(40, 45).map {Pair(it, getRandomNumber()) })
        }
    
    private fun getRandomNumber(): Int {
        val min = 0
        val max = 5
        return (Math.random() * (max - min) + min).toInt()
    }
}