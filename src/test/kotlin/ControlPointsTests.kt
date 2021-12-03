import ru.emkn.kotlin.sms.*
import java.io.File
import kotlin.test.*

internal class ControlPointsTests {

    /*
    @Test
    fun cp1test() {
        val cp1 = ControlPoint(
            pathname = "sample-data/additional sample-data/cp-1.csv",
            inputDistance = Distance("unknown")
        )
        assertEquals("unknown", cp1.distance.name)
        assertEquals(3, cp1.info.size)
        assertEquals("1km", cp1.name)
        assert(243 in cp1.info.keys)
    }

    @Test
    fun exceptionTest() {
        val cp2 = ControlPoint(name = "km2", Distance("unknown"))
        try {
            cp2.dataFromProtocol(File("sample-data/additional sample-data/cp-1.csv").readText())
        } catch (e: AssertionError) {
            assertEquals("Имя КП не соответствует КП", e.message)
        }

        try {
            val cp3 = ControlPoint(pathname = "sample-data/additional_sample-data/cp-1.csv", Distance("unknown"))
        } catch (e: AssertionError) {
            assertEquals("Файла не существует либо у него не csv-расширение", e.message)
        }
    }

*/
}