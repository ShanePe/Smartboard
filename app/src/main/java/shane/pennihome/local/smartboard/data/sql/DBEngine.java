package shane.pennihome.local.smartboard.data.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import shane.pennihome.local.smartboard.data.Dashboard;
import shane.pennihome.local.smartboard.data.Options;
import shane.pennihome.local.smartboard.data.Template;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.services.interfaces.IService;

/**
 * Created by shane on 13/01/18.
 */

@SuppressWarnings({"unused"})
public class DBEngine extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "smartboard";
    private final Context mContext;

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

    public void writeToDatabase(IDatabaseObject data) {
        ExecuteSQL(Queries.getUpdateDatastore(data));
    }

    public List<IDatabaseObject> readAllFromDatabase() {
        return getDataObjects(Queries.getAllDatastores());
    }

    public IDatabaseObject readFromDatabase(String Id) {
        List<IDatabaseObject> items = getDataObjects(Queries.getDatastore(Id));
        assert items != null;
        return items.size() == 0 ? null : items.get(0);
    }

    public List<IDatabaseObject> readFromDatabaseByType(@SuppressWarnings("SameParameterValue") IDatabaseObject.Types type) {
        return getDataObjects(Queries.getDatastoreByType(type));
    }

    public void cleanDataStore() {
        ExecuteSQL(Queries.getDeleteDatastore());
        ExecuteSQL(Queries.getDropDatastore());
        ExecuteSQL(Queries.getCreateDatastore());
    }

    public void deleteFromDatabase(IDatabaseObject data) {
        ExecuteSQL(Queries.getDeleteFromDatastore(data));
    }

    public void updatePosition(IDatabaseObject object) {
        ExecuteSQL(Queries.getUpdatePosition(object));
    }

    private void ExecuteSQL(String query) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.execSQL(query);
        } catch (Exception ex) {
            if (mContext != null)
                Toast.makeText(mContext, "Write Error : " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private List<IDatabaseObject> getDataObjects(String query) {
        SQLiteDatabase db = null;
        try {
            List<IDatabaseObject> items = new ArrayList<>();

            db = this.getReadableDatabase();
            Cursor c = db.rawQuery(query, null);
            while (c.moveToNext()) {
                IDatabaseObject dbO;
                switch (IDatabaseObject.Types.valueOf(c.getString(1))) {
                    case Dashboard:
                        dbO = Dashboard.Load(c.getString(3));
                        break;
                    case Service:
                        dbO = IService.fromJson(IService.class, c.getString(3));
                        break;
                    case Template:
                        dbO = Template.Load(Template.class, c.getString(3));
                        break;
                    case Options:
                        dbO = Options.Load(Options.class, c.getString(3));
                        break;
                    default:
                        throw new Exception("Invalid Database Type : " + c.getString(1));
                }
                if (dbO != null) {
                    dbO.setPosition(c.getInt(2));
                    items.add(dbO);
                }
            }
            c.close();
            return items;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (db != null)
                db.close();
        }
    }

    @SuppressWarnings("SameReturnValue")
    static class Queries {
        @SuppressWarnings("SameReturnValue")
        static String getCreateDatastore() {
            return "CREATE TABLE IF NOT EXISTS datastore (id text PRIMARY KEY unique,type text not null, position integer, object text not null)";
        }

        static String getDropDatastore() {
            return "drop table datastore";
        }

        static String getDeleteDatastore() {
            return "delete from datastore ";
        }

        static String getDeleteFromDatastore(IDatabaseObject object) {
            return String.format("Delete from datastore where id='%s'", object.getDataID());
        }

        static String getUpdateDatastore(IDatabaseObject object) {
            return String.format("replace into datastore (id, type, position, object) values('%s','%s', %s, '%s')",
                    object.getDataID(),
                    object.getDatabaseType(),
                    object.getPosition(),
                    object.toJson());
        }

        @SuppressWarnings("SameReturnValue")
        static String getAllDatastores() {
            return "select id, type, position, object from datastore order by position";
        }

        static String getDatastore(String Id) {
            return String.format("select id, type, position, object from datastore where id = '%s'", Id);
        }

        static String getDatastoreByType(IDatabaseObject.Types type) {
            return String.format("select id, type, position, object from datastore where type = '%s' order by position", type);
        }

        static String getUpdatePosition(IDatabaseObject object) {
            return String.format("update datastore set position = %s where id = '%s'", object.getPosition(), object.getDataID());
        }
    }
}
