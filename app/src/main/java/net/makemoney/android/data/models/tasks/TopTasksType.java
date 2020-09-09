package net.makemoney.android.data.models.tasks;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@IntDef({TopTasksType.TASKS, TopTasksType.PARTNERS})
@Retention(RetentionPolicy.SOURCE)
public @interface TopTasksType {
    int TASKS = 0;
    int PARTNERS = 1;
}
