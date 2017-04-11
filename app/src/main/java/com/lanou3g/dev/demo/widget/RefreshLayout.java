package com.lanou3g.dev.demo.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.annotation.Px;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

/**
 * Created by Risky57 on 2017/4/6.
 */

public class RefreshLayout extends ViewGroup {

    private static final int STATE_CANCEL = 0;
    private static final int STATE_REFRESHING = 1;
    private static final int STATE_LOADING = 2;

    private static final int INTENT_ORIENTATION_START = 0;
    private static final int INTENT_ORIENTATION_DOWN = 1;
    private static final int INTENT_ORIENTATION_UP = 2;

    private int loadState;

    private int orientationIntent;
    private boolean checkIntent = true;

    private View mTarget;

    private LoadingLayout mHeaderView;
    private LoadingLayout mFooterView;

    private Scroller mScroller;
    private int mHeaderHeight;
    private int mTargetHeight;
    private int mFooterHeight;
    private Animator.AnimatorListener mAnimatorListener;

    private OnRefreshOrLoadListener mOnRefreshOrLoadListener;

    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        mHeaderView = new LoadingLayout(getContext());
        mFooterView = new LoadingLayout(getContext());
        addView(mHeaderView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(mFooterView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mScroller = new Scroller(getContext(), new DecelerateInterpolator());

        mAnimatorListener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                locationReset();
            }
        };
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        mTarget = getChildAt(2);
        setMeasuredDimension(mTarget.getMeasuredWidth(), mTarget.getMeasuredHeight());
        mHeaderHeight = mHeaderView.getMeasuredHeight();
        mTargetHeight = mTarget.getMeasuredHeight();
        mFooterHeight = mFooterView.getMeasuredHeight();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mHeaderView.layout(l, -mHeaderHeight, r, 0);
        mTarget.layout(l, 0, r, mTargetHeight);
        mFooterView.layout(l, mTargetHeight, r, mTargetHeight + mFooterHeight);
    }

    private int interceptLastX;
    private int interceptLastY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;

        int startX = (int) ev.getX();
        int startY = (int) ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                intercept = false;
                lastY = startY;
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = startX - interceptLastX;
                int deltaY = startY - interceptLastY;

                if (Math.abs(deltaX) > Math.abs(deltaY)){
                    intercept = false;
                }else{
                    if (deltaY > 0 && !mTarget.canScrollVertically(-1)){
                        intercept = true;
                    }else if (deltaY < 0 && !mTarget.canScrollVertically(1)){
                        intercept = true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:

                break;
        }

        interceptLastX = startX;
        interceptLastY = startY;

        return intercept;
    }

    private int lastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int startY = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:
                int delta = startY - lastY;
                int scrollY = getScrollY();
                if (checkIntent) {
                    checkIntent = false;
                    if (scrollY < 0) orientationIntent = INTENT_ORIENTATION_DOWN;
                    if (scrollY > 0) orientationIntent = INTENT_ORIENTATION_UP;
                    if (scrollY == 0) checkIntent = true;
                }
                if (scrollY < 0 && scrollY > -mHeaderHeight) {
                    // 下拉未到临界点
                    mHeaderView.pullDown();
                    loadState = STATE_CANCEL;
                } else if (scrollY <= -mHeaderHeight) {
                    // 下拉到临界点
                    mHeaderView.loosen();
                    loadState = STATE_REFRESHING;
                } else if (scrollY > 0 && scrollY < mFooterHeight) {
                    // 上拉未到临界点
                    mFooterView.pullDown();
                    loadState = STATE_CANCEL;
                } else if (scrollY >= mFooterHeight) {
                    // 上拉到临界点
                    mFooterView.loosen();
                    loadState = STATE_LOADING;
                }
                scrollBy(0, -delta);
                break;
            case MotionEvent.ACTION_UP:
                switch (loadState) {
                    case STATE_CANCEL:
                        locationReset();
                        break;
                    case STATE_REFRESHING:
                        mHeaderView.refreshing();
                        if (mOnRefreshOrLoadListener != null) {
                            mOnRefreshOrLoadListener.onRefresh();
                        }
                        locationRefreshing();
                        break;
                    case STATE_LOADING:
                        mFooterView.refreshing();
                        if (mOnRefreshOrLoadListener != null) {
                            mOnRefreshOrLoadListener.onLoad();
                        }
                        locationLoading();
                        break;
                }
                checkIntent = true;
                orientationIntent = INTENT_ORIENTATION_START;
                break;
        }
        lastY = startY;

        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            invalidate();
        }
    }

    @Override
    public void scrollTo(@Px int x, @Px int y) {
//        Log.d("RefreshLayout", "y:" + y);
        switch (orientationIntent) {
            case INTENT_ORIENTATION_START:
                break;

            case INTENT_ORIENTATION_DOWN:
                y = y > 0 ? 0 : y;
                break;
            case INTENT_ORIENTATION_UP:
                y = y < 0 ? 0 : y;
                break;
        }
        super.scrollTo(x, y);
    }

    public void loadingFinished() {

        switch (loadState) {
            case STATE_REFRESHING:
                mHeaderView.refreshFinish(mAnimatorListener);
                break;
            case STATE_LOADING:
                mFooterView.refreshFinish(mAnimatorListener);
                break;
        }
    }

    private void locationReset() {
        mScroller.startScroll(0, getScrollY(), 0, -getScrollY());
        invalidate();
    }

    private void locationRefreshing() {
        mScroller.startScroll(0, getScrollY(), 0, -getScrollY() - mHeaderHeight);
        invalidate();
    }

    private void locationLoading() {
        mScroller.startScroll(0, getScrollY(), 0, -getScrollY() + mFooterHeight);
        invalidate();
    }

    public void setOnRefreshOrLoadListener(OnRefreshOrLoadListener onRefreshOrLoadListener) {
        mOnRefreshOrLoadListener = onRefreshOrLoadListener;
    }

    public interface OnRefreshOrLoadListener{
        void onRefresh();
        void onLoad();
    }
}
