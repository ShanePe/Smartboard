package shane.pennihome.local.smartboard.services.PhilipsHue;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.comms.JsonExecutor;
import shane.pennihome.local.smartboard.comms.JsonExecutorRequest;
import shane.pennihome.local.smartboard.comms.JsonExecutorResult;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Globals;
import shane.pennihome.local.smartboard.data.NameValuePair;
import shane.pennihome.local.smartboard.services.interfaces.IRegisterServiceFragment;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.services.interfaces.IThingsGetter;
import shane.pennihome.local.smartboard.things.routines.Routine;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IExecutor;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.ui.UIHelper;

/**
 * Created by SPennicott on 02/02/2018.
 */

public class HueBridgeService extends IService {
    private final static String PH_DISCOVER_URL = "https://www.meethue.com/api/nupnp";
    private final static String PH_DISCOVER_V2_URL = "https://discovery.meethue.com/";
    private String mAddress;
    private String mToken;

    public static HueBridgeService Load(String json) {
        try {
            return IService.fromJson(HueBridgeService.class, json);
        } catch (Exception e) {
            return new HueBridgeService();
        }
    }

    private String getAddress() {
        return mAddress;
    }

    void setAddress(String address) {
        if (mAddress == null)
            mAddress = "";
        this.mAddress = address;
    }

    private String getToken() {
        if (mToken == null)
            mToken = "";
        return mToken;
    }

    private void setToken(String token) {
        this.mToken = token;
    }

    @Override
    public String getName() {
        return "Philips Hue";
    }

    @Override
    public IRegisterServiceFragment getRegisterDialog() {
        return new HueBridgeFragment();
    }

    @Override
    public int getDrawableIconResource() {
        return R.mipmap.icon_phlogo_large_mm_fg;
    }

