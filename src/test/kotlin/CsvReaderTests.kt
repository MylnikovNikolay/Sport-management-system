
import ru.emkn.kotlin.sms.CsvReader
import kotlin.test.*

class CsvReaderTests {
    @Test
    fun oneLineTests(){
        assertContentEquals(listOf(""), CsvReader.readOneLine(""))
        assertContentEquals(listOf("aboba"), CsvReader.readOneLine("aboba"))
        assertContentEquals(listOf("aboba", "aboba"), CsvReader.readOneLine("aboba,aboba"))
        assertContentEquals(listOf("", "aboba", ""), CsvReader.readOneLine(",aboba,"))
        assertContentEquals(listOf("", "", "", ""), CsvReader.readOneLine(",,,"))
        assertFailsWith<AssertionError> {
            CsvReader.readOneLine(",,,\n,,,")
        }
    }

    @Test
    fun oneLineTestsWithSpaces() {
        assertContentEquals(listOf(""), CsvReader.readOneLine("         "))
        assertContentEquals(listOf("aboba"), CsvReader.readOneLine(" aboba      "))
        assertContentEquals(listOf("aboba", "abo ba"), CsvReader.readOneLine("   aboba , abo  ba"))
        assertContentEquals(listOf("", "abo ba", ""), CsvReader.readOneLine("    , abo ba  , "))
        assertContentEquals(listOf("", "", "", ""), CsvReader.readOneLine("   ,   ,,   "))
    }

    @Test
    fun oneLineTestsWithQuotes() {
        assertContentEquals(listOf(""), CsvReader.readOneLine("\"\""))
        assertContentEquals(listOf("aboba"), CsvReader.readOneLine("\"aboba\""))
        assertContentEquals(listOf("aboba", "aboba"), CsvReader.readOneLine("\"aboba\",\"aboba\""))
        assertContentEquals(listOf("", "aboba", ""), CsvReader.readOneLine("\"\",aboba,\"\""))
        assertContentEquals(listOf("\"aboba,aboba\""), CsvReader.readOneLine("\"aboba,aboba\""))
        assertContentEquals(listOf("\"aboba,aboba\"", "aboba"), CsvReader.readOneLine("\"aboba,aboba\", \"aboba\""))
        assertContentEquals(listOf("\",aboba,\""), CsvReader.readOneLine("\",aboba,\""))
        assertContentEquals(listOf("aboba","\",,,,,,,\"", "aboba"), CsvReader.readOneLine("aboba,\",,,,,,,\",aboba"))
    }

    @Test
    fun oneLineTestsWithQuotesAndSpaces() {
        assertContentEquals(
            listOf("aboba", "aboba", "aboba", "aboba"),
            CsvReader.readOneLine(" aboba   , aboba, \"aboba \" , \" aboba \"")
        )
        assertContentEquals(
            listOf("a b o b a", "aboba", "\"aboba, aboba\"", "aboba"),
            CsvReader.readOneLine(" a    b  o  b   a   , aboba, \"  aboba,    aboba \" , \" aboba \"      ")
        )
        assertContentEquals(
            listOf("a b o b a", "", "aboba","", "\"aboba, aboba\"", "aboba"),
            CsvReader.readOneLine(" a    b  o  b   a   ,, aboba,   \"        \"   , \"  aboba,    aboba \" , \" aboba \"      ")
        )
    }

    @Test
    fun nullOneLineTests() {
        assertNull(
            CsvReader.readOneLine("""
                "aboba, aboba", "
            """.trimIndent())
        )
        assertNull(
            CsvReader.readOneLine("""
                "aboba, aboba", ""aboba
            """.trimIndent())
        )
        assertNull(
            CsvReader.readOneLine("""
                aboba   "aboba, aboba", ""
            """.trimIndent())
        )
        assertNull(
            CsvReader.readOneLine("""
                "aboba"    "aboba"
            """.trimIndent())
        )
        assertNull(
            CsvReader.readOneLine("""
                "aboba",    "aboba"    "aboba, aboba", "aboba"
            """.trimIndent())
        )
        assertNull(
            CsvReader.readOneLine("""
                "abo""ba",    "aboba"
            """.trimIndent())
        )
    }

    @Test
    fun readTest() {
        val string = """
            aboba 1, aboba 2, "aboba 3", " aboba 4 "
            aboba 5, aboba 6  , "aboba  7",  aboba 8 
            aboba 9, aboba 10, "aboba 11", " aboba 12 " 
                aboba 13 , aboba 14 , aboba 15, aboba 16            
        """.trimIndent()
        val list = CsvReader.read(string)!!
        var counter = 0
        for (i in 0..3)
            for(j in 0..3) {
                counter++
                assertEquals("aboba $counter", list[i][j])

            }
    }

    @Test
    fun readNullTests() {
        var string = """
            aboba, "aboba          
        """.trimIndent()
        assertNull(CsvReader.read(string))

        string = """
            aboba, "aboba"
            aboba, aboba,
        """.trimIndent()
        assertNull(CsvReader.read(string))

        string = """
            ,,,,
            ,,,,
            ,,,
            ,,,,,
        """.trimIndent()
        assertNull(CsvReader.read(string))

        string = """
            ,,,,
            ,,,,
            
            ,,,,
        """.trimIndent()
        assertNull(CsvReader.read(string))

        string = """
            ,,,,
            ,,",,"
            ,,,,
        """.trimIndent()
        assertNull(CsvReader.read(string))
    }

    @Test
    fun readWithHeaderTest() {
        val string = """
            %4 == 1, %4 == 2, %4 == 3, %4 == 0
            1, 2, 3, 4
            5, 6, 7, 8
            9, 10, 11, 12
        """.trimIndent()
        val listOfMaps = CsvReader.readWithHeader(string)
        assertEquals(2.toString(), listOfMaps!![0]["%4 == 2"])
        assertEquals(11.toString(), listOfMaps!![2]["%4 == 3"])
        assertEquals(8.toString(), listOfMaps!![1]["%4 == 0"])
    }
}