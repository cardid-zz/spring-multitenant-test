package testApplication.restservice


import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import testApplication.resservice.SomeService

@RestController
class RestController(
    private val service: SomeService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/working")
    suspend fun working(@RequestParam("param") param : Int) : ResponseEntity<*> = coroutineScope(){
        val res = async{ service.doSomething(param)}
        return@coroutineScope ResponseEntity.ok(res.await())
    }

    @PostMapping("/failed")
    suspend fun failed(@RequestBody body: BodyParam) : ResponseEntity<*> = coroutineScope(){
        logger.debug("[d] ${body.toString()}")
        val res = async { service.doSomething(body.value) }

        return@coroutineScope ResponseEntity.ok(res.await())
    }
}

