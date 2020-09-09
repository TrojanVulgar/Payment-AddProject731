package net.makemoney.android.data.models.withdrawal


data class WithdrawalInventoryCardItem(
     override val id: Int,
     val ud: Int,
     val title: String,
     val bgColor: Int,
     val logo: Int,
     val amount: Float
) : net.makemoney.android.data.models.withdrawal.WithdrawalItem()