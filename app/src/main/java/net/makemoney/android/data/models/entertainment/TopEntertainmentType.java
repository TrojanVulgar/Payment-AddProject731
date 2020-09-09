package net.makemoney.android.data.models.entertainment;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@IntDef({TopEntertainmentType.QUIZZES, TopEntertainmentType.GAMES})
@Retention(RetentionPolicy.SOURCE)
public @interface TopEntertainmentType {
    int QUIZZES = 0;
    int GAMES = 1;
}
