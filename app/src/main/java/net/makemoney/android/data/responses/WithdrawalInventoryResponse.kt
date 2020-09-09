package net.makemoney.android.data.responses


data class WithdrawalInventoryResponse(
        val method_id: Int,
        val method_name: String,
        val ud: Int,
        val amount: Float
)