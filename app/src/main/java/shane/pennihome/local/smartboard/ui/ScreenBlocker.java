package shane.pennihome.local.smartboard.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import shane.pennihome.local.smartboard.data.Globals;

/**
 * Created by shane on 02/03/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class ScreenBlocker extends FrameLayout {
    private AlphaAnimation mAnimation;
    private boolean mShown = false;
    private OnBlockListener mOnBlockListener;

    public ScreenBlocker(@NonNull Context context) {
        super(context);
        init();
    }

    public ScreenBlocker(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScreenBlocker(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private OnBlockListener getOnBlockListener() {
        return mOnBlockListener;
    }

    public void setOnBlockListener(OnBlockListener onBlockListener) {
        mOnBlockListener = onBlockListener;
    }

    @Override
    public boolean isShown() {
        return mShown;
    }

    private void setShown(boolean shown) {
        mShown = shown;
        if (getOnBlockListener() != null) {
            if (isShown())
                getOnBlockListener().OnShown();
            else
                getOnBlockListener().OnDismiss();
        }
        setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
    }

    private void init() {
        this.setVisibility(View.GONE);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShown())
                    dismiss();
            }
        });
    }

    public void show() {
        setVisibility(View.VISIBLE);
        createAnimation(0.0f, 1.0f);

        mAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mAnimation = null;
                setShown(true);
                Log.i(Globals.ACTIVITY, "Finished blocker show animation");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        post(new Runnable() {
            @Override
            public void run() {
                startAnimation(mAnimation);
                Log.i(Globals.ACTIVITY, "Starting blocker show animation");
            }
        });
    }

    private void dismiss() {
        createAnimation(1.0f, 0.0f);
        mAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(View.GONE);
                mAnimation = null;
                setShown(false);
                Log.i(Globals.ACTIVITY, "Finished blocker dismiss animation");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        post(new Runnable() {
            @Override
            public void run() {
                if (mAnimation != null)
                    startAnimation(mAnimation);
                Log.i(Globals.ACTIVITY, "Starting blocker dismiss animation");
            }
        });
    }

    private void createAnimation(float from, float to) {
        if (mAnimation != null)
            mAnimation.cancel();
        mAnimation = new AlphaAnimation(from, to);
        int DURATION = 3000;
        mAnimation.setDuration(DURATION);
    }

    public interface OnBlockListener {
        void OnShown();

        void OnDismiss();
    }
}
