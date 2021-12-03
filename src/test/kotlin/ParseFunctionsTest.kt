import exceptions.CSVFieldNamesException
import exceptions.CSVStringWithNameException
import exceptions.ProblemWithCSVException
import ru.emkn.kotlin.sms.*
import java.io.File
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class ParseEventTest {

    @Test
    fun `correct input test (event)`() {
        val path = "src/test/resources/events-test/correctEvent.csv"
        val tmp = File(path).copyTo(File("src/test/resources/events-test/event.csv"), true)
        val configurationFolder = File("src/test/resources/events-test").walk().toList()
        assertEquals(
            NameDate("Mad Wave Classic", LocalDate.parse("20.11.2021", formatter)),
            getNameAndDate(configurationFolder, path)
        )
        tmp.delete()
    }

    @Test
    fun `incorrect header (event)`() {
        val path = "src/test/resources/events-test/incorrectHeader.csv"
        val tmp = File(path).copyTo(File("src/test/resources/events-test/event.csv"), true)
        val configurationFolder = File("src/test/resources/events-test").walk().toList()
        assertFailsWith<CSVStringWithNameException> { getNameAndDate(configurationFolder, path) }
        tmp.delete()
    }

    @Test
    fun `incorrect header two (event)`() {
        val path = "src/test/resources/events-test/incorrectHeaderOther.csv"
        val tmp = File(path).copyTo(File("src/test/resources/events-test/event.csv"), true)
        val configurationFolder = File("src/test/resources/events-test").walk().toList()
        assertFailsWith<CSVStringWithNameException> { getNameAndDate(configurationFolder, path) }
        tmp.delete()
    }

    @Test
    fun `many strings (event)`() {
        val path = "src/test/resources/events-test/tooManyStrings.csv"
        val tmp = File(path).copyTo(File("src/test/resources/events-test/event.csv"), true)
        val configurationFolder = File("src/test/resources/events-test").walk().toList()
        assertFailsWith<ProblemWithCSVException> { getNameAndDate(configurationFolder, path) }
        tmp.delete()
    }

    @Test
    fun `not enough strings (event)`() {
        val path = "src/test/resources/events-test/notEnoughStrings.csv"
        val tmp = File(path).copyTo(File("src/test/resources/events-test/event.csv"), true)
        val configurationFolder = File("src/test/resources/events-test").walk().toList()
        assertFailsWith<ProblemWithCSVException> { getNameAndDate(configurationFolder, path) }
        tmp.delete()
    }

    @Test
    fun `empty csv (event)`() {
        val path = "src/test/resources/events-test/empty.csv"
        val tmp = File(path).copyTo(File("src/test/resources/events-test/event.csv"), true)
        val configurationFolder = File("src/test/resources/events-test").walk().toList()
        assertFailsWith<ProblemWithCSVException> { getNameAndDate(configurationFolder, path) }
        tmp.delete()
    }
}


internal class ParseCollectiveTest {
    @Test
    fun `correct input test (participant)`() {
        val path = "src/test/resources/participant-test/correctParticipant.csv"
        assertEquals(
            listOf((Participant("М12", Sex.MALE, "АВРАМЕНКО", "ДАНИИЛ", 2009, "3р"))).toString(),
            Collective(path).collectiveParser(path).toString()
        )
    }

    @Test
    fun `incorrect header (participant)`() {
        val path = "src/test/resources/participant-test/incorrectHeader.csv"
        assertFailsWith<CSVFieldNamesException> { Collective(path).collectiveParser(path) }
    }


    @Test
    fun `empty csv (participant)`() {
        val path = "src/test/resources/participant-test/empty.csv"
        assertFailsWith<CSVStringWithNameException> { Collective(path).collectiveParser(path) }
    }

    @Test
    fun `not enough strings (participant)`() {
        val path = "src/test/resources/participant-test/notEnoughStrings.csv"
        assertFailsWith<ProblemWithCSVException> { Collective(path).collectiveParser(path) }
    }

    @Test
    fun `correct input test (multiple participants)`() {
        val answer = listOf(
            Participant("М09", Sex.MALE, "Белов", "Станислав", 2009, ""),
            Participant("Ж12", Sex.FEMALE, "Треглазова", "Виталия", 2012, "2р"),
            Participant("Ж10", Sex.FEMALE, "Романенко", "Варвара", 2010, "")
        )
        val path = "src/test/resources/participant-test/correctParticipants.csv"
        assertEquals(
            answer.toString(), Collective(path).collectiveParser(path).toString()
        )
    }
}

internal class ParseDistanceTest {
    @Test
    fun `correct input test (distances)`() {
        val path = "src/test/resources/distances-test/correctDistance.csv"
        val configFile = mapOf(
            Pair("Название", "D4000"),
            Pair("1", "100"),
            Pair("2", "200"),
            Pair("3", "300"),
            Pair("4", "400"),
            Pair("5", "500")
        )
        assertEquals(
            mapOf(Pair("D500", Distance(configFile, path, mutableSetOf()))).toString(),
            distancesParser(File(path), mutableSetOf()).toString()
        )
    }

    @Test
    fun `incorrect header (distances)`() {
        val path = "src/test/resources/distances-test/incorrectHeader.csv"
        assertFailsWith<CSVFieldNamesException> { distancesParser(File(path), mutableSetOf()) }
    }
}

internal class ParseGroupTest {
    @Test
    fun `correct input test (groups)`() {
        val configFile = mapOf(
            Pair("Название", "D4000"),
            Pair("1", "100"),
            Pair("2", "200"),
            Pair("3", "300"),
            Pair("4", "400"),
            Pair("5", "500")
        )
        val path = "src/test/resources/group-test/correctGroup.csv"
        val distancePath = "src/test/resources/distances-test/correctDistance.csv"
        val answer = listOf(Group("М0304", Distance(configFile, distancePath, mutableSetOf())))
        assertEquals(
            answer.toString(),
            groupsParser(distancesParser(File(distancePath), mutableSetOf()), File(path), Phase.FIRST).toString()
        )
    }

    @Test
    fun `incorrect header input (groups)`() {
        val path = "src/test/resources/group-test/incorrectHeader.csv"
        val distancePath = "src/test/resources/distances-test/correctDistance.csv"
        assertFailsWith<CSVFieldNamesException> { groupsParser(distancesParser(File(distancePath), mutableSetOf()), File(path), Phase.FIRST).toString() }
    }
}