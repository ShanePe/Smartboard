package shane.pennihome.local.smartboard.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.things.routines.block.RoutineBlock;
import shane.pennihome.local.smartboard.things.switches.block.SwitchBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.things.routines.Routine;
import shane.pennihome.local.smartboard.things.switches.Switch;

/**
 * Created by shane on 17/01/18.
 */

@SuppressWarnings({"ALL", "RedundantCast"})
public class JsonBuilder {
    public static Gson Get() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(IThing.class, new JsonDeserializer<IThing>() {
            @Override
            public IThing deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jThing = json.getAsJsonObject();

                switch (jThing.get("mInstance").getAsString().toLowerCase()) {
                    case "switch":
                        return Switch.Load(jThing.toString());
                    case "routine":
                        return Routine.Load(jThing.toString());
                    default:
                        throw new JsonParseException("Invalid type of thing : " + jThing.get("mInstance").getAsString());
                }
            }
        });

        builder.registerTypeAdapter(IBlock.class, new JsonDeserializer<IBlock>() {
            @Override
            public IBlock deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jBlock = json.getAsJsonObject();

                switch (jBlock.get("mInstance").getAsString().toLowerCase()) {
                    case "switchblock":
                        return SwitchBlock.Load(jBlock.toString());
                    case "routineblock":
                        return RoutineBlock.Load(jBlock.toString());
                    default:
                        throw new JsonParseException("Invalid type of block : " + jBlock.get("mInstance").getAsString());
                }
            }
        });

        builder.registerTypeAdapter(IThing.class, new JsonSerializer<IThing>() {
            @Override
            public JsonElement serialize(IThing src, Type typeOfSrc, JsonSerializationContext context) {
                if (src instanceof Switch)
                    return context.serialize((Switch) src);
                else if (src instanceof Routine)
                    return context.serialize((Routine) src);
                else
                    throw new JsonParseException("Invalid type of thing : " + src.toString());
            }
        });

        builder.registerTypeAdapter(IBlock.class, new JsonSerializer<IBlock>() {
            @Override
            public JsonElement serialize(IBlock src, Type typeOfSrc, JsonSerializationContext context) {
                if (src instanceof SwitchBlock)
                    return context.serialize((SwitchBlock) src);
                else if (src instanceof RoutineBlock)
                    return context.serialize((RoutineBlock) src);
                else
                    throw new JsonParseException("Invalid type of block : " + src.toString());
            }
        });

        return builder.create();
    }
}
