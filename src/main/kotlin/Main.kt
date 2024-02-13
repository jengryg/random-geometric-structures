import ch.qos.logback.classic.Level
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    setLoggingLevel(Level.TRACE)

    runBlocking {
        ExampleFigureGeneration.poissonPointProcessStepConstruction()
        ExampleFigureGeneration.vietorisRipsComplexStepConstruction()
    }
}