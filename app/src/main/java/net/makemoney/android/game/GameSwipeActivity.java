package net.makemoney.android.game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;

import net.makemoney.android.R;
import net.makemoney.android.extensions.ExtensionsKt;
import net.makemoney.android.utils.AppPreferences;
import net.makemoney.android.utils.InstantDialog;

import org.joda.time.DateTime;

import java.util.Locale;

import timber.log.Timber;

public class GameSwipeActivity extends Activity {

    private float balance = 0;
    private TextView money, record1, record2;
    private long time;
    private float record_all_text = 0f;
    private ImageView animation, start;
    private Intent resultIntent;

    private boolean isUnpaidMode = false;

    private RewardedVideoAd ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        if (!AppPreferences.INSTANCE.getGameSwipeDateStart().equals(DateTime.now().toString("yyyy-MM-dd", Locale.US))) {
            AppPreferences.INSTANCE.setGameSwipeTimes(0);
            AppPreferences.INSTANCE.setGameSwipeDateStart(DateTime.now().toString("yyyy-MM-dd", Locale.US));
        } else {
            if (AppPreferences.INSTANCE.getGameSwipeTimes() > 1000) {
                isUnpaidMode = true;
            }
        }

        ad = MobileAds.getRewardedVideoAdInstance(ExtensionsKt.getAppContext());
        ad.setRewardedVideoAdListener(null);
        ad.loadAd(getString(R.string.google_ad_video_id), new AdRequest.Builder().build());

        money = (TextView) findViewById(R.id.money);
        record1 = (TextView) findViewById(R.id.record1);
        record2 = (TextView) findViewById(R.id.record2);
        animation = (ImageView) findViewById(R.id.animation);
        start = (ImageView) findViewById(R.id.start);
        resultIntent = new Intent();

        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.swipe);
        SwipeEvents.detect(relativeLayout, new SwipeEvents.SwipeCallback() {
            @Override
            public void onSwipeTop(int event) {
                Timber.i("SWIPE TIMES: %s", AppPreferences.INSTANCE.getGameSwipeTimes());
                if (AppPreferences.INSTANCE.getGameSwipeTimes() >= 999) {
                    showDialog();
                }

                AppPreferences.INSTANCE.setGameSwipeTimes(AppPreferences.INSTANCE.getGameSwipeTimes() + 1);
                Timber.i("Preferences: %s", AppPreferences.INSTANCE.getGameSwipeTimes() % 100);
                if (AppPreferences.INSTANCE.getGameSwipeTimes() % 100 == 0) { // show ad every 100 swipes
                    Timber.i("Here is ur ad, blush, videos is loaded? %s", ad.isLoaded());
                    if (ad.isLoaded()) {
                        ad.show();
                    }
                }

                if (isUnpaidMode) {
                    setResult(Activity.RESULT_CANCELED);
                    return;
                }

                if (balance == 0) {
                    time = System.currentTimeMillis();
                }
                balance = balance + 0.01f;

                setResult(Activity.RESULT_OK, resultIntent.putExtra("game_score", balance));

                float record_text = balance / ((System.currentTimeMillis() - time) / 1000);
                if (record_text == Double.POSITIVE_INFINITY) {
                    record_text = 0;
                }
                if (record_all_text < record_text) {
                    record_all_text = record_text;
                }
                money.setText(String.format(Locale.ITALY, "%.2f", balance).concat(" $"));
                record1.setText(String.format(Locale.ITALY, "%.2f", record_text));
                record2.setText(String.format(Locale.ITALY, "%.2f", record_all_text));
            }

            @Override
            public void onSwipeRight() {
            }

            @Override
            public void onSwipeBottom() {
            }

            @Override
            public void onSwipeLeft() {
            }
        }, animation, start);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ad.loadAd(getString(R.string.google_ad_video_id), new AdRequest.Builder().build());
    }

    private void showDialog() {
        String description = getString(R.string.games_limit_over);
        View dialogInflate = LayoutInflater.from(this).inflate(R.layout.dialog_simple_info, null, false);
        AlertDialog dialog = new InstantDialog(this, dialogInflate).show("");
        try {
            assert dialog != null;
            TextView tv = dialog.findViewById(R.id.tv_info_simple_dialog_text);
            Button btn = dialog.findViewById(R.id.btn_info_simple_dialog_ok);

            tv.setText(description);
            btn.setOnClickListener(v -> {
                finish();
                dialog.cancel();
            });
        } catch (NullPointerException e) {
            Timber.e(e);
        }
    }
}