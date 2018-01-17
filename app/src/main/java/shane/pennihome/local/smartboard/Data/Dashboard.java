package shane.pennihome.local.smartboard.Data;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import shane.pennihome.local.smartboard.Data.Interface.IDatabaseObject;
import shane.pennihome.local.smartboard.Data.Interface.IThing;

/**
 * Created by shane on 13/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Dashboard extends IDatabaseObject {
    private final List<Group> mGroups = new ArrayList<>();

    public static Dashboard Load(String json) {
        Dashboard ret = new Dashboard();
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(IThing.class, new JsonDeserializer<IThing>() {
                @Override
                public IThing deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    JsonObject jThing = json.getAsJsonObject();

                    switch (jThing.get("mInstance").getAsString().toLowerCase()) {
                        case "device":
                            return Device.Load(jThing.toString());
                        case "routine":
                            return Routine.Load(jThing.toString());
                        default:
                            throw new JsonParseException("Invalid type of thing : " + jThing.get("mInstance").getAsString());
                    }
                }
            });

            Gson gson = builder.create();
            ret = gson.fromJson(json, Dashboard.class);
        } catch (Exception e) {
            Log.e("Smartboard", "Error : " + e.getMessage());
        }

        return ret;
    }

    public List<Group> getRows() {
        return mGroups;
    }

    @Override
    public Types getType() {
        return Types.Dashboard;
    }

    public Group getRowAt(int index) {
        return mGroups.get(index);
    }

}
