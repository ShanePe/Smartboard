package shane.pennihome.local.smartboard.Data;

import java.util.ArrayList;
import java.util.List;

import shane.pennihome.local.smartboard.Data.Interface.IDatabaseObject;

/**
 * Created by shane on 13/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Dashboard extends IDatabaseObject{
    private String mName = "";
    private final List<Row> mRows = new ArrayList<>();

    public String getName(){return mName;}
    public void setName(String name){mName = name;}
    public List<Row> getRows(){return mRows; }

    @Override
    public Types getType() {
        return Types.Dashboard;
    }

    public static Dashboard Load(String json)
    {
        try {
            return IDatabaseObject.Load(Dashboard.class, json);
        } catch (Exception e) {
            return new Dashboard();
        }
    }
}
