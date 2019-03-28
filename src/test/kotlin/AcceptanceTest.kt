import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class AcceptanceTest{
    private lateinit var myOut: ByteArrayOutputStream

    fun setup(){
        myOut = ByteArrayOutputStream()
        System.setOut(PrintStream(myOut))
    }

    fun teardown(){
        myOut.close()
    }

    inline fun eachTest(codeBlock: () -> Unit){
        try {
            setup()
            codeBlock()
        }finally {
            teardown()
        }
    }

    @TestFactory
    fun `given an hour in hh,mm,ss form, give me back berlin clock configuration`() = listOf(
            "00:00:00" to "0-0000-0000-00000000000-0000",
            "00:00:01" to "Y-0000-0000-00000000000-0000",
            "00:00:02" to "0-0000-0000-00000000000-0000",
            "00:00:03" to "Y-0000-0000-00000000000-0000",
            "00:01:00" to "0-0000-0000-00000000000-Y000",
            "00:02:00" to "0-0000-0000-00000000000-YY00",
            "00:03:00" to "0-0000-0000-00000000000-YYY0",
            "00:04:00" to "0-0000-0000-00000000000-YYYY",
            "00:05:00" to "0-0000-0000-Y0000000000-0000",
            "00:10:00" to "0-0000-0000-YY000000000-0000",
            "00:15:00" to "0-0000-0000-YYR00000000-0000",
            "00:20:00" to "0-0000-0000-YYRY0000000-0000",
            "00:30:00" to "0-0000-0000-YYRYYR00000-0000",
            "00:55:00" to "0-0000-0000-YYRYYRYYRYY-0000",
            "00:59:01" to "Y-0000-0000-YYRYYRYYRYY-YYYY",
            "01:00:00" to "0-0000-R000-00000000000-0000",
            "02:00:00" to "0-0000-RR00-00000000000-0000",
            "03:00:00" to "0-0000-RRR0-00000000000-0000",
            "04:00:00" to "0-0000-RRRR-00000000000-0000",
            "05:00:00" to "0-R000-0000-00000000000-0000",
            "10:00:00" to "0-RR00-0000-00000000000-0000",
            "15:00:00" to "0-RRR0-0000-00000000000-0000",
            "20:00:00" to "0-RRRR-0000-00000000000-0000",
            "23:59:59" to "Y-RRRR-RRR0-YYRYYRYYRYY-YYYY",
            "09:23:24" to "0-R000-RRRR-YYRY0000000-YYY0"
    ).map{ (input, expected) ->
        DynamicTest.dynamicTest("given $input i expect $expected"){
            eachTest {
                berlinClockConfiguration(arrayOf(input))
                Assertions.assertEquals(expected, myOut.toString())
            }
        }
    }

    fun berlinClockConfiguration(args: Array<String>) {
        val (hh, mm, ss) = args[0].split(":").map(String::toInt)
        val roundLamp = if(ss % 2 == 1) "Y" else "0"
        val minutesLamp = (mm % 5)
        val fiveMinutesLamp = (mm / 5)
        val hoursLamp = (hh % 5)
        val fiveHoursLamp = (hh / 5)

        val minutesString = "${(0 until minutesLamp).joinToString("") { "Y" }}${(0 until 4 - minutesLamp).joinToString("") { "0" }}"
        val fiveMinutesString = "${(1..fiveMinutesLamp).joinToString("") { if(it % 3 != 0) "Y" else "R"}}${(0 until 11 - fiveMinutesLamp).joinToString("") { "0" }}"
        val hoursString = "${(0 until hoursLamp).joinToString("") { "R" }}${(0 until 4 - hoursLamp).joinToString("") { "0" }}"
        val fiveHoursString = "${(0 until fiveHoursLamp).joinToString("") { "R" }}${(0 until 4 - fiveHoursLamp).joinToString("") { "0" }}"

        print("$roundLamp-$fiveHoursString-$hoursString-$fiveMinutesString-$minutesString")
    }

}