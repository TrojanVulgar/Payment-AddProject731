package net.makemoney.android.game;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SwipeEvents implements View.OnTouchListener {

    private SwipeCallback swipeCallback;
    private SwipeSingleCallback swipeSingleCallback;
    private SwipeDirection detectSwipeDirection;

    float x1, x2, y1, y2;
    View view;
    static ImageView dollar, start;
    private float dX, dY;

    private static SwipeEvents newInstance() {
        return new SwipeEvents();
    }

    public static void detect(View view, SwipeCallback swipeCallback, ImageView dollar_, ImageView start_) {
        SwipeEvents evt = SwipeEvents.newInstance();
        evt.swipeCallback = swipeCallback;
        evt.view = view;
        dollar = dollar_;
        start = start_;
        evt.detect();
    }

    public static void detectTop(View view, SwipeSingleCallback swipeSingleCallback) {
        SwipeEvents evt = SwipeEvents.newInstance();
        evt.swipeSingleCallback = swipeSingleCallback;
        evt.view = view;
        evt.detectSingle(SwipeDirection.TOP);
    }

    public static void detectRight(View view, SwipeSingleCallback swipeSingleCallback) {
        SwipeEvents evt = SwipeEvents.newInstance();
        evt.swipeSingleCallback = swipeSingleCallback;
        evt.view = view;
        evt.detectSingle(SwipeDirection.RIGHT);
    }

    public static void detectBottom(View view, SwipeSingleCallback swipeSingleCallback) {
        SwipeEvents evt = SwipeEvents.newInstance();
        evt.swipeSingleCallback = swipeSingleCallback;
        evt.view = view;
        evt.detectSingle(SwipeDirection.BOTTOM);
    }

    public static void detectLeft(View view, SwipeSingleCallback swipeSingleCallback) {
        SwipeEvents evt = SwipeEvents.newInstance();
        evt.swipeSingleCallback = swipeSingleCallback;
        evt.view = view;
        evt.detectSingle(SwipeDirection.LEFT);
    }

    private void detect() {
        view.setOnTouchListener(this);
    }

    private void detectSingle(SwipeDirection direction) {
        this.detectSwipeDirection = direction;
        view.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_CANCEL:
                ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(dollar.getLayoutParams());
                marginParams.setMargins(marginParams.leftMargin, marginParams.topMargin, marginParams.rightMargin, (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        40,
                        Resources.getSystem().getDisplayMetrics()
                ));
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                layoutParams.addRule(RelativeLayout.ALIGN_LEFT, start.getId());
                dollar.setLayoutParams(layoutParams);
                dollar.setVisibility(View.GONE);
                dollar.animate().y( Resources.getSystem().getDisplayMetrics().heightPixels - (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        320,
                        Resources.getSystem().getDisplayMetrics()
                )).start();
                break;
            case MotionEvent.ACTION_DOWN:
                dollar.setVisibility(View.VISIBLE);
                x1 = event.getX();
                y1 = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                y2 = event.getY();
                SwipeDirection direction = null;

                float xDiff = x2 - x1;
                float yDiff = y2 - y1;
                if (Math.abs(xDiff) > Math.abs(yDiff)) {
                    if (x1 < x2) {
                        direction = SwipeDirection.RIGHT;
                    } else {
                        direction = SwipeDirection.LEFT;
                    }
                } else {
                    if (y1 > y2) {
                        direction = SwipeDirection.TOP;
                    } else {
                        direction = SwipeDirection.BOTTOM;
                    }
                }

                if (detectSwipeDirection != null && swipeSingleCallback != null) {
                    if (direction == detectSwipeDirection) {
                        swipeSingleCallback.onSwipe();
                    }
                } else {
                    if (direction == SwipeDirection.RIGHT) {
                        swipeCallback.onSwipeRight();
                    }
                    if (direction == SwipeDirection.LEFT) {
                        swipeCallback.onSwipeLeft();
                    }
                    if (direction == SwipeDirection.TOP) {
                        swipeCallback.onSwipeTop(event.getAction());
                    }
                    if (direction == SwipeDirection.BOTTOM) {
                        swipeCallback.onSwipeBottom();
                    }
                }

                ViewGroup.MarginLayoutParams marginParams2 = new ViewGroup.MarginLayoutParams(dollar.getLayoutParams());
                marginParams2.setMargins(marginParams2.leftMargin, marginParams2.topMargin, marginParams2.rightMargin, (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        40,
                        Resources.getSystem().getDisplayMetrics()
                ));
                RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(marginParams2);
                layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                layoutParams2.addRule(RelativeLayout.ALIGN_LEFT, start.getId());
                dollar.setLayoutParams(layoutParams2);
                dollar.setVisibility(View.GONE);
                dollar.animate().y( Resources.getSystem().getDisplayMetrics().heightPixels - (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        320,
                        Resources.getSystem().getDisplayMetrics()
                )).start();

                break;
            case MotionEvent.ACTION_MOVE:
                dollar.animate()
                        .y(dollar.getY() - 32)
                        .setDuration(0)
                        .start();
                break;

        }
        return false;
    }

    public enum SwipeDirection {
        TOP, RIGHT, BOTTOM, LEFT
    }

    public interface SwipeCallback {
        public void onSwipeTop(int event);

        public void onSwipeRight();

        public void onSwipeBottom();

        public void onSwipeLeft();
    }

    public interface SwipeSingleCallback {
        public void onSwipe();
    }
}