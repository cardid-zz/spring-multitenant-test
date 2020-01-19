package testApplication.tenant.multi

import org.slf4j.LoggerFactory

class TenantContext(private val tenantId: String) : ObjectContext  {
    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        const val HEADER: String = "tenant"
    }

    override fun getValue(): String {

        return tenantId.also {
            logger.debug("[d] tenant = $it")
        }
    }

    override fun getHeader(): String {
        return HEADER
    }
}