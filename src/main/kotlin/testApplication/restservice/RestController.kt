package testApplication.restservice


import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import testApplication.resservice.SomeService
import kotlin.coroutines.CoroutineContext

@RestController
class RestController(
    private val service: SomeService,
    private val coroutineContext: CoroutineContext
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/working")
    suspend fun working(@RequestParam("param") param : Int) : ResponseEntity<*> = withContext(coroutineContext){
        val res = async{ service.doSomething(param)}
        return@withContext ResponseEntity.ok(res)
    }

    @PostMapping("/failed")
    suspend fun failed(@RequestBody body: BodyParam) : ResponseEntity<*> = withContext(coroutineContext){
        logger.debug("[d] ${body.toString()}")
        val res = async { service.doSomething(body.value) }

        return@withContext ResponseEntity.ok(res)
    }
}

