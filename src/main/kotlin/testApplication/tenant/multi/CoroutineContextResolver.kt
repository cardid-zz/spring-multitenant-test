//package testApplication.tenant.multi
//
//import kotlinx.coroutines.Dispatchers.Main
//import kotlinx.coroutines.Dispatchers.Unconfined
//import kotlin.coroutines.*
//
//const val SECURED_COMMON_POOL = "SecuredCommonPool"
//const val SECURED_UNCONFINED = "SecuredUnconfined"
//
//internal open class CoroutineContextResolver(
//    private val dispatcher: ContinuationInterceptor
//): AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor {
//    override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> =
//        dispatcher.interceptContinuation(Wrapper(continuation))
//
//    private var tenantId = TenantContext.getTenant()
//
//    inner class Wrapper<T>(private val continuation: Continuation<T>) : Continuation<T> {
//        private inline fun wrap(block: () -> Unit) {
//            try {
//                TenantContext.set(tenantId)
//                block()
//            } finally {
//                tenantId = TenantContext.DEFAULT
//            }
//        }
//
//        override val context: CoroutineContext get() = continuation.context
//        override fun resume(value: T) = wrap { continuation.resume(value) }
//        override fun resumeWithException(exception: Throwable) = wrap { continuation.resumeWithException(exception) }
//    }
//
//    internal open class SpringSecurityCoroutineContextResolver: CoroutineContextResolver {
//        override fun resolveContext(beanName: String, bean: Any?): CoroutineContext? = when(beanName) {
//            SECURED_COMMON_POOL -> CoroutineContextResolver(Main)
//            SECURED_UNCONFINED -> CoroutineContextResolver(Unconfined)
//            else -> bean as? CoroutineContext
//        }
//    }
