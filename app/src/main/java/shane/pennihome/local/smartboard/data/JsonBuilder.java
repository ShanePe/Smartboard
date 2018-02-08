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

import shane.pennihome.local.smartboard.comms.interfaces.IMessage;
import shane.pennihome.local.smartboard.services.PhilipsHue.HueBridgeService;
import shane.pennihome.local.smartboard.services.SmartThings.SmartThingsService;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.things.routines.Routine;
import shane.pennihome.local.smartboard.things.routines.RoutineBlock;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.things.switches.SwitchBlock;
import shane.pennihome.local.smartboard.thingsframework.ThingChangedMessage;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 17/01/18.
 */

@SuppressWarnings({"ALL", "RedundantCast"})
public class JsonBuilder {
    public static Gson get() {
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


        builder.registerTypeAdapter(IService.class, new JsonDeserializer<IService>() {
            @Override
            public IService deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jService = json.getAsJsonObject();

                switch (jService.get("mInstance").getAsString().toLowerCase()) {
                    case "smartthingsservice":
                        return SmartThingsService.Load(jService.toString());
                    case "huebridgeservice":
                        return HueBridgeService.Load(jService.toString());
                    default:
                        throw new JsonParseException("Invalid type of service : " + jService.get("mInstance").getAsString());
                }
            }
        });

        builder.registerTypeAdapter(IMessage.class, new JsonDeserializer<IMessage<?>>() {
            @Override
            public IMessage<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jMessage = json.getAsJsonObject();

                switch (jMessage.get("mInstance").getAsString().toLowerCase()) {
                    case "thingchangedmessage":
                        return ThingChangedMessage.Load(jMessage.toString());
                    default:
                        throw new JsonParseException("Invalid type of message : " + jMessage.get("mInstance").getAsString());
                }
            }
        });

        builder.registerTypeAdapter(IMessage.class, new JsonSerializer<IMessage<?>>() {
            @Override
            public JsonElement serialize(IMessage<?> src, Type typeOfSrc, JsonSerializationContext context) {
                if (src instanceof ThingChangedMessage)
                    return context.serialize((ThingChangedMessage) src);
                else
                    throw new JsonParseException("Invalid type of message : " + src.toString());
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

        builder.registerTypeAdapter(IService.class, new JsonSerializer<IService>() {
            @Override
            public JsonElement serialize(IService src, Type typeOfSrc, JsonSerializationContext context) {
                if (src instanceof SmartThingsService)
                    return context.serialize((SmartThingsService) src);
                if(src instanceof HueBridgeService)
                    return context.serialize((HueBridgeService) src);
                else
                    throw new JsonParseException("Invalid type of service : " + src.toString());
            }
        });

        return builder.create();
    }
}
