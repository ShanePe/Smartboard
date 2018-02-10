package shane.pennihome.local.smartboard.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;

import shane.pennihome.local.smartboard.R;

/**
 * Created by shane on 27/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class ViewSwiper extends ViewPager {
    private ViewAdapter mViewAdapter;
    private TabLayout mTabLayout;
    private boolean mAutoHideTabs;
    private Thread mTimer;
    private Animation mSlideUpAnim;
    private Animation mSlideDnAnim;

    public ViewSwiper(@NonNull Context context) {
        super(context);
    }

    public ViewSwiper(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public View getView(int id)
    {
        return getViewAdapter().getView(id);
    }

    public void addView(String name, int ResId) {
        getViewAdapter().addView(name, ResId);
    }

    public void removeView(@SuppressWarnings("SameParameterValue") String name)
    {
        getViewAdapter().removeView(name);
    }

    public boolean isAutoHideTabs() {
        return mAutoHideTabs;
    }

    public void setAutoHideTabs(@SuppressWarnings("SameParameterValue") boolean autoHideTabs) {
        mAutoHideTabs = autoHideTabs;
        if (autoHideTabs) {
            mTabLayout.setVisibility(View.GONE);

            mSlideUpAnim = AnimationUtils.loadAnimation(mTabLayout.getContext(), R.anim.tab_up);
            mSlideDnAnim = AnimationUtils.loadAnimation(mTabLayout.getContext(), R.anim.tab_down);
            mSlideUpAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            mSlideDnAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mTabLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        } else {
            mSlideUpAnim = null;
            mSlideDnAnim = null;
        }
    }

    public TabLayout getTabLayout() {
        return mTabLayout;
    }

    public void setTabLayout(TabLayout tabLayout) {
        mTabLayout = tabLayout;
        mTabLayout.setupWithViewPager(this);
    }

    public ViewSwiper.ViewAdapter getViewAdapter() {
        if (mViewAdapter == null)
            createAdapter();

        return mViewAdapter;
    }

    private void createAdapter() {
        mViewAdapter = new ViewAdapter();
        setAdapter(mViewAdapter);
        mViewAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPageScrolled(int position, float offset, int offsetPixels) {
        super.onPageScrolled(position, offset, offsetPixels);

        if (mTabLayout != null && mAutoHideTabs) {
            if (mTabLayout.getVisibility() != View.VISIBLE) {
                mTabLayout.setVisibility(View.VISIBLE);

                mSlideDnAnim.cancel();
                mSlideUpAnim.cancel();

                mTabLayout.startAnimation(mSlideUpAnim);

            }

            if (mTimer != null) {
                mTimer.interrupt();
                mTimer = null;
            }

            mTimer = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                        mTabLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                mTabLayout.startAnimation(mSlideDnAnim);
                            }
                        });
                    } catch (InterruptedException ignored) {
                    }
                }
            });

            mTimer.start();
        }
    }

    void clear() {
        createAdapter();
    }

    public class ViewAdapter extends PagerAdapter {
        final ArrayList<Pair<String, Integer>> mTabs;
        final SparseArray<View> mViewCache;

        ViewAdapter() {
            mTabs = new ArrayList<>();
            mViewCache = new SparseArray<>();
        }

        void addView(String name, int ResId) {
            mTabs.add(new Pair<>(name, ResId));
            notifyDataSetChanged();
        }

        void addView(String name, View view) {
            int pos = mTabs.size();
            mTabs.add(new Pair<>(name, pos));
            mViewCache.put(pos, view);
        }

        void removeView(String name)
        {
            for(int i=0;i<mTabs.size();i++)
                if(mTabs.get(i).first.toLowerCase().equals(name.toLowerCase()))
                {
                    mTabs.remove(i);
                    mViewCache.remove(i);
                    notifyDataSetChanged();
                    break;
                }
        }

        View getView(int id)
        {
            for(int i = 0;i<mViewCache.size();i++) {
                if (mViewCache.get(i).getId() == id)
                    return mViewCache.get(i);
                View internal = mViewCache.get(i).findViewById(id);
                if(internal != null)
                    return internal;
            }
            return findViewById(id);
        }


        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            if (mViewCache.get(position) == null)
                mViewCache.put(position, findViewById(mTabs.get(position).second));

            View view = mViewCache.get(position);
            boolean hasView = false;
            for (int i = 0; i < container.getChildCount(); i++)
                if (container.getChildAt(i).equals(view)) {
                    hasView = true;
                    break;
                }

            if (!hasView)
                container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTabs.get(position).first;
        }
    }
}
