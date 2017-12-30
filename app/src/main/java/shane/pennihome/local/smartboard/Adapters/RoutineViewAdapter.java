package shane.pennihome.local.smartboard.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import shane.pennihome.local.smartboard.Data.Interface.Thing;
import shane.pennihome.local.smartboard.Data.Routine;
import shane.pennihome.local.smartboard.Fragments.ThingFragment;
import shane.pennihome.local.smartboard.R;

/**
 * Created by shane on 30/12/17.
 */

public class RoutineViewAdapter extends ThingViewAdapter {
    public RoutineViewAdapter(List<Thing> items, ThingFragment.OnListFragmentInteractionListener listener) {
        super(items, listener);
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_routine;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder vh = (ViewHolder)holder;
        Routine r = (Routine)mValues.get(position);
        vh.mItem = r;
        vh.mIdView.setText(r.getName());
        vh.mContentView.setText(r.getName());

        vh.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(vh.mItem);
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Routine mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.id);
            mContentView = view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
