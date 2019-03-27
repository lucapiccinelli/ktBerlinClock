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
            "00:00:01" to "Y-0000-0000-00000000000-Y000"
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
        print(if(ss == 1) "Y-0000-0000-00000000000-Y000" else "0-0000-0000-00000000000-0000")
    }

}