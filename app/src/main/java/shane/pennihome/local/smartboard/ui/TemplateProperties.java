package shane.pennihome.local.smartboard.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.data.Template;
import shane.pennihome.local.smartboard.data.Templates;
import shane.pennihome.local.smartboard.data.sql.DBEngine;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;

/**
 * Created by SPennicott on 10/02/2018.
 */

public class TemplateProperties extends LinearLayoutCompat {
    private Templates mTemplates;
    private Spinner mSpTemplate;
    private ImageView mPreview;
    private Template mTemplate;
    private CheckBox mSaveTemplate;
    private OnTemplateActionListener mOnTemplateActionListener;

    public Templates getTemplates() {
        return mTemplates;
    }

    public void setTemplates(Templates templates) {
        this.mTemplates = templates;
        doSpinnerTemplates();
        doPropertyChange();
    }

    public Template getTemplate() {
        return mTemplate;
    }

    public void setTemplate(Template template) {
        this.mTemplate = template;
        doPropertyChange();
    }

    public boolean isSaveAsTemplate() {
        return mSaveTemplate != null && mSaveTemplate.isChecked();
    }

    public void setOnTemplateActionListener(OnTemplateActionListener ontemplateactionlistener) {
        this.mOnTemplateActionListener = ontemplateactionlistener;
    }

    public TemplateProperties(Context context) {
        super(context);
        initialiseView(context);
    }

    public TemplateProperties(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialiseView(context);
    }

    public TemplateProperties(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialiseView(context);
    }

    private void initialiseView(Context context){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.custom_template_selector, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mSpTemplate = findViewById(R.id.cts_templates);
        mPreview = findViewById(R.id.cts_preview);
        mSaveTemplate = findViewById(R.id.cts_save);

        mSpTemplate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0)
                    mTemplate = null;
                else {
                    mTemplate = mTemplates.get(i - 1);
                    if (mOnTemplateActionListener != null)
                        mOnTemplateActionListener.OnTemplateSelected(mTemplate);
                }
                doPropertyChange();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mTemplate = null;
                doPropertyChange();
            }
        });

        doSpinnerTemplates();
        doPropertyChange();


    }

    private void doPropertyChange()
    {
        mSaveTemplate.setEnabled(mTemplate == null);
        if(mPreview != null && mTemplate != null) {
            mSaveTemplate.setChecked(false);
            mTemplate.getBlock().renderTemplateBackgroundTo(mPreview);
        }
        else if(mTemplate == null) {
            assert mPreview != null;
            mPreview.setBackgroundColor(Color.TRANSPARENT);
        }

        invalidate();
        requestLayout();
    }

    private void doSpinnerTemplates() {
        if (mSpTemplate != null && mTemplates != null) {
            mSpTemplate.setAdapter(new TemplateAdapter(mTemplates));
        }
    }

    public interface OnTemplateActionListener {
        void OnTemplateSelected(Template template);
    }


    public void createTemplate(Context context, IBlock source) throws Exception {
        IBlock block = IBlock.CreateByTypeID(source.getThingType().ordinal());
        block.copyValuesFrom(source);
        Template template = new Template(block);
        DBEngine db = new DBEngine(context);
        db.writeToDatabase(template);
    }

    private static class TemplateAdapter extends BaseAdapter implements SpinnerAdapter
    {
        final Templates mTemplates;

        TemplateAdapter(Templates templates) {
            this.mTemplates = templates;
        }

        @Override
        public int getCount() {
            return mTemplates.size() + 1;
        }

        @Override
        public Object getItem(int i) {
            return mTemplates.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater layoutInflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                assert layoutInflater != null;
                view = layoutInflater.inflate(R.layout.spinner_template, null);
            }

            TextView txt = view.findViewById(R.id.temp_sp_templates);
            ImageView img = view.findViewById(R.id.temp_blip);

            if(i == 0) {
                txt.setText(R.string.lbl_select_a_template);
                img.setVisibility(View.GONE);
            }
            else {
                Template t = mTemplates.get(i-1);
                txt.setText(t.getName());
                mTemplates.get(i-1).getBlock().renderTemplateBlip(img);
            }
            return view;
        }

    }
}
