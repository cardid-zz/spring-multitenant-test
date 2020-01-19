package testApplication.tenant.filter

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.util.context.Context
import testApplication.tenant.multi.SubscriberContext
import testApplication.tenant.multi.TenantContext
import java.util.function.Function


@Configuration
class TenantFilter : WebFilter {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun filter(
        serverWebExchange: ServerWebExchange,
        webFilterChain: WebFilterChain
    ): Mono<Void> {

        if (!serverWebExchange.request.headers.containsKey(TenantContext.HEADER)) {
            return webFilterChain.filter(serverWebExchange)
        }

        val tenant = serverWebExchange.request.headers[TenantContext.HEADER]?.get(0)!!

        logger.debug("[d] currentThread =  ${Thread.currentThread()}")

        return SubscriberContext.contextSubscriber(
            applyContext = Function { context: Context ->
                if (!context.hasKey(TenantContext::class.java)) {
                    context.put(TenantContext::class.java, TenantContext(tenant))
                } else {
                    context
                }
            }
            , chain = webFilterChain, exchange = serverWebExchange)
    }
}
