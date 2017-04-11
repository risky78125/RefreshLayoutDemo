package com.lanou3g.dev.demo.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lanou3g.dev.demo.R;

/**
 * Created by Risky57 on 2017/4/6.
 */

public class LoadingLayout extends FrameLayout {

    private ImageView mImgArrow;
    private TextView mTextMessage;

    private ObjectAnimator animRefreshing;
    private ObjectAnimator animPulldown;
    private ObjectAnimator animLoosen;


    public LoadingLayout(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_loading, this, true);
        mImgArrow = (ImageView) findViewById(R.id.img_arrow);
        mTextMessage = (TextView) findViewById(R.id.text_message);

        animRefreshing = createRefreshingAnim();
        animLoosen = createRotateAnim(0f, 180f, 180f);
        animPulldown = createRotateAnim(-180f, 0f, 0f);

    }

    private ObjectAnimator createRefreshingAnim(){
        ObjectAnimator anim = ObjectAnimator.ofFloat(mImgArrow, "rotation", 0, 360f);
        anim.setDuration(500);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setInterpolator(new LinearInterpolator());
        return anim;
    }

    public void refreshFinish(Animator.AnimatorListener listener) {
        animRefreshing.cancel();
        mTextMessage.setText("刷新完成");
        mImgArrow.setImageResource(R.drawable.ic_check);
        animPulldown.addListener(listener);
        animPulldown.start();
    }

    public void refreshing() {
        mTextMessage.setText("正在刷新");
        mImgArrow.setImageResource(R.drawable.ic_autorenew);
        if (animRefreshing.isRunning()) {
            animRefreshing.cancel();
        }
        animRefreshing.start();
    }

    private boolean isLoosenState;
    private boolean isPullState;


    public void loosen() {
        if (!isLoosenState) {
            isLoosenState = true;
            isPullState = false;
            mTextMessage.setText("松开刷新");
            mImgArrow.setImageResource(R.drawable.ic_arrow);
            animRefreshing.cancel();
            animLoosen.start();
        }
    }

    public void pullDown() {
        if (!isPullState) {
            isPullState = true;
            isLoosenState = false;
            mTextMessage.setText("继续下拉刷新");
            mImgArrow.setImageResource(R.drawable.ic_arrow);
            animRefreshing.cancel();
            animPulldown.removeAllListeners();
            animPulldown.start();
        }
    }

    private ObjectAnimator createRotateAnim(float... values){
        ObjectAnimator anim = ObjectAnimator.ofFloat(mImgArrow, "rotation", values);
        anim.setDuration(500);
        return anim;
    }
}