    @Override
    public void register(final Context context, final OnProcessCompleteListener<IService> onProcessCompleteListener) {
        RegisterHandler handler = new RegisterHandler(context, new Connector(), new OnProcessCompleteListener<Exception>() {
            @Override
            public void complete(boolean success, Exception source) {
                if (success)
                    HueBridgeService.super.register(context, onProcessCompleteListener);
                else
                    Toast.makeText(context, "Error : " + source.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        handler.execute();

    }

    @Override
    protected boolean isRegistered() {
        return !TextUtils.isEmpty(getAddress()) && !TextUtils.isEmpty(getToken());
    }

    @Override
    public boolean isAwaitingAction() {
        return !TextUtils.isEmpty(getAddress()) && TextUtils.isEmpty(getToken());
    }

    @Override
    public void connect() throws Exception {
        Connector connector = new Connector();
        connector.getThings();
    }

    @Override
    public ArrayList<IThingsGetter> getThingGetters() {
        ArrayList<IThingsGetter> thingsGetters = new ArrayList<>();
        if (isAwaitingAction() || !isRegistered())
            thingsGetters.add(new Connector());
        thingsGetters.add(new SwitchGetterV2());
        thingsGetters.add(new RoutineGetterV2());
        return thingsGetters;
    }

    @Override
    public ServicesTypes getServiceType() {
        return ServicesTypes.PhilipsHue;
    }

    @Override
    public Types getDatabaseType() {
        return Types.Service;
    }

    ArrayList<HueBridge> discover() throws Exception {
        ArrayList<HueBridge> bridges = new ArrayList<>();
        JsonExecutorResult result = JsonExecutor.fulfil(new JsonExecutorRequest(new URL(PH_DISCOVER_V2_URL), JsonExecutorRequest.Types.GET));

        if (!result.isSuccess())
            throw result.getError();

        JSONArray jBridges = new JSONArray(result.getResult());
        if (jBridges.length() == 0)
            throw new Error("No bridges found");

        for (int i = 0; i < jBridges.length(); i++) {
            JSONObject jBrid = jBridges.getJSONObject(i);
            bridges.add(new HueBridge(jBrid.getString("id"), jBrid.getString("internalipaddress")));
        }
        return bridges;
    }

    private JSONObject GetEndPointV2(String endPoint) throws Exception {
        JsonExecutorRequest request = new JsonExecutorRequest(new URL(String.format("https://%s/clip/v2/resource/%s", getAddress(), endPoint)), JsonExecutorRequest.Types.GET);
        request.getHeaders().add(new NameValuePair("hue-application-key", getToken()));

        JsonExecutorResult result = JsonExecutor.fulfil(request);
        if (!result.isSuccess())
            throw result.getError();

        JSONObject jResult = result.getResultAsJsonObject();
        if (jResult.has("errors"))
            if (jResult.getJSONArray("errors").length() > 0)
                throw new Exception(jResult.getJSONArray("errors").getJSONObject(0).getString("description"));
        return jResult;
    }

    private void IterateEndPointV2(String endPoint, EndPointItemIterate endPointItemIterate) throws Exception {
        JSONArray array = GetEndPointV2(endPoint).getJSONArray("data");
        for (int i = 0; i < array.length(); i++)
            endPointItemIterate.Item(array.getJSONObject(i));
    }

    private JsonExecutorResult PutEndPointV2(IThing thing, String resource, String body) {
        try {
            JsonExecutorRequest request = new JsonExecutorRequest(new URL(String.format("https://%s/clip/v2/resource/%s/%s", getAddress(), resource, thing.getId())), JsonExecutorRequest.Types.PUT);
            request.getHeaders().add(new NameValuePair("hue-application-key", getToken()));
            request.setPutBody(body);

            return JsonExecutor.fulfil(request);

        } catch (Exception e) {
            return new JsonExecutorResult(e);
        }
    }

    private interface EndPointItemIterate {
        void Item(JSONObject data) throws JSONException;
    }

    private static class RegisterHandler extends AsyncTask<Void, Void, Exception> {
        private final WeakReference<Context> mContext;
        private final WeakReference<Connector> mConnector;
        private final OnProcessCompleteListener<Exception> mOnProcessCompleteListener;
        private HueBridgeLinkDialog mDialog;

        RegisterHandler(Context context, Connector connector, OnProcessCompleteListener<Exception> onProcessCompleteListener) {
            this.mContext = new WeakReference<>(context);
            this.mConnector = new WeakReference<>(connector);
            this.mOnProcessCompleteListener = onProcessCompleteListener;
        }

        @Override
        protected void onPreExecute() {
            mDialog = new HueBridgeLinkDialog();
            mDialog.setDescription(mConnector.get().getLoadMessage());
            mDialog.show(((AppCompatActivity) mContext.get()).getSupportFragmentManager(), "Hue_wait");
            mDialog.setOnLoadCompleteListener(new OnProcessCompleteListener<TextView>() {
                @Override
                public void complete(boolean success, TextView source) {
                    mConnector.get().setDescriptionTextView(source);
                }
            });
            mDialog.setOnCancelClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mConnector.get().cancel();
                }
            });
        }

        @Override
        protected void onPostExecute(Exception e) {
            mDialog.dismiss();
            mOnProcessCompleteListener.complete(e == null, e);
        }

        @Override
        protected Exception doInBackground(Void... voids) {
            try {
                mConnector.get().getThings();
                return null;
            } catch (Exception ex) {
                return ex;
            }
        }
    }

    public class Connector implements IThingsGetter {
        TextView mTextDesc;
        private boolean mCancel;

        @Override
        public String getLoadMessage() {
            return "Connecting to Philips Hue Bridge";
        }

        void cancel() {
            UpdateDialog("Cancelling ...");
            mCancel = true;

        }

        private void UpdateDialog(final String msg) {
            Log.i(String.format("%s %s", (mTextDesc == null), msg), "testing");
            if (mTextDesc != null)
                mTextDesc.post(new Runnable() {
                    @Override
                    public void run() {
                        mTextDesc.setText(msg);
                    }
                });
        }

        @Override
        public Things getThings() throws Exception {
            URL url = new URL(String.format("http://%s/api", getAddress()));
            JSONObject jPost = new JSONObject(String.format("{\"devicetype\":\"%s#%s\"}", Globals.ACTIVITY, Globals.getSharedPreferences().getString("uid", "unknown")));
            JsonExecutorRequest request = new JsonExecutorRequest(url, JsonExecutorRequest.Types.POST);
            request.setPostJson(jPost);

            JsonExecutorResult result = JsonExecutor.fulfil(request);

            if (!result.isSuccess())
                throw result.getError();

            JSONObject jObj = result.getResultAsJsonObject();
            if (jObj.has("error")) {
                JSONObject jError = jObj.getJSONObject("error");

                int loopCount = 0;
                int errorCode = jError.getInt("type");
                while (errorCode == 101) {
                    if (mCancel)
                        throw new Exception("Cancelled by user.");

                    if (loopCount > 36)
                        throw new Exception("Timeout waiting for authorisation push.");

                    UpdateDialog("Please press the link button on the Hue Bridge.");
                    Thread.sleep(5000);

                    result = JsonExecutor.fulfil(request);
                    if (!result.isSuccess())
                        throw result.getError();

                    jObj = result.getResultAsJsonObject();
                    if (jObj.has("error")) {
                        jError = jObj.getJSONObject("error");

                        errorCode = jError.getInt("type");
                        if (errorCode != 101)
                            throw new Exception(jError.getString("description"));
                    } else
                        break;
                    loopCount += 1;
                }
            }

            if (jObj.has("success")) {
                {
                    JSONObject jSuc = jObj.getJSONObject("success");
                    setToken(jSuc.getString("username"));
                }
            } else
                throw new Exception("Did not get authorisation for Hue Bridge");

            return new Things();
        }

        @Override
        public int getUniqueId() {
            return 4;
        }

        @Override
        public void setDescriptionTextView(TextView txtDescription) {
            mTextDesc = txtDescription;
        }

        @Override
        public Type getThingType() {
            return IThing.class;
        }

        @Override
        public IExecutor<?> getExecutor(String Id) {
            return null;
        }

    }

    public class SwitchGetterV2 implements IThingsGetter {
        @Override
        public String getLoadMessage() {
            return "Getting Philips Hue lights";
        }


        private Things getGroupedLights(String group, final HashMap<String, Boolean> groupState) throws Exception {
            final Things things = new Things();

            IterateEndPointV2(group, new EndPointItemIterate() {
                @Override
                public void Item(JSONObject data) throws JSONException {
                    if (data.has("grouped_services")) {
                        JSONArray grouped = data.getJSONArray("grouped_services");
                        for (int i = 0; i < grouped.length(); i++) {
                            JSONObject service = grouped.getJSONObject(i);
                            if (service.getString("rtype").equals("grouped_light")) {
                                Switch s = new Switch();
                                s.setId(service.getString("rid"));
                                s.setName(data.getJSONObject("metadata").getString("name"));
                                s.setOn(groupState.containsKey(s.getId()) ? groupState.get(s.getId()) : false, false);
                                s.setType(data.getString("type"));
                                s.setService(ServicesTypes.PhilipsHue);
                                s.setResource("grouped_light");

                                s.initialise();
                                things.add(s);

                                break;
                            }
                        }
                    }
                }
            });

            return things;
        }

        @Override
        public Things getThings() throws Exception {
            final Things things = new Things();
            final HashMap<String, Boolean> groupState = new HashMap<>();

            IterateEndPointV2("light", new EndPointItemIterate() {
                @Override
                public void Item(JSONObject data) throws JSONException {
                    Switch d = new Switch();
                    d.setId(data.getString("id"));
                    d.setName(data.getJSONObject("metadata").getString("name"));
                    d.setUnreachable(false, false);
                    d.setOn(data.getJSONObject("on").getBoolean("on"), false);
                    d.setType(data.getString("type"));
                    d.setService(ServicesTypes.PhilipsHue);
                    d.setResource("light");
                    if (data.has("dimming")) {
                        d.setIsDimmer(true);
                        d.setDimmerLevel(Math.round((float) data.getJSONObject("dimming").getDouble("brightness")), false);
                    }
                    if (data.has("color")) {
                        JSONObject color = data.getJSONObject("color").getJSONObject("xy");
                        JSONObject dim = data.getJSONObject("dimming");

                        UIHelper.PhilipsHueRgbObject rgb = UIHelper.xyBriToRgb(color.getDouble("x"), color.getDouble("y"),
                                convertLevelTo(dim.getDouble("brightness")));

                        d.setSupportsColour(true, false);
                        d.setCurrentColour(Color.rgb(rgb.getRed(), rgb.getGreen(), rgb.getBlue()), false);
                    }

                    d.initialise();
                    things.add(d);
                }
            });

            IterateEndPointV2("grouped_light", new EndPointItemIterate() {
                @Override
                public void Item(JSONObject data) throws JSONException {
                    groupState.put(data.getString("id"), data.getJSONObject("on").getBoolean("on"));
                }
            });

            things.addAll(getGroupedLights("room", groupState));
            things.addAll(getGroupedLights("zone", groupState));

            return things;

        }

        @Override
        public int getUniqueId() {
            return 5;
        }

        @Override
        public void setDescriptionTextView(TextView txtDescription) {

        }

        @Override
        public Type getThingType() {
            return Switch.class;
        }

        @Override
        public IExecutor<?> getExecutor(String Id) {
            if ("level".equals(Id)) {
                return new LevelExecutor();
            }
            return new OnOffExecutor();
        }

        private double convertLevelTo(double lvl) {
            double ret = Math.round(((double) lvl * 255.0) / 100.0);
            if (ret < 0.0)
                ret = 0.0;
            else if (ret > 255.0)
                ret = 255.0;
            return ret;
        }

        public class OnOffExecutor extends IExecutor<Void> {

            @Override
            protected JsonExecutorResult execute(IThing thing) {
                return PutEndPointV2(thing, ((Switch) thing).getResource(), String.format("{\"on\":{\"on\":%s}}", !((Switch) thing).isOn()));
            }
        }

        public class LevelExecutor extends IExecutor<Integer> {

            @Override
            protected JsonExecutorResult execute(IThing thing) {
                return PutEndPointV2(thing, "light", String.format("{\"dimming\": {\"brightness\": %s}}", getValue()));
            }
        }
    }

    public class SwitchGetter implements IThingsGetter {

        @Override
        public String getLoadMessage() {
            return "Getting Philips Hue lights";
        }

        @Override
        public Things getThings() throws Exception {
            Things things = new Things();

            JsonExecutorResult result = JsonExecutor.fulfil(new JsonExecutorRequest(new URL(String.format("http://%s/api/%s/lights", getAddress(), getToken())), JsonExecutorRequest.Types.GET));
            if (!result.isSuccess())
                throw result.getError();

            JSONObject jDevices = result.getResultAsJsonObject();

            Iterator<String> iterator = jDevices.keys();
            while (iterator.hasNext()) {
                String k = iterator.next();
                JSONObject jDev = jDevices.getJSONObject(k);
                JSONObject jState = jDev.getJSONObject("state");

                Switch d = new Switch();
                d.setId(k);
                d.setName(jDev.getString("name"));
                d.setUnreachable(!jState.getBoolean("reachable"), false);
                d.setOn(jState.getBoolean("on"), false);
                d.setType(jDev.getString("type"));
                d.setService(ServicesTypes.PhilipsHue);
                if (jState.has("bri")) {
                    d.setIsDimmer(true);
                    d.setDimmerLevel(convertLevelFrom(jState), false);
                }

                d.initialise();
                things.add(d);
            }


            return things;
        }

        private int convertLevelFrom(JSONObject jState) throws JSONException {
            String level = jState.getString("bri");
            int lvl = Math.round((Float.parseFloat(level) / 255f) * 100f);
            if (lvl < 0)
                lvl = 0;
            else if (lvl > 100)
                lvl = 100;
            return lvl;
        }

        private int convertLevelTo(int lvl) {
            int ret = Math.round(((float) lvl * 255f) / 100f);
            if (ret < 0)
                ret = 0;
            else if (ret > 255)
                ret = 255;
            return ret;
        }

        @Override
        public int getUniqueId() {
            return 5;
        }

        @Override
        public void setDescriptionTextView(TextView txtDescription) {

        }

        @Override
        public Type getThingType() {
            return Switch.class;
        }

        @Override
        public IExecutor<?> getExecutor(String Id) {
            if (Id.equalsIgnoreCase("level"))
                return new LevelExecutor();

            return new IExecutor<Void>() {

                @Override
                protected JsonExecutorResult execute(IThing thing) {
                    try {
                        JsonExecutorRequest request = new JsonExecutorRequest(new URL(String.format("http://%s/api/%s/lights/%s/state", getAddress(), getToken(), thing.getId())), JsonExecutorRequest.Types.PUT);
                        request.setPutBody(String.format("{\"on\":%s}", !((Switch) thing).isOn()));

                        return JsonExecutor.fulfil(request);

                    } catch (Exception e) {
                        return new JsonExecutorResult(e);
                    }
                }
            };
        }

        public class LevelExecutor extends IExecutor<Integer> {
            @Override
            public String getId() {
                return "level";
            }

            @Override
            protected JsonExecutorResult execute(IThing thing) {
                try {
                    JsonExecutorRequest request = new JsonExecutorRequest(new URL(String.format("http://%s/api/%s/lights/%s/state", getAddress(), getToken(), thing.getId())), JsonExecutorRequest.Types.PUT);
                    request.setPutBody(String.format("{\"bri\":%s}", convertLevelTo(getValue())));

                    return JsonExecutor.fulfil(request);

                } catch (Exception e) {
                    return new JsonExecutorResult(e);
                }
            }
        }

    }

    public class RoutineGetterV2 implements IThingsGetter {


        @Override
        public String getLoadMessage() {
            return "Getting Philips Hue Routines";
        }

        @Override
        public Things getThings() throws Exception {
            final Things things = new Things();
            final HashMap<String, HashMap<String, String>> places = new HashMap<>();
            places.put("zone", new HashMap<String, String>());
            places.put("room", new HashMap<String, String>());

            IterateEndPointV2("room", new EndPointItemIterate() {
                @Override
                public void Item(JSONObject data) throws JSONException {
                    places.get("room").put(data.getString("id"), data.getJSONObject("metadata").getString("name"));
                }
            });

            IterateEndPointV2("zone", new EndPointItemIterate() {
                @Override
                public void Item(JSONObject data) throws JSONException {
                    places.get("zone").put(data.getString("id"), data.getJSONObject("metadata").getString("name"));
                }
            });

            IterateEndPointV2("scene", new EndPointItemIterate() {
                @Override
                public void Item(JSONObject data) throws JSONException {
                    JSONObject group = data.getJSONObject("group");
                    String n = places.containsKey(group.getString("rtype")) ?
                            places.get(group.getString("rtype")).get(group.getString("rid")) :
                            "All";

                    Routine r = new Routine();
                    r.setId(data.getString("id"));
                    r.setName(n + " : " + data.getJSONObject("metadata").getString("name"));
                    r.setService(IService.ServicesTypes.PhilipsHue);
                    r.initialise();
                    things.add(r);
                }
            });

            return things;
        }

        @Override
        public int getUniqueId() {
            return 6;
        }

        @Override
        public void setDescriptionTextView(TextView txtDescription) {

        }

        @Override
        public Type getThingType() {
            return Routine.class;
        }

        @Override
        public IExecutor<?> getExecutor(String Id) {
            return new IExecutor<Void>() {
                @Override
                protected JsonExecutorResult execute(IThing thing) {
                    return PutEndPointV2(thing, "scene", "{\"recall\": {\"action\": \"active\"}}");
                }
            };
        }
    }

    public class RoutineGetter implements IThingsGetter {

        @Override
        public String getLoadMessage() {
            return "Getting Philips Hue Routines";
        }

        @Override
        public Things getThings() throws Exception {
            Things things = new Things();
            JsonExecutorResult groupResult = JsonExecutor.fulfil(new JsonExecutorRequest(new URL(String.format("http://%s/api/%s/groups", getAddress(), getToken())), JsonExecutorRequest.Types.GET));
            if (!groupResult.isSuccess())
                throw groupResult.getError();

            JsonExecutorResult sceneResult = JsonExecutor.fulfil(new JsonExecutorRequest(new URL(String.format("http://%s/api/%s/scenes", getAddress(), getToken())), JsonExecutorRequest.Types.GET));
            if (!sceneResult.isSuccess())
                throw sceneResult.getError();

            JsonExecutorResult linkResult = JsonExecutor.fulfil(new JsonExecutorRequest(new URL(String.format("http://%s/api/%s/resourcelinks", getAddress(), getToken())), JsonExecutorRequest.Types.GET));
            if (!linkResult.isSuccess())
                throw linkResult.getError();

            final JSONArray groups = new JSONArray();
            JSONObject jGroup = groupResult.getResultAsJsonObject();
            JSONObject jRoutine = sceneResult.getResultAsJsonObject();

            Iterator<String> iterator = jGroup.keys();

            while (iterator.hasNext()) {
                String k = iterator.next();
                JSONObject jGp = jGroup.getJSONObject(k);
                jGp.put("id", k);
                jGp.put("lkey", buildLightKey(jGp.getJSONArray("lights")));
                groups.put(jGp);
            }

            final JSONArray routines = new JSONArray();

            iterator = jRoutine.keys();
            while (iterator.hasNext()) {
                String k = iterator.next();
                JSONObject jRt = jRoutine.getJSONObject(k);
                String sLightKey = buildLightKey(jRt.getJSONArray("lights"));
                jRt.put("id", k);
                jRt.put("lkey", sLightKey);

                JSONArray relGroups = new JSONArray();
                for (int x = 0; x < groups.length(); x++) {
                    JSONObject jG = groups.getJSONObject(x);
                    if (jG.getString("lkey").equals(sLightKey))
                        relGroups.put(jG);
                }

                jRt.put("groups", relGroups);
                routines.put(jRt);
            }

            for (int i = 0; i < routines.length(); i++) {
                JSONObject jRout = routines.getJSONObject(i);
                JSONArray jGroups = jRout.getJSONArray("groups");

                if (jGroups.length() == 0) {
                    Routine r = new Routine();
                    r.setId(jRout.getString("id"));
                    r.setName(jRout.getString("name") + " in all");
                    r.setService(IService.ServicesTypes.PhilipsHue);
                    r.initialise();
                    things.add(r);
                } else {
                    for (int x = 0; x < jGroups.length(); x++) {
                        JSONObject jgrp = jGroups.getJSONObject(x);
                        Routine r = new Routine();
                        r.setId(jRout.getString("id"));
                        r.setName(jRout.getString("name") + " in " + jgrp.getString("name"));
                        r.setService(IService.ServicesTypes.PhilipsHue);
                        r.initialise();
                        things.add(r);
                    }
                }
            }

            return things;
        }

        private String buildLightKey(JSONArray array) throws JSONException {
            ArrayList<Integer> keys = new ArrayList<>();
            for (int i = 0; i < array.length(); i++)
                keys.add(Integer.valueOf(array.getString(i)));
            Collections.sort(keys);
            StringBuilder retKey = new StringBuilder();
            for (int i : keys)
                retKey.append(i);
            return retKey.toString();
        }

        @Override
        public int getUniqueId() {
            return 6;
        }

        @Override
        public void setDescriptionTextView(TextView txtDescription) {

        }

        @Override
        public Type getThingType() {
            return Routine.class;
        }

        @Override
        public IExecutor<?> getExecutor(String Id) {
            return new IExecutor<Void>() {

                @Override
                protected JsonExecutorResult execute(IThing thing) {
                    try {
                        JsonExecutorRequest request = new JsonExecutorRequest(new URL(String.format("http://%s/api/%s/groups/0/action", getAddress(), getToken())), JsonExecutorRequest.Types.PUT);
                        request.setPutBody(String.format("{\"scene\":\"%s\"}", thing.getId()));

                        return JsonExecutor.fulfil(request);

                    } catch (Exception e) {
                        return new JsonExecutorResult(e);
                    }
                }
            };
        }
    }
}
