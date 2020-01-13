package testApplication.tenant.multi

import kotlinx.coroutines.ThreadContextElement
import kotlinx.coroutines.asContextElement
import okhttp3.internal.http.RequestLine
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Component
object TenantContext {
    const val DEFAULT: String = "default"

    private val logger = LoggerFactory.getLogger(javaClass)

    private val currentTenant = InheritableThreadLocal<String?>()

    fun getTenant() : String {
        return currentTenant.get() ?: DEFAULT
    }
    fun set(tenantId : String) {
        currentTenant.set(tenantId)
    }
    fun remove() {
        currentTenant.remove()
    }

    fun asContextElement(): ThreadContextElement<String?> {
        logger.debug("[d] asContextElement ${getTenant()}")
        return currentTenant.asContextElement(getTenant())
    }
}