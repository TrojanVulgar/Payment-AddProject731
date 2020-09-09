package net.makemoney.android.data.models.withdrawal


data class WithdrawalNewCardItem(
        override val id: Int,
        val title: String,
        val bgColor: Int,
        val logo: Int
) : net.makemoney.android.data.models.withdrawal.WithdrawalItem()