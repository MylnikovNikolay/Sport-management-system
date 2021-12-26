import ru.emkn.kotlin.sms.*
import kotlin.test.*

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList

class ExperimentingTests {
    @Test
    fun test1() {
        assertNotEquals(ControlPoint("34").hashCode(), ControlPoint("34").hashCode())
    }

    @Test
    fun test2() {

        assertContains(MSL<String>(mutableStateListOf("azaz")), "azaz")

        val msl = MSL<Int>()
        msl.add(2)
        msl.add(4)
        msl.add(5)
        msl.remove(4)
        msl.remove(7)
        assertContains(msl, 2)
        assertContains(msl, 5)
        assertEquals(msl.size, 2)

        assertEquals(msl.filter {it % 2 == 1}.size, 1)
    }
}