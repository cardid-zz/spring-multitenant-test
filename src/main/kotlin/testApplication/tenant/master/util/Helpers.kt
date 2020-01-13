package testApplication.tenant.master.util


fun rootCause(t: Throwable): Throwable {
    val cause = t.cause
    return if (cause != null && cause !== t) {
        rootCause(cause)
    } else t
}