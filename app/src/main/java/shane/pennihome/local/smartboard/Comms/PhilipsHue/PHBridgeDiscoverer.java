package shane.pennihome.local.smartboard.Comms.PhilipsHue;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import shane.pennihome.local.smartboard.Comms.Interface.ICommunicator;
import shane.pennihome.local.smartboard.Comms.Interface.OnCommResponseListener;
import shane.pennihome.local.smartboard.Comms.Interface.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.Comms.RESTCommunicator;
import shane.pennihome.local.smartboard.Comms.RESTCommunicatorResult;
import shane.pennihome.local.smartboard.Data.Globals;
import shane.pennihome.local.smartboard.Data.HueBridge;

/**
 * Created by shane on 30/12/17.
 */

@SuppressWarnings("ALL")
public class PHBridgeDiscoverer extends ICommunicator<PHBridgeDiscoverer> {
    private final List<HueBridge> mBridgeDiscoveryResults = new ArrayList<>();

    public PHBridgeDiscoverer(Context mContext, OnProcessCompleteListener<PHBridgeDiscoverer> mProcessCompleteListener) {
        super(mContext, mProcessCompleteListener);
    }

    public List<HueBridge> getBridgeDiscoveryResults() {
        return mBridgeDiscoveryResults;
    }

    @Override
    public void PreProcess() {
        super.PreProcess();
        mBridgeDiscoveryResults.clear();
    }

    @Override
    public String getFailedMessage() {
        return "Could not discover Hue Bridges";
    }

    @Override
    public String getDialogMessage() {
        return "Scanning for Hue Bridges on the network...";
    }

    @Override
    public JSONObject Process() throws Exception {
        JSONObject jRet = new JSONObject();
        final JSONArray bridges = new JSONArray();
        RESTCommunicator httpCommunicator = new RESTCommunicator();
        httpCommunicator.getJson(Globals.PH_DISCOVER_URL, new OnCommResponseListener() {
            @Override
            public void process(JSONObject obj) {
                bridges.put(obj);
            }
        });
        jRet.put("bridges", bridges);
        return jRet;
    }

    @Override
    public void Complete(RESTCommunicatorResult result) throws Exception {
        JSONObject jres = result.getResult();
        JSONArray bridges = jres.getJSONArray("bridges");
        for (int i = 0; i < bridges.length(); i++) {
            JSONObject jBrid = bridges.getJSONObject(i);
            HueBridge b = new HueBridge();
            b.setId(jBrid.getString("id"));
            b.setIp(jBrid.getString("internalipaddress"));

            mBridgeDiscoveryResults.add(b);
        }
    }
}