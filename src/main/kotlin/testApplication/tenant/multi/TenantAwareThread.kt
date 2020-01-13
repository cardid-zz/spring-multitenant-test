package testApplication.tenant.multi

class TenantAwareThread(val target: Runnable?, private val tenantContext: TenantContext) : Thread(target) {
    private var tenant: String = TenantContext.DEFAULT

    override fun run() {
        tenantContext.set(tenant)
        super.run()
        tenantContext.remove()
    }

    init {
        tenant = tenantContext.getTenant()
    }
}