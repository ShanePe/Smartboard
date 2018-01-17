package shane.pennihome.local.smartboard.Data;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import shane.pennihome.local.smartboard.Adapters.SpinnerThingAdapter;
import shane.pennihome.local.smartboard.Comms.Interface.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.Data.Interface.IBlock;
import shane.pennihome.local.smartboard.Data.Interface.IDatabaseObject;
import shane.pennihome.local.smartboard.Data.Interface.IThing;
import shane.pennihome.local.smartboard.Listeners.OnBlockSetListener;
import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.UI.Interface.IBlockUI;
import shane.pennihome.local.smartboard.UI.SwitchBlockHandler;
import shane.pennihome.local.smartboard.UI.UIHelper;

/**
 * Created by shane on 13/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class SwitchBlock extends IBlock {
    public static SwitchBlock Load(String json) {
        try {
            return IDatabaseObject.Load(SwitchBlock.class, json);
        } catch (Exception e) {
            return new SwitchBlock();
        }
    }

   @Override
    public IBlockUI getUIHandler() {
        return new SwitchBlockHandler(this);
    }

    @Override
    public int GetViewResourceID() {
        return R.layout.dashboard_block_switch;
    }

}
