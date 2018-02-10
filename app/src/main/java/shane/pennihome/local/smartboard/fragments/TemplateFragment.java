package shane.pennihome.local.smartboard.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.adapters.TemplateAdapter;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Globals;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.data.Template;
import shane.pennihome.local.smartboard.data.Templates;
import shane.pennihome.local.smartboard.data.sql.DBEngine;
import shane.pennihome.local.smartboard.fragments.interfaces.IFragment;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnBlockSetListener;
import shane.pennihome.local.smartboard.ui.UIHelper;
import shane.pennihome.local.smartboard.ui.listeners.OnBlockSelectListener;

/**
 * Created by SPennicott on 09/02/2018.
 */

@SuppressWarnings("DefaultFileTemplate")
public class TemplateFragment extends IFragment {

    private TemplateAdapter mAdapter;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mnu_add_frag, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mnu_dash_add)
        {
            UIHelper.showBlockSelectionWindow((AppCompatActivity)getActivity(), new OnBlockSelectListener() {
                @Override
                public void BlockSelected(IBlock block) {
                    block.setBlockDefaults(new Group());
                    UIHelper.showBlockTemplateWindow((AppCompatActivity) getActivity(), block, new OnBlockSetListener() {
                        @Override
                        public void OnSet(IBlock block) {
                            Template template = new Template(block);
                            DBEngine db = new DBEngine(getContext());
                            db.writeToDatabase(template);
                            mAdapter.notifyItemInserted(mAdapter.addTemplate(template));
                        }
                    });
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_template_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), Globals.BLOCK_COLUMNS));

            mAdapter = new TemplateAdapter(Templates.Load(getContext()));
            mAdapter.setOnInteractionListener(new TemplateAdapter.OnInteractionListener() {
                @Override
                public void onInteraction(final Template item, final int position) {
                    UIHelper.showBlockTemplateWindow((AppCompatActivity) getActivity(), item.getBlock(), new OnBlockSetListener() {
                        @Override
                        public void OnSet(IBlock block) {
                            item.setBlock(block);
                            item.setName(block.getName());
                            DBEngine db = new DBEngine(getContext());
                            db.writeToDatabase(item);
                            mAdapter.notifyItemChanged(position);
                        }
                    });
                }

                @Override
                public void onItemLongClick(final Template item, final int position) {
                    UIHelper.showConfirm(getContext(), "Confirm",
                            String.format("Are you sure you want to delete the template block '%s'", item.getName()), new OnProcessCompleteListener() {
                        @Override
                        public void complete(boolean success, Object source) {
                            if(success)
                            {
                                DBEngine db = new DBEngine(getContext());
                                db.deleteFromDatabase(item);
                                mAdapter.getTemplates().remove(item);
                                mAdapter.notifyItemRemoved(position);
                            }
                        }
                    });
                }
            });
            recyclerView.setAdapter(mAdapter);
        }

        return view;

    }
}

