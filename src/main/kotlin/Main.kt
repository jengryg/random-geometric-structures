import ch.qos.logback.classic.Level
import kotlinx.coroutines.runBlocking

const val OUTPUT_DIRECTORY = "figures"

fun main(args: Array<String>) {
    setLoggingLevel(Level.TRACE)

    runBlocking {
        ExampleFigureGeneration.poissonPointProcessStepConstruction()
        ExampleFigureGeneration.vietorisRipsComplexStepConstruction()
    }
}