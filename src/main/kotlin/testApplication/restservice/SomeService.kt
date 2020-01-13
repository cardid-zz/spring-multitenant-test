package testApplication.resservice

import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import testApplication.tenant.master.util.TenantDispatcher
import testApplication.tenant.multi.TenantContext
import java.math.BigDecimal

@Service
class SomeService {
    private val logger = LoggerFactory.getLogger(javaClass)

    suspend fun doSomething(param: Int): String = withContext(TenantDispatcher.Default) {
        logger.debug("[d] doSomething currentThread =  ${Thread.currentThread()} currentTenant = ${TenantContext.getTenant()}")
        delay(500)
        return@withContext "param = ${param} and tenant was = ${TenantContext.getTenant()}"
    }
}