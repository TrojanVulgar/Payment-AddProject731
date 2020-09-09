package net.makemoney.android.data.models.withdrawal;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@IntDef({WithdrawalCardType.WEBMONEY, WithdrawalCardType.YANDEX,
        WithdrawalCardType.QIWI, WithdrawalCardType.MTS,
        WithdrawalCardType.TELE2, WithdrawalCardType.MEGAFON,
        WithdrawalCardType.BEELINE, WithdrawalCardType.PAYPAL,
        WithdrawalCardType.WOT, WithdrawalCardType.WARFACE,
        WithdrawalCardType.STEAM, WithdrawalCardType.VK})
@Retention(RetentionPolicy.SOURCE)
public @interface WithdrawalCardType {
    int WEBMONEY = 1;
    int YANDEX = 2;
    int QIWI = 3;
    int MTS = 4;
    int TELE2 = 5;
    int MEGAFON = 6;
    int BEELINE = 7;
    int PAYPAL = 8;
    int WOT = 9;
    int WARFACE = 10;
    int STEAM = 11;
    int VK = 12;
}