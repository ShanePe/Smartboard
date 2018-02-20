package shane.pennihome.local.smartboard.ui;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.GridLayout;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.adapters.MultiThingSelectorAdapter;

/**
 * Created by shane on 19/02/18.
 */

public class MultiThingSelector extends GridLayout {
    Things mThings;
    MultiThingSelectorAdapter mAdapter;

    public MultiThingSelector(Context context) {
        super(context);
        initialiseView(context);
    }

    public MultiThingSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialiseView(context);
    }

    public MultiThingSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialiseView(context);
    }

    public Things getThings() {
        return mThings;
    }

    public void setThings(Things things) {
        mThings = things;
        if (mAdapter != null) {
            mAdapter.setThings(mThings);
            mAdapter.notifyDataSetChanged();
        }
    }

    public Things getSelectedThings() {
        return mAdapter.getSelectedThings();
    }

    public void setSelectedThings(Things selectedThings) {
        mAdapter.setSelectedThings(selectedThings);
    }

    private void initialiseView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.custom_multi_thing_selection_list, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        RecyclerView rv = (RecyclerView) findViewById(R.id.cmts_recycleview);
        rv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mAdapter = new MultiThingSelectorAdapter(getThings());
        rv.setAdapter(mAdapter);
    }
}

