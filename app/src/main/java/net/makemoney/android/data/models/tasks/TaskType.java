package net.makemoney.android.data.models.tasks;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@IntDef({TaskType.NEW, TaskType.ACTIVE, TaskType.PARTNER, TaskType.VIDEO, TaskType.GAMES, TaskType.QUIZZES})
@Retention(RetentionPolicy.SOURCE)
public @interface TaskType {
    int NEW = 0;
    int ACTIVE = 1;
    int PARTNER = 2;
    int VIDEO = 3;
    int GAMES = 4;
    int QUIZZES = 5;
}
