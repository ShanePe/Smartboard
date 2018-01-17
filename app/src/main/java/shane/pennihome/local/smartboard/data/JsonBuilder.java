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

import shane.pennihome.local.smartboard.blocks.interfaces.IBlock;
import shane.pennihome.local.smartboard.blocks.switchblock.SwitchBlock;
import shane.pennihome.local.smartboard.things.Interface.IThing;
import shane.pennihome.local.smartboard.things.Routine.Routine;
import shane.pennihome.local.smartboard.things.Switch.Switch;

/**
 * Created by shane on 17/01/18.
 */

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
                if (src instanceof Routine)
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
                else
                    throw new JsonParseException("Invalid type of block : " + src.toString());
            }
        });

        return builder.create();
    }
}
