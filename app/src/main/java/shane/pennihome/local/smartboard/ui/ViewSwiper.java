package shane.pennihome.local.smartboard.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by shane on 27/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class ViewSwiper extends ViewPager {
    private ViewAdapter mViewAdapter;
    private TabLayout mTabLayout;

    public ViewSwiper(@NonNull Context context) {
        super(context);
    }

    public ViewSwiper(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TabLayout getTabLayout() {
        return mTabLayout;
    }

    public void setTabLayout(TabLayout tabLayout) {
        mTabLayout = tabLayout;
        mTabLayout.setupWithViewPager(this);
    }

    public ViewSwiper.ViewAdapter getViewAdapter() {
        if (mViewAdapter == null) {
            mViewAdapter = new ViewAdapter();
            setAdapter(mViewAdapter);
        }
        return mViewAdapter;
    }

    public class ViewAdapter extends PagerAdapter {
        final ArrayList<Pair<String, Integer>> mTabs;

        ViewAdapter() {
            this.mTabs = new ArrayList<>();
        }

        void addView(String name, int ResId) {
            mTabs.add(new Pair<>(name, ResId));
            notifyDataSetChanged();
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
            return findViewById(mTabs.get(position).second);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTabs.get(position).first;
        }
    }
}
