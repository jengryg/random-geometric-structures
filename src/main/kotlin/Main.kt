import ch.qos.logback.classic.Level
import figures.PoissonPointProcessStepConstruction
import figures.VietorisRipsComplexStepConstruction
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    setLoggingLevel(Level.TRACE)

    runBlocking {
        PoissonPointProcessStepConstruction.example()
        VietorisRipsComplexStepConstruction.example()
    }
}