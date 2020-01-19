package testApplication.tenant.master.util

import kotlinx.coroutines.Dispatchers
import org.springframework.stereotype.Component
import kotlin.coroutines.CoroutineContext


@Component
object TenantDispatcher{
    val Default: CoroutineContext get() = Dispatchers.Default//+ TenantContext.asContextElement()
    val Main: CoroutineContext get() = Dispatchers.Main //+ TenantContext.asContextElement()
    val IO: CoroutineContext get() = Dispatchers.IO //+ TenantContext.asContextElement()
}