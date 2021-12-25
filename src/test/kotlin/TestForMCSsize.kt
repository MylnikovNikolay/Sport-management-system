import TestCompetitions.Companion.testGeneration
import ru.emkn.kotlin.sms.*
import java.io.File
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.test.*

class TestForMCSsize {

    @Test
    fun test1() {
        assertEquals(2, listOf("fr", "fr").MCSsize(listOf("fr", "fr")))
        assertEquals(0, listOf("fr", "fr").MCSsize(listOf("frau", "frau")))
        assertEquals(1, listOf("frau", "fr").MCSsize(listOf("fr", "frau")))
    }

    @Test
    fun test2() {
        val list1 = (0..999).toList()
        val list2 = (500..1499).toList()
        assertEquals(500, list1.MCSsize(list2))
    }

    @Test
    fun test3() {
        val list1 = (0..999).toList()
        val list2 = (0..999).toList().map{1000 - it}
        assertEquals(1, list1.MCSsize(list2))
    }
}