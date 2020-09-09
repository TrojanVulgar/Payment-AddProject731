package net.makemoney.android.data.models.referral;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@IntDef({DetailsType.OTHER, DetailsType.CURRENCY})
@Retention(RetentionPolicy.SOURCE)
public @interface DetailsType {
    int OTHER = 0;
    int CURRENCY = 1;
}
