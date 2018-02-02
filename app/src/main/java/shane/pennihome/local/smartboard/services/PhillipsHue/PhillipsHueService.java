package shane.pennihome.local.smartboard.services.PhillipsHue;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.comms.Executor;
import shane.pennihome.local.smartboard.comms.ExecutorRequest;
import shane.pennihome.local.smartboard.comms.ExecutorResult;
import shane.pennihome.local.smartboard.data.Globals;
import shane.pennihome.local.smartboard.services.interfaces.IRegisterServiceFragment;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.services.interfaces.IThingsGetter;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by SPennicott on 02/02/2018.
 */

public class PhillipsHueService extends IService {
    public final static String PH_DISCOVER_URL = "https://www.meethue.com/api/nupnp";
    private String mAddress;
    private String mToken;

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        if(mAddress == null)
            mAddress = "";
        this.mAddress = address;
    }

    public String getToken() {
        if(mToken == null)
            mToken = "";
        return mToken;
    }

    public void setToken(String token) {
        this.mToken = token;
    }

    @Override
    public IRegisterServiceFragment getRegisterDialog() {
        return new HueBridgeFragment();
    }

    @Override
    public int getDrawableIconResource() {
        return R.drawable.icon_phlogo_large;
    }

    @Override
    public void register(Context context) throws Exception {
        Exception error = new RegisterHandler(context, new Connector()).execute().get();
        if(error != null)
            throw error;
    }

    @Override
    protected boolean isRegistered() {
        return (!getAddress().equals("") && !getToken().equals(""));
    }

    @Override
    public boolean isAwaitingAction() {
        return (!getAddress().equals("") && getToken().equals(""));
    }

    @Override
    public void connect() throws Exception {
        Connector connector = new Connector();
        connector.getThings();
    }


    @Override
    public ArrayList<IThingsGetter> getThingGetters() {
        return null;
    }

    @Override
    public Things getThings() throws Exception {
        return null;
    }

    @Override
    public ServicesTypes getServiceType() {
        return ServicesTypes.PhilipsHue;
    }

    @Override
    public <T extends IThing> ArrayList<IThingsGetter> getThingsGetter(Class<T> cls) {
        return null;
    }

    @Override
    protected <V extends IThing> IThingsGetter getThingExecutor(Class<V> cls) {
        return null;
    }

    @Override
    public Types getDatabaseType() {
        return Types.Service;
    }

    public ArrayList<HueBridge> Discover() throws Exception
    {
            ArrayList<HueBridge> bridges = new ArrayList<>();
            ExecutorResult result = Executor.fulfil(new ExecutorRequest(new URL(PH_DISCOVER_URL), ExecutorRequest.Types.GET));

            if(!result.isSuccess())
                throw result.getError();

            JSONArray jBridges = new JSONArray(result.getResult());
            if(jBridges.length() == 0)
                throw new Error("No bridges found");

            for (int i = 0; i < jBridges.length(); i++) {
                JSONObject jBrid = jBridges.getJSONObject(i);
                bridges.add(new HueBridge(jBrid.getString("id"), jBrid.getString("internalipaddress")));
            }
        return bridges;
    }


    private static class RegisterHandler extends AsyncTask<Void, Void, Exception>
    {
        private WeakReference<Context> mContext;
        private WeakReference<Connector> mConnector;
        private AlertDialog mDialog;

        public RegisterHandler(Context context, Connector connector) {
            this.mContext = new WeakReference<Context>(context);
            this.mConnector = new WeakReference<Connector>(connector);
        }

        @Override
        protected void onPreExecute() {
            LayoutInflater inflater = LayoutInflater.from(mContext.get());
            View view = inflater.inflate(R.layout.service_startup, null);
            LinearLayout layout = new LinearLayout(mContext.get());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(view);
            TextView txt = view.findViewById(R.id.sl_description);
            txt.setText(mConnector.get().getLoadMessage());
            mConnector.get().setDescriptionTextView(txt);

            mDialog = new AlertDialog.Builder(mContext.get())
                            .setView(layout)
                            .setCancelable(false)
                            .create();
            mDialog.show();
        }

        @Override
        protected void onPostExecute(Exception e) {
            mDialog.dismiss();
        }

        @Override
        protected Exception doInBackground(Void... voids) {
            try {
                mConnector.get().getThings();
                return null;
            }catch(Exception ex)
            {
                return ex;
            }
        }
    }

    public class Connector implements IThingsGetter
    {
        TextView mTextDesc;
        @Override
        public String getLoadMessage() {
            return "Connecting to Phillips Hue Bridge";
        }

        private void UpdateDialog(final String msg)
        {
            if(mTextDesc != null)
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
            JSONObject jPost = new JSONObject(String.format("{\"devicetype\":\"%s#%s\"}",Globals.ACTIVITY, Globals.getSharedPreferences().getString("uid", "unknown")));
            ExecutorRequest request = new ExecutorRequest(url, ExecutorRequest.Types.POST);
            request.setPostJson(jPost);

            ExecutorResult result = Executor.fulfil(request);

            if(!result.isSuccess())
                throw result.getError();

            JSONObject jObj = buildJsonResponse(result.getResult());
            if (jObj.has("error")) {
                JSONObject jError = jObj.getJSONObject("error");

                int loopCount = 0;
                int errorCode = jError.getInt("type");
                while (errorCode == 101) {
                    if (loopCount > 36)
                        throw new Exception("Timeout waiting for authorisation push.");

                    UpdateDialog("Please press the link button on the Hue Bridge.");
                    Thread.sleep(5000);

                    result = Executor.fulfil(request);
                    if(!result.isSuccess())
                        throw result.getError();

                    jObj = buildJsonResponse(result.getResult());
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
        public ExecutorResult execute(IThing thing) {
            return null;
        }
    }
}
