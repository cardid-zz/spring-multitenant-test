package testApplication.tenant.multi

import kotlinx.coroutines.ExecutorCoroutineDispatcher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.parameters.DefaultSecurityParameterNameDiscoverer
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.async.DeferredResult
import org.springframework.web.method.support.AsyncHandlerMethodReturnValueHandler
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.HandlerMethodReturnValueHandler
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.mvc.method.annotation.DeferredResultMethodReturnValueHandler
import java.lang.reflect.Method
import java.util.concurrent.Executors
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.reflect.jvm.kotlinFunction

object CoroutinesInRestController {

    @Configuration
    internal class CoroutinesInjection {
        @Bean
        @Primary
        fun parameterNameDiscovererWithCoroutines() =
            object: DefaultSecurityParameterNameDiscoverer(listOf()) {
                override fun getParameterNames(method: Method) =
                    ((super.getParameterNames(method)?.toList() ?: listOf()) +
                            if (method.isSuspend) listOf("__continuation__") else listOf()).toTypedArray()
            }
        @Bean
        fun coroutineContext() = object : ExecutorCoroutineDispatcher() {
            override val executor = Executors.newFixedThreadPool(128)

            override fun dispatch(context: CoroutineContext, block: Runnable) {
                val tenantId = TenantContext.getTenant()
                SecurityContextHolder.getContext()
                executor.execute {
                    TenantContext.set(tenantId)
                    block.run()
                }
            }

            override fun close() {
                executor.shutdown()
            }
        }
    }

    @Configuration
    internal class CoroutinesWebMvcConfigurer : WebMvcConfigurer {

        @Autowired
        private lateinit var coroutineContext: CoroutineContext

        override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
            resolvers.add(0, coroutineArgumentResolver(coroutineContext))
        }

        override fun addReturnValueHandlers(handlers: MutableList<HandlerMethodReturnValueHandler>) {
            handlers.add(0, returnValueHandler())
        }
    }

    private const val DEFERRED_RESULT = "deferred_result"

    private fun <T> isContinuationClass(clazz: Class<T>) = Continuation::class.java.isAssignableFrom(clazz)
    val Method?.isSuspend: Boolean get() = this?.kotlinFunction?.isSuspend ?: false

    fun coroutineArgumentResolver(coroutineContext: CoroutineContext) =
        object : HandlerMethodArgumentResolver {
            override fun supportsParameter(parameter: MethodParameter) =
                parameter.method.isSuspend && isContinuationClass(parameter.parameterType)

            override fun resolveArgument(parameter: MethodParameter, mavContainer: ModelAndViewContainer,
                                         webRequest: NativeWebRequest, binderFactory: WebDataBinderFactory) =
                object : Continuation<Any> {
                    val deferredResult = DeferredResult<Any>()

                    override val context: CoroutineContext
                        get() = coroutineContext

                    override fun resumeWith(result: Result<Any>) {
                        if (result.isSuccess) {
                            deferredResult.setResult(result.getOrNull())
                        } else {
                            deferredResult.setErrorResult(result.exceptionOrNull())
                        }
                    }
                }.apply {
                    mavContainer.model[DEFERRED_RESULT] = deferredResult
                }
        }

    fun returnValueHandler() =
        object: AsyncHandlerMethodReturnValueHandler {
            private val delegate = DeferredResultMethodReturnValueHandler()

            override fun supportsReturnType(returnType: MethodParameter): Boolean =
                returnType.method.isSuspend

            override fun handleReturnValue(returnValue: Any?, type: MethodParameter,
                                           mavContainer: ModelAndViewContainer, webRequest: NativeWebRequest) {
                val result = mavContainer.model[DEFERRED_RESULT] as DeferredResult<*>

                return delegate.handleReturnValue(result, type, mavContainer, webRequest)
            }

            override fun isAsyncReturnValue(returnValue: Any, returnType: MethodParameter): Boolean =
                returnValue === COROUTINE_SUSPENDED
        }
}