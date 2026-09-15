package me.riafy.finlog.data.models

data class PaymentMethod(
    val id: Long,
    val name: String,
    val sortOrder: Long,
    val isDefault: Boolean
)

object DefaultPaymentMethods {
    val names = listOf("Cash", "UPI", "Credit Card", "Debit Card", "Bank Transfer", "Other")
}
