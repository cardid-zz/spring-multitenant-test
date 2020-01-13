package testApplication.tenant.filter

import testApplication.tenant.multi.TenantContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono


@Component
class TenantFilter (
    private val tenantContext: TenantContext
) : WebFilter {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val tenantHeader = "tenant"

    override fun filter(
        serverWebExchange: ServerWebExchange,
        webFilterChain: WebFilterChain
    ): Mono<Void> {
        val tenant = serverWebExchange.request.headers[tenantHeader]


        if (tenant.isNullOrEmpty()) {
            setTenant(TenantContext.DEFAULT)
        } else {
            setTenant(tenant.first())
        }
        logger.debug("[d] currentThread =  ${Thread.currentThread()}")
        return webFilterChain.filter(serverWebExchange)
    }

    private fun setTenant(tenant: String) {
        try {
            tenantContext.set(tenant)
        } catch (e: Exception) {
            throw RuntimeException()
        }
    }
}
