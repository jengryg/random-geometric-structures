import ch.qos.logback.classic.Level
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory

const val OUTPUT_DIRECTORY = "figures"

fun main(args: Array<String>) {
    val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger
    rootLogger.level = Level.TRACE

    runBlocking {
        ExampleFigureGeneration.examplePoissonPointProcessStepConstruction()
    }
}