package shane.pennihome.local.smartboard.Data.SQL;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import shane.pennihome.local.smartboard.Data.Block;
import shane.pennihome.local.smartboard.Data.Dashboard;
import shane.pennihome.local.smartboard.Data.Interface.IDatabaseObject;

/**
 * Created by shane on 13/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class DBEngine extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "smartboard";
    private Context mContext;

    public DBEngine(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Queries.getCreateDatastore());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void WriteToDatabase(IDatabaseObject data) {
        ExecuteSQL(Queries.getUpdateDatastore(data));
    }

    public List<IDatabaseObject> ReadAllFromDatabase() {
        return getDataObjects(Queries.getAllDatastores());
    }

    public IDatabaseObject readFromDatabase(String Id) {
        List<IDatabaseObject> items = getDataObjects(Queries.getDatastore(Id));
        return items.size() == 0 ? null : items.get(0);
    }

    public List<IDatabaseObject> readFromDatabaseByType(@SuppressWarnings("SameParameterValue") IDatabaseObject.Types type) {
        return getDataObjects(Queries.getDatastoreByType(type));
    }

    public void CleanDataStore() {
        ExecuteSQL(Queries.getCleanDatastore());
    }

    private void ExecuteSQL(String query) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.execSQL(query);
        } catch (Exception ex) {
            if (mContext != null)
                Toast.makeText(mContext, "Write Error : " + ex.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (db != null)
                db.close();
        }
    }

    List<IDatabaseObject> getDataObjects(String query) {
        SQLiteDatabase db = null;
        try {
            List<IDatabaseObject> items = new ArrayList<>();

            db = this.getReadableDatabase();
            Cursor c = db.rawQuery(query, null);
            while (c.moveToNext()) {
                switch (IDatabaseObject.Types.valueOf(c.getString(1))) {
                    case Dashboard:
                        items.add(Dashboard.Load(c.getString(2)));
                        break;
                    case Block:
                        items.add(Block.Load(c.getString(2)));
                        break;
                }
            }
            c.close();
            return items;

        } finally {
            if (db != null)
                db.close();
        }
    }

    static class Queries {
        @SuppressWarnings("SameReturnValue")
        public static String getCreateDatastore() {
            return "CREATE TABLE IF NOT EXISTS datastore (id text PRIMARY KEY unique,type text not null, object text not null)";
        }

        public static String getCleanDatastore() {
            return "delete from datastore ";
        }

        public static String getUpdateDatastore(IDatabaseObject object) {
            return String.format("replace into datastore (id, type, object) values('%s','%s','%s')", object.getID(), object.getType(), object.toJson());
        }

        @SuppressWarnings("SameReturnValue")
        public static String getAllDatastores() {
            return "select id, type, object from datastore";
        }

        public static String getDatastore(String Id) {
            return String.format("select id, type, object from datastore where id = '%s'", Id);
        }

        public static String getDatastoreByType(IDatabaseObject.Types type) {
            return String.format("select id, type, object from datastore where type = '%s'", type);
        }
    }

}
