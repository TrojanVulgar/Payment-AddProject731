package net.makemoney.android.data.models.tasks;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@IntDef({TaskRateType.WITHOUT_RATE, TaskRateType.ONLY_RATE, TaskRateType.RATE_WITH_FEED})
@Retention(RetentionPolicy.SOURCE)
public @interface TaskRateType {
    int WITHOUT_RATE = 0;
    int ONLY_RATE = 1;
    int RATE_WITH_FEED = 2;
}
