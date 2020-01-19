package testApplication.resservice

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.util.context.Context
import testApplication.tenant.multi.TenantContext

@Service
class SomeService {
    private val logger = LoggerFactory.getLogger(javaClass)

    suspend fun doSomething(param : Int): Flux<String?> {
        val entities: Flux<String> = listOf(param.toString()).toFlux<String>()
        return entities.flatMap<String> { ad ->
            Mono.subscriberContext().map<String> { context: Context ->
                val tenantContext = context.get(TenantContext::class.java)
                ad + "tenant = ${tenantContext.getValue()}".also {
                    logger.debug("[d] tenant = ${tenantContext.getValue()}")
                }
            }
        }
    }
}

