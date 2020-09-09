package net.makemoney.android.data.responses

data class WithdrawalNewCardsResponse(
        val methods: List<net.makemoney.android.data.responses.WithdrawalNewCardsResponse.WithdrawalMethods>,
        val nominals: List<Float>,
        val rate: Float
) {
    data class WithdrawalMethods(
            val id: Int,
            val title: String
    )
}
