package com.pulsereality.shema.data.model

data class SubscriptionPlan(
    val id: String,
    val title: String,
    val price: String,
    val period: String,
    val tag: String? = null,
    val subtitle: String,
    val isBestValue: Boolean = false
) {
    companion object {
        val PLANS = listOf(
            SubscriptionPlan(
                id = "com.shema.premium.yearly",
                title = "Yearly Access",
                price = "$29.99",
                period = "/ year",
                tag = "SAVE 50% • BEST VALUE",
                subtitle = "Only $2.50/month, billed annually. Includes 7-day free trial.",
                isBestValue = true
            ),
            SubscriptionPlan(
                id = "com.shema.premium.monthly",
                title = "Monthly Access",
                price = "$4.99",
                period = "/ month",
                subtitle = "Flexible monthly subscription, cancel anytime."
            ),
            SubscriptionPlan(
                id = "com.shema.premium.weekly",
                title = "Weekly Access",
                price = "$1.99",
                period = "/ week",
                subtitle = "Billed weekly, cancel anytime."
            )
        )
    }
}
