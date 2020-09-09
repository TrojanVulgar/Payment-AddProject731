package net.makemoney.android.data.models.tasks;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@IntDef({OwnTaskType.DIRECT, OwnTaskType.SEARCH})
@Retention(RetentionPolicy.SOURCE)
public @interface OwnTaskType {
    int DIRECT = 0;
    int SEARCH = 1;
}
