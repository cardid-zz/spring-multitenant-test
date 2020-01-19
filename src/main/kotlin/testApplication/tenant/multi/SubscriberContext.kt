package testApplication.tenant.multi

import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.util.context.Context
import java.util.function.Function

@Component
object SubscriberContext {
    fun contextSubscriber(
        applyContext: Function<Context, Context>,
        exchange: ServerWebExchange,
        chain: WebFilterChain
    ): Mono<Void> {
        return chain
            .filter(exchange)
            .transformDeferred { function: Mono<Void?> ->
                function
                    .then(Mono.subscriberContext())
                    .flatMap {
                        val continuation = Mono.empty<Void>()
                        continuation
                    }
                    .subscriberContext { applyContext.apply(it) }
            }
    }
}