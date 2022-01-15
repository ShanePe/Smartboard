package shane.pennihome.local.smartboard.data;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.data.sql.DBEngine;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by SPennicott on 10/02/2018.
 */

public class Templates extends ArrayList<Template> {
    public Templates getForType(IThing.Types type)
    {
        Templates ret = new Templates();
        for(Template t : this)
            if(t.getThingType() == type)
                ret.add(t);

        ret.sort();
        return ret;
    }

    private void sort()
    {
        Collections.sort(this, new Comparator<Template>() {
            @Override
            public int compare(Template template, Template t1) {
                return template.getName().compareTo(t1.getName());
            }
        });
    }

    public int getIndex(Template t) {
        for (int i = 0; i < size(); i++) {
            if(get(i).getDataID().equals(t.getDataID()))
                return i;
        }

        return -1;
    }

    public static Templates Load(Context context)
    {
        Templates templates = new Templates();
        DBEngine db = new DBEngine(context);
        for(IDatabaseObject d: db.readFromDatabaseByType(IDatabaseObject.Types.Template))
            templates.add((Template)d);
        return templates;
    }
}
